package fp.services;

import fp.util.CurationComment;
import fp.util.CurationStatus;
import fp.util.CurrationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


/**
 * Provides support for scientific name validation against IPNI, the International Plant Names Index.
 * 
 * @author Lei Dou
 *
 */
public class IPNIService implements IScientificNameValidationService {
    private boolean useCache;

    public IPNIService(){
	}
			
	public void validateScientificName(String scientificName, String author){
	    validateScientificName(scientificName, author, "", "","","");
	}
	
	/**
	 * @param rank is ignored for this service.
	 */
	public void validateScientificName(String scientificName, String author, String rank, String kingdom, String phylum, String tclass){
		correctedScientificName = null;
		correctedAuthor = null;
		IPNIlsid = null;
		comment = "";
		curationStatus = CurationComment.UNABLE_CURATED;

		//try to find information from the cached file
		//if failed, then access IPNI service or even GNI service
		
		String key = constructKey(scientificName, author);		
		if(useCache && cachedScientificName.containsKey(key)){
			HashMap<String,String> cachedScientificNameInfo = cachedScientificName.get(key);
			
			String expAuthor = cachedScientificNameInfo.get("author");
			if(expAuthor.equals("")){
				//can't be found in either IPNI or GNI
				comment = "Failed to find scientific name in both IPNI and GNI.";
			}else if(expAuthor.equalsIgnoreCase(author)){
				correctedScientificName = scientificName;
				correctedAuthor = author;
				IPNIlsid = constructIPNILSID(cachedScientificNameInfo.get("id")); 
				comment = "The scientific name and authorship are correct.";
				curationStatus = CurationComment.CORRECT;
			}else{
				correctedScientificName = scientificName;
				correctedAuthor = expAuthor;
				IPNIlsid = constructIPNILSID(cachedScientificNameInfo.get("id")); 
				comment = "Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.";
				curationStatus = CurationComment.CURATED;
			}
		}else{
			//try to find it in IPNI service			
			try{
				String source = "";
				String id = simplePlantNameSearch(scientificName, author);
				if(id == null){ 
					//access the GNI and try to get the name that is in the lexical group and from IPNI
					Vector<String> resolvedNameInfo = resolveIPNINameInLexicalGroupFromGNI(scientificName);
					
					if(resolvedNameInfo == null || resolvedNameInfo.size()==0){
						//failed to find it in GNI						
						comment = "Can't find the scientific name and authorship by searching in IPNI and the lexical group from IPNI in GNI.";
					}else{
						//find it in GNI
						String resolvedScientificName = resolvedNameInfo.get(0);
						String resolvedScientificNameAuthorship = resolvedNameInfo.get(1);

						//searching for this name in IPNI again to get the IPNI LSID
						id = simplePlantNameSearch(resolvedScientificName, resolvedScientificNameAuthorship);
						if(id == null){
							//failed to find the name got from GNI in the IPNI
							comment = "Found name which is in the same lexical group as the searched scientific name and from IPNI but failed to find this name really in IPNI.";
						}else{
							//correct the wrong scientific name or author by searching in both IPNI and GNI
							correctedScientificName = resolvedScientificName;
							correctedAuthor = resolvedScientificNameAuthorship;
							IPNIlsid = constructIPNILSID(id); 
							comment = "Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.";
							curationStatus = CurationComment.CURATED;
							source = "IPNI/GNI";
						}
					}					
				}else{
					//get a match by searching in IPNI
					IPNIlsid = constructIPNILSID(id); 
					correctedScientificName = scientificName;
					correctedAuthor = author;
					comment = "The scientific name and authorship are correct.";
					curationStatus = CurationComment.CORRECT;
					source = "IPNI";				
				}				
				
				//write newly found information into hashmap and later write into the cached file if it exists
				if(useCache){
					HashMap<String,String> cachedScientificNameInfo = new HashMap<String,String>();
					
//					if(correctedAuthor == null){
//						cachedScientificNameInfo.put("author", "");
//					}else{
//						cachedScientificNameInfo.put("author", correctedAuthor);
//					}
					
					if(correctedAuthor == null){
						correctedAuthor = "";
					}
					cachedScientificNameInfo.put("author", correctedAuthor);
					
//					if(id == null){
//						cachedScientificNameInfo.put("id", "");
//					}else{
//						cachedScientificNameInfo.put("id", id);
//					}
					
					if(id == null){
						id = "";						
					}	
					cachedScientificNameInfo.put("id", id);
													
					cachedScientificNameInfo.put("source", source);
					
					cachedScientificName.put(key,cachedScientificNameInfo);
					
					newFoundScientificName.add(scientificName);				
					newFoundScientificName.add(author);
					newFoundScientificName.add(correctedAuthor);
					newFoundScientificName.add(id);
					newFoundScientificName.add(source);					
				}
			} catch (Exception ex){
				comment = ex.getMessage();
				curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
				return;
			}
		}		
	}
	
	public CurationStatus getCurationStatus(){
		return curationStatus;
	}

	public String getCorrectedScientificName(){
		return correctedScientificName;
	}
	
	public String getCorrectedAuthor(){
		return correctedAuthor;
	}

	public String getLSID(){
		return IPNIlsid;
	}
	
	public String getComment() {
		return comment;
	}	

	public void setCacheFile(String file) throws CurrationException {
        useCache = true;
		initializeCacheFile(file);
		importFromCache();
	}

	public void flushCacheFile() throws CurrationException {
		if(cacheFile == null){
			return;
		}

		try {
            if (newFoundScientificName == null) {
                System.out.println("Error: newFoundScientificName = null for cache file: "+cacheFile.getAbsolutePath());
            } else {
                //output the newly found information into the cached file
                if(newFoundScientificName.size()>0){
                    BufferedWriter writer  = new BufferedWriter(new FileWriter(cacheFile,true));
                    for(int i=0;i<newFoundScientificName.size();i=i+5){
                        String strLine = "";
                        for(int j=i;j<i+5;j++){
                            if(j>i){
                                strLine = strLine + "\t" ;
                            }
                            strLine = strLine + newFoundScientificName.get(j);
                        }
                        writer.write(strLine+"\n");
                    }
                    writer.close();
                }
            }
		} catch (IOException e) {
			throw new CurrationException(getClass().getName()+" failed to write newly found scientific name information into cached file "+cacheFile.toString()+" since "+e.getMessage());
		}
	}

    public void setUseCache(boolean use) {
        this.useCache = use;
        cachedScientificName = new HashMap<String,HashMap<String,String>>();
        newFoundScientificName = new Vector<String>();
    }

    public String getServiceName() {
		return serviceName;
	}

	private void initializeCacheFile(String fileStr) throws CurrationException{
		cacheFile = new File(fileStr);

		if(!cacheFile.exists()){
			try {
				//If it's the first time to use the cached file and the file doesn't exist now, then create one
				FileWriter writer = new FileWriter(fileStr);
				writer.close();
			} catch (IOException e) {
				throw new CurrationException(getClass().getName()+" failed since the specified data cache file of "+fileStr+" can't be opened successfully for "+e.getMessage());
			}
		}

		if(!cacheFile.isFile()){
			throw new CurrationException(getClass().getName()+" failed since the specified data cache file "+fileStr+" is not a valid file.");
		}
	}

	private void importFromCache() throws CurrationException{
		cachedScientificName = new HashMap<String,HashMap<String,String>>();
		newFoundScientificName = new Vector<String>();

		//read
		try {
			BufferedReader cachedFileReader = new BufferedReader(new FileReader(cacheFile));
			String strLine = cachedFileReader.readLine();
			while(strLine!=null){
				String[] info = strLine.split(ColumnDelimiterInCacheFile,-1);
				if(info.length != 5){
					throw new CurrationException(getClass().getName()+" failed to import data from cached file since some information is missing at: "+strLine);
				}

				String taxon = info[0];
				String author = info[1];
				String expAuthor = info[2];
				String id = info[3];
				String source = info[4];

				HashMap<String,String> valueMap = new HashMap<String,String>();
				valueMap.put("author", expAuthor);
				valueMap.put("id", id);
				valueMap.put("source", source);

				cachedScientificName.put(constructKey(taxon,author), valueMap);

				strLine = cachedFileReader.readLine();
			}
			cachedFileReader.close();
		} catch (FileNotFoundException e) {
			//Since whether the file exist or not has been tested before, this exception should never be reached.
			throw new CurrationException(getClass().getName()+" failed to import data from cached file for "+e.getMessage());
		} catch (IOException e) {
			throw new CurrationException(getClass().getName()+" failed to import data from cached file for "+e.getMessage());
		}
	}

	private String constructKey(String taxon, String author){
		return taxon+" "+author;
	}
	
	private String constructIPNILSID(String id){
		return ipniLSIDPrefix+id;
	}
		
	private String simplePlantNameSearch(String taxon, String author) throws CurrationException {
		String outputFormat = "delimited-minimal";
		
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
        httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);

        List<org.apache.http.NameValuePair> parameters = new ArrayList<org.apache.http.NameValuePair>();
        parameters.add(new BasicNameValuePair("find_wholeName", taxon));
        parameters.add(new BasicNameValuePair("output_format", outputFormat));
		
		try {
            HttpResponse resp;
            HttpPost httpPost = new HttpPost(IPNIurl);
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));
            resp = httpclient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() != 200) {
				throw new CurrationException("IPNIService failed to send request to IPNI for "+resp.getStatusLine().getStatusCode());
			}				
            InputStream response = resp.getEntity().getContent();
			
			//parse the response
			String id  = null;
			String version = "";
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(response));
			String strLine = responseReader.readLine();
			//skip the head
			strLine = responseReader.readLine();
			while(strLine != null){
				String [] info = strLine.split("%");
				if(info.length!=5){
					throw new CurrationException("IPNIService failed in simplePlantNameSearch for " + taxon + "since the returned value doesn't contain valid information.");
				}
				String foundId = info[0].trim();
				String foundVersion = info[1].trim();
				String foundTaxon = info[3].trim();
				String foundAuthor = info[4].trim();
				
				if(foundTaxon.toLowerCase().equals(taxon.toLowerCase()) &&
						foundAuthor.toLowerCase().equals(author.toLowerCase())){
					//found one
					if(version.equals("") || version.compareTo(foundVersion)<=0){
						//the newly found one is more recent
						version = foundVersion;
						id = foundId;
					}
				}
				
				strLine = responseReader.readLine();
			}
			responseReader.close();
			return id;
		} catch (IOException e) {
			throw new CurrationException("IPNIService failed to access IPNI service for "+e.getMessage());
		}				
	}
	
	/**
	 * Special case of finding a name from an authoritative data source in the same lexical group as the supplied
	 * name in GNI's web services. 
	 * 
	 * @param scientificName
	 * @return
	 * @throws CurrationException
	 */
	private Vector<String> resolveIPNINameInLexicalGroupFromGNI(String scientificName) throws CurrationException {
		//get IPNI service Id at the first time 
		if(IPNISourceId == null){
			IPNISourceId = GNISupportingService.getIPNISourceId();
		}
		
		//If GNI doesn't support the data source of IPNI, then do nothing.
		if(IPNISourceId == null){
			return null;
		}
		
		//search name in GNI
		String nameFromIPNIInLexicalGroup = GNISupportingService.searchLexicalGroupInGNI(scientificName,IPNISourceId);
		if(nameFromIPNIInLexicalGroup == null){
			return null;
		}
		
		//parse name into scientific name and author by using the name parsing service in GNI
		return GNISupportingService.parseName(nameFromIPNIInLexicalGroup);
	}
	

	
	public String getFoundKingdom() {
		String result = "";
		if (this.IPNIlsid!=null) {
			return "Plantae";
		}
		return result;
	}

	public String getFoundPhylum() {
		return "";
	}

	public String getFoundOrder() {
		return "";
	}

	public String getFoundClass() {
		return "";
	}

	public String getFoundFamily() {
		return "";
	}	
	
	private File cacheFile = null;
	private HashMap<String, HashMap<String,String>> cachedScientificName;
	private Vector<String> newFoundScientificName;
	private static final String ColumnDelimiterInCacheFile = "\t";

	private CurationStatus curationStatus;
	private String correctedScientificName = null;
	private String correctedAuthor = null;
	private String IPNIlsid = null;	
	private String comment = "";
	
	private String IPNISourceId = null;	
	
	private final static String IPNIurl = "http://www.ipni.org/ipni/simplePlantNameSearch.do";
	private final static String ipniLSIDPrefix = "urn:lsid:ipni.org:names:";
	private final String serviceName = "IPNI";


}

package fp.services;

import edu.harvard.mcz.nametools.ICNafpAuthorNameComparator;
import edu.harvard.mcz.nametools.NameComparison;
import edu.harvard.mcz.nametools.NameUsage;
import fp.util.CurationComment;
import fp.util.CurationStatus;
import fp.util.CurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.io.*;
import java.util.*;


/**
 * Provides support for scientific name validation against IPNI, the International Plant Names Index.
 * 
 * @author Lei Dou
 * @author mole
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
        log = new LinkedList<List>();

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
				List<NameUsage> searchResults = plantNameSearch(scientificName, author);
				String id = handleSearchResults(searchResults);
				if(id != null){
					source = "IPNI";
				} else {
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
						searchResults = plantNameSearch(resolvedScientificName, resolvedScientificNameAuthorship);
						id = handleSearchResults(searchResults);
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

	public void setCacheFile(String file) throws CurationException {
        useCache = true;
		initializeCacheFile(file);
		importFromCache();
	}

	public void flushCacheFile() throws CurationException {
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
			throw new CurationException(getClass().getName()+" failed to write newly found scientific name information into cached file "+cacheFile.toString()+" since "+e.getMessage());
		}
	}

    @Override
    public List<List> getLog() {
        return log;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setUseCache(boolean use) {
        this.useCache = use;
        cachedScientificName = new HashMap<String,HashMap<String,String>>();
        newFoundScientificName = new Vector<String>();
    }

    public String getServiceName() {
		return serviceName;
	}

	private void initializeCacheFile(String fileStr) throws CurationException{
		cacheFile = new File(fileStr);

		if(!cacheFile.exists()){
			try {
				//If it's the first time to use the cached file and the file doesn't exist now, then create one
				FileWriter writer = new FileWriter(fileStr);
				writer.close();
			} catch (IOException e) {
				throw new CurationException(getClass().getName()+" failed since the specified data cache file of "+fileStr+" can't be opened successfully for "+e.getMessage());
			}
		}

		if(!cacheFile.isFile()){
			throw new CurationException(getClass().getName()+" failed since the specified data cache file "+fileStr+" is not a valid file.");
		}
	}

	private void importFromCache() throws CurationException{
		cachedScientificName = new HashMap<String,HashMap<String,String>>();
		newFoundScientificName = new Vector<String>();

		//read
		try {
			BufferedReader cachedFileReader = new BufferedReader(new FileReader(cacheFile));
			String strLine = cachedFileReader.readLine();
			while(strLine!=null){
				String[] info = strLine.split(ColumnDelimiterInCacheFile,-1);
				if(info.length != 5){
					throw new CurationException(getClass().getName()+" failed to import data from cached file since some information is missing at: "+strLine);
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
			throw new CurationException(getClass().getName()+" failed to import data from cached file for "+e.getMessage());
		} catch (IOException e) {
			throw new CurationException(getClass().getName()+" failed to import data from cached file for "+e.getMessage());
		}
	}

	private String constructKey(String taxon, String author){
		return taxon+" "+author;
	}
	
	private String constructIPNILSID(String id){
		return ipniLSIDPrefix+id;
	}
	
	/**
	 * Query IPNI's simple plant name search with a scientificName, obtain a list of zero to 
	 * many matches where the authorship is an exact match (ignoring spaces), or is a matching
	 * abbreviation of the provided authorship.
	 * 
	 * @param taxon the scientific name to check
	 * @param author the author to find amongst the results
	 * @return a list of NameUsage instances on for each case of a matching name and authorship
	 * @throws CurationException
	 */
	public List<NameUsage> plantNameSearch(String taxon, String author) throws CurationException { 
		List<NameUsage> result = new ArrayList<NameUsage>();
		
		String outputFormat = "delimited-minimal";
        long starttime = System.currentTimeMillis();
		
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
				throw new CurationException("IPNIService failed to send request to IPNI for "+resp.getStatusLine().getStatusCode());
			}				
            InputStream response = resp.getEntity().getContent();
			
			//parse the response
			String id  = null;
			String version = "";
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(response));
			//skip the head
			String strLine = responseReader.readLine();
			while( (strLine = responseReader.readLine())!=null ){
				String [] info = strLine.split("%");
				if(info.length!=5){
					throw new CurationException("IPNIService failed in simplePlantNameSearch for " + taxon + "since the returned value doesn't contain valid information.");
				}
				String foundId = info[0].trim();
				String foundVersion = info[1].trim();
				String foundTaxon = info[3].trim();
				String foundAuthor = info[4].trim();
				NameUsage usage = new NameUsage();
				usage.setKey(Integer.parseInt(foundId.replaceAll("[^0-9]", "")));
				usage.setScientificName(foundTaxon);
                usage.setAuthorship(foundAuthor);
                usage.setGuid(constructIPNILSID(foundId));
                usage.setAuthorComparator(new ICNafpAuthorNameComparator(.75d,.5d));
                usage.setOriginalAuthorship(author);
                usage.setOriginalScientificName(taxon);
                NameComparison comparison = usage.getAuthorComparator().compare(author, foundAuthor);
                usage.setAuthorshipStringEditDistance(comparison.getSimilarity());
                usage.setMatchDescription(comparison.getMatchType());
				if(     foundTaxon.toLowerCase().equals(taxon.toLowerCase().trim()) 
						&&
						(
						  comparison.getMatchType().equals(NameComparison.MATCH_EXACT)
						  ||
						  comparison.getSimilarity()==1d
						  ||
						  comparison.getMatchType().equals(NameComparison.MATCH_SAMEBUTABBREVIATED)
						  ||
						  comparison.getMatchType().equals(NameComparison.MATCH_AUTHSIMILAR)
						  ||
						  comparison.getMatchType().equals(NameComparison.MATCH_ADDSAUTHOR)
			            )
					)
				{
					//found one
		            result.add(usage);
					if(version.equals("") || version.compareTo(foundVersion)<=0){
						//the newly found one is more recent
						version = foundVersion;
						id = foundId;
					}
				}
				
				strLine = responseReader.readLine();
			}
			responseReader.close();
            httpPost.releaseConnection();
            List l = new LinkedList();
            l.add(this.getClass().getSimpleName());
            l.add(starttime);
            l.add(System.currentTimeMillis());
            l.add(httpPost.toString());
            log.add(l);
		} catch (IOException e) {
			throw new CurationException("IPNIService failed to access IPNI service for "+e.getMessage());
		}			
		
		return result;
	}	
	
    protected String handleSearchResults(List<NameUsage> searchResults) { 
		String id = null;
		if (searchResults.size()==1) { 
			NameUsage match = searchResults.get(0);
			int iid = match.getKey();
			id = Integer.toString(iid);
			//got one  match by searching in IPNI
			IPNIlsid = match.getGuid(); 
			correctedScientificName = match.getScientificName();
			correctedAuthor = match.getAuthorship();
			if ( match.getMatchDescription().equals(NameComparison.MATCH_EXACT)
					|| match.getAuthorshipStringEditDistance()==1d) { 
			   comment = "The scientific name and authorship are correct.  " + match.getMatchDescription();
			   curationStatus = CurationComment.CORRECT;
			} else if (match.getMatchDescription().equals(NameComparison.MATCH_SAMEBUTABBREVIATED)) { 
			   comment = "The scientific name and authorship are probably correct, but with a different abbreviation for the author.  " + match.getMatchDescription();
			   curationStatus = CurationComment.CORRECT;
			} else if (match.getMatchDescription().equals(NameComparison.MATCH_ADDSAUTHOR)) { 
			   comment = "An authorship is suggested where none was provided.  " + match.getMatchDescription();
			   curationStatus = CurationComment.CURATED;
			} else if (match.getMatchDescription().equals(NameComparison.MATCH_ERROR) 
					|| match.getMatchDescription().equals(NameComparison.MATCH_DISSIMILAR)) {
				// no match to report
				id = null;
			} else { 
		        if (match.getAuthorshipStringEditDistance()>= match.getAuthorComparator().getSimilarityThreshold()) {
				   comment = "Scientific name authorship corrected.  " + match.getMatchDescription() + "  Similarity=" + match.getAuthorshipStringEditDistance();
				   curationStatus = CurationComment.CURATED;
		        } else { 
				   // too weak a match to report
				   id = null;
		        }
			}
		} else if (searchResults.size()>1) {  
		    Iterator<NameUsage> i = searchResults.iterator();
		    boolean done = false;
		    double bestMatch = -1d;
		    while (i.hasNext() && !done) { 
		    	NameUsage match = i.next();
		    	// pick the best match out of the search results.
		    	if (match.getAuthorshipStringEditDistance()>bestMatch) { 
		    		bestMatch = match.getAuthorshipStringEditDistance();
		    	} else { 
		    		if (match.getAuthorshipStringEditDistance()>= match.getAuthorComparator().getSimilarityThreshold()) {
		    			int iid = match.getKey();
		    			id = Integer.toString(iid);
		    			IPNIlsid = match.getGuid(); 
		    			correctedScientificName = match.getScientificName();
		    			correctedAuthor = match.getAuthorship();
		    			comment = "The scientific name and authorship are correct.";
		    			if (match.getMatchDescription().equals(NameComparison.MATCH_EXACT) || match.getAuthorshipStringEditDistance()==1d) { 
		    				comment = "The scientific name and authorship are correct.  " + match.getMatchDescription();
		    				curationStatus = CurationComment.CORRECT;
		    			} else { 
		    				comment = "Scientific name authorship corrected.  " + match.getMatchDescription() + "  Similarity=" + match.getAuthorshipStringEditDistance();
		    				curationStatus = CurationComment.CURATED;
		    			}
		    			if (match.getMatchDescription().equals(NameComparison.MATCH_EXACT)) {
		    				done = true;
		    			}
		    		}
		    	}
		    }
		}
		return id;
    }
  	
	
	/** 
	 * Search IPNI on taxon, return id for a result with an exact match on author.
	 * 
	 * @param taxon
	 * @param author
	 * @return
	 * @throws CurationException
	 */
	public String simplePlantNameSearch(String taxon, String author) throws CurationException {
		String outputFormat = "delimited-minimal";
        long starttime = System.currentTimeMillis();
		
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
				throw new CurationException("IPNIService failed to send request to IPNI for "+resp.getStatusLine().getStatusCode());
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
					throw new CurationException("IPNIService failed in simplePlantNameSearch for " + taxon + "since the returned value doesn't contain valid information.");
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
            httpPost.releaseConnection();
            List l = new LinkedList();
            l.add(this.getClass().getSimpleName());
            l.add(starttime);
            l.add(System.currentTimeMillis());
            l.add(httpPost.toString());
            log.add(l);
			return id;
		} catch (IOException e) {
			throw new CurationException("IPNIService failed to access IPNI service for "+e.getMessage());
		}				
	}
	
	/**
	 * Special case of finding a name from an authoritative data source in the same lexical group as the supplied
	 * name in GNI's web services. 
	 * 
	 * @param scientificName
	 * @return
	 * @throws CurationException
	 */
	private Vector<String> resolveIPNINameInLexicalGroupFromGNI(String scientificName) throws CurationException {
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
    private List<List> log = new LinkedList<List>();

	private CurationStatus curationStatus;
	private String correctedScientificName = null;
	private String correctedAuthor = null;
	private String IPNIlsid = null;	
	private String comment = "";
	
	private String IPNISourceId = null;	


	private final static String IPNIurl = "http://www.ipni.org/ipni/simplePlantNameSearch.do";
    //private final static String IPNIurl = "http://lore.genomecenter.ucdavis.edu/cache/ipni.php";
    //private final static String IPNIurl = "http://localhost/cache/ipni.php";
	private final static String ipniLSIDPrefix = "urn:lsid:ipni.org:names:";
	private final String serviceName = "IPNI";

	/**
	 * @param correctedScientificName the correctedScientificName to set
	 */
	public void setCorrectedScientificName(String correctedScientificName) {
		this.correctedScientificName = correctedScientificName;
	}

}

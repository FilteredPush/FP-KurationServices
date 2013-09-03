package fp.services;

import fp.util.CurationComment;
import fp.util.CurationStatus;
import fp.util.CurrationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.dom4j.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Provides support for scientific name validation against IndexFungorum.
 * 
 * @author Lei Dou
 * @author Paul J. Morris
 *
 */
public class IndexFungorumService implements IScientificNameValidationService {
    private boolean useCache = false;

    public IndexFungorumService(){
	}
			
	public void validateScientificName(String scientificName, String author){
	    validateScientificName(scientificName, author, "", "","","");
	}
	
	/**
	 * 
	 * @param rank is ignored for this service.
	 */
	public void validateScientificName(String scientificName, String author, String rank, String kingdom, String phylum, String tclass){
		correctedScientificName = null;
		correctedAuthor = null;
		IFlsid = null;
		comment = "";
		curationStatus = CurationComment.UNABLE_CURATED;

		//try to find information from the cached file
		//if failed, then access IF service or even GNI service
		
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
				IFlsid = constructIPNILSID(cachedScientificNameInfo.get("id")); 
				comment = "The scientific name and authorship are correct.";
				curationStatus = CurationComment.CORRECT;
			}else{
				correctedScientificName = scientificName;
				correctedAuthor = expAuthor;
				IFlsid = constructIPNILSID(cachedScientificNameInfo.get("id")); 
				comment = "Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.";
				curationStatus = CurationComment.CURATED;
			}
		}else{
			//try to find it in IPNI service			
			try{
				String source = "";
				String id = simpleFungusNameSearch(scientificName, author);
				if(id == null){
					
					System.out.println("Checking GNI for " + scientificName );
					
					//access the GNI and try to get the name that is in the lexical group and from IF
					Vector<String> resolvedNameInfo = resolveIFNameInLexicalGroupFromGNI(scientificName);
					
					if(resolvedNameInfo == null || resolvedNameInfo.size()==0){
						//failed to find it in GNI						
						comment = "Can't find the scientific name and authorship by searching in IPNI and the lexical group from IPNI in GNI.";
					}else{
						//find it in GNI
						String resolvedScientificName = resolvedNameInfo.get(0);
						String resolvedScientificNameAuthorship = resolvedNameInfo.get(1);
						
						
					    System.out.println("Found in GNI " +  resolvedScientificName + " " + resolvedScientificNameAuthorship );

						//searching for this name in IPNI again to get the IPNI LSID
						id = simpleFungusNameSearch(resolvedScientificName, resolvedScientificNameAuthorship);
						if(id == null){
							//failed to find the name got from GNI in the IPNI
							comment = "Found name which is in the same lexical group as the searched scientific name and from IPNI but failed to find this name really in IPNI.";
						}else{
							//correct the wrong scientific name or author by searching in both IPNI and GNI
							correctedScientificName = resolvedScientificName;
							correctedAuthor = resolvedScientificNameAuthorship;
							IFlsid = constructIPNILSID(id); 
							comment = "Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.";
							curationStatus = CurationComment.CURATED;
							source = "IPNI/GNI";
						}
					}					
				}else{
					//get a match by searching in IPNI
					IFlsid = constructIPNILSID(id); 
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
			}catch(CurrationException ex){
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
		return IFlsid;
	}
	
	public String getComment() {
		return comment;
	}	
	
	public void setCacheFile(String file) throws CurrationException {
		initializeCacheFile(file);		
		importFromCache();
	}
	
	public void flushCacheFile() throws CurrationException{
		if(cacheFile == null){
			return;
		}
		
		try {
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
		} catch (IOException e) {
			throw new CurrationException(getClass().getName()+" failed to write newly found scientific name information into cached file "+cacheFile.toString()+" since "+e.getMessage());
		}		
	}

    public void setUseCache(boolean use) {
        this.useCache = true;
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
		return IF_LSIDPrefix+id;
	}
		
	/** 
	 * Search against IndexFungorum NameSearch.
	 * 
	 * http://www.indexfungorum.org/IXFWebService/Fungus.asmx?op=NameSearch
	 * 
	 * @param taxon to search for
	 * @param author for name
	 * @return IndexFungorum ID if found, otherwise null;
	 * @throws CurrationException
	 */
	private String simpleFungusNameSearch(String taxon, String author) throws CurrationException{
	    // http://www.indexfungorum.org/IXFWebService/Fungus.asmx/NameSearch?SearchText=string&AnywhereInText=string&MaxNumber=string
        org.apache.http.client.HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
        httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);

        List<org.apache.http.NameValuePair> parameters = new ArrayList<org.apache.http.NameValuePair>();
        parameters.add(new BasicNameValuePair("AnywhereInText", "false"));
        parameters.add(new BasicNameValuePair("MaxNumber", "10"));
        parameters.add(new BasicNameValuePair("SearchText", taxon));
		try {
            HttpResponse resp;
            HttpPost httpPost = new HttpPost(IFurl);
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));
            resp = httpclient.execute(httpPost);
            long statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                throw new CurrationException("IFService failed to send request to IndexFungorum for "+statusCode);
            }
            InputStream response = resp.getEntity().getContent();
			
			//parse the response
			String id  = null;
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			try {

				//Using factory get an instance of document builder
				DocumentBuilder db = dbf.newDocumentBuilder();
				
				org.w3c.dom.Document dom = db.parse(response);
				
				Element root = dom.getDocumentElement();
				NodeList nodeList = root.getElementsByTagName("IndexFungorum");
				System.out.println(nodeList.toString() + " " + nodeList.getLength());
				if (nodeList==null)  {
					throw new CurrationException("IndexFungorumService failed in NameSearch for " + taxon + ".");
				} else {
                   for (int i=0; i<nodeList.getLength(); i++) {
                	   org.w3c.dom.Node node = nodeList.item(i);
            		   if (node.getNodeType() == Node.ELEMENT_NODE) {
            		      Element element = (Element) node;
            		      try { 
            		      String foundId = element.getElementsByTagName("RECORD_x0020_NUMBER").item(0).getChildNodes().item(0).getNodeValue();
            		      String foundTaxon = element.getElementsByTagName("NAME_x0020_OF_x0020_FUNGUS").item(0).getChildNodes().item(0).getNodeValue();
            		      String foundAuthor = element.getElementsByTagName("AUTHORS").item(0).getChildNodes().item(0).getNodeValue();
            		      
            		      System.out.println("Possible IF Match: " + foundId + " " + foundTaxon + " " + foundAuthor);
            		      
     				      // String foundVersion = info[1].trim();  // No Versions in IF.
            		      if(foundTaxon.toLowerCase().equals(taxon.toLowerCase()) &&
            		          foundAuthor.toLowerCase().equals(author.toLowerCase())){
            		    	  //found exact match
    						 id = foundId;
    					  }
            		      } catch (NullPointerException e)  {
            		    	 // IF node with no Record, Name, or Authors.
            		      }
            		   } else { 
					      throw new CurrationException("IndexFungorumService failed in NameSearch for " + taxon + ".  Response not formatted as expected"  );
            		   }
                   } 
				}
				

			}catch(ParserConfigurationException epc) {
				throw new CurrationException("IndexFungorumService failed in name search for " + taxon + "." + epc.getMessage());
			}catch(SAXException es) {
				throw new CurrationException("IndexFungorumService failed in name search for " + taxon + "." + es.getMessage());
			}catch(IOException eio) {
				throw new CurrationException("IndexFungorumService failed in name search for " + taxon + "." + eio.getMessage());
			}			
			
			return id;
		} catch (IOException e) {
			throw new CurrationException("IndexFungorumService failed to access IF service for "+e.getMessage());
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
	private Vector<String> resolveIFNameInLexicalGroupFromGNI(String scientificName) throws CurrationException {
		//get IPNI service Id at the first time 
		if(IFSourceId == null){
			IFSourceId = GNISupportingService.getGNIDataSourceID("Index Fungorum");
		}
		
		System.out.println("IndexFungorum is GNI Datasource: " + IFSourceId);
		
		//If GNI doesn't support the data source of IPNI, then do nothing.
		if(IFSourceId == null){
			return null;
		}
		
		//search name in GNI
		String nameFromIPNIInLexicalGroup = GNISupportingService.searchLexicalGroupInGNI(scientificName,IFSourceId);
		if(nameFromIPNIInLexicalGroup == null){
			return null;
		}
		
		//parse name into scientific name and author by using the name parsing service in GNI
		return GNISupportingService.parseName(nameFromIPNIInLexicalGroup);
	}
	
	public String getFoundKingdom() {
		String result = "";
		if (this.IFlsid!=null) {
			return "Fungi";
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
	private String IFlsid = null;	
	private String comment = "";
	
	private String IFSourceId = null;	
	
	// http://www.indexfungorum.org/IXFWebService/Fungus.asmx/NameSearch?SearchText=string&AnywhereInText=string&MaxNumber=string
	
	private final static String IFurl = "http://www.indexfungorum.org/IXFWebService/Fungus.asmx/NameSearch";

	private final static String IF_LSIDPrefix = "urn:lsid:indexfungorum.org:names:";
	
	private final String serviceName = "IndexFungorum";


}

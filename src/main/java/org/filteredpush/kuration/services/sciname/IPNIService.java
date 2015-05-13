package org.filteredpush.kuration.services.sciname;

import edu.harvard.mcz.nametools.AuthorNameComparator;
import edu.harvard.mcz.nametools.ICNafpAuthorNameComparator;
import edu.harvard.mcz.nametools.NameComparison;
import edu.harvard.mcz.nametools.NameUsage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationException;
import org.filteredpush.kuration.util.CurationStatus;

import java.io.*;
import java.util.*;


/**
 * Provides support for scientific name validation against IPNI, the International Plant Names Index.
 * 
 * @author Lei Dou
 * @author mole
 *
 */
public class IPNIService extends SciNameServiceParent {
	
	private static final Log logger = LogFactory.getLog(IPNIService.class);
	
	private final static String IPNIurl = "http://www.ipni.org/ipni/simplePlantNameSearch.do";
    //private final static String IPNIurl = "http://lore.genomecenter.ucdavis.edu/cache/ipni.php";
    //private final static String IPNIurl = "http://localhost/cache/ipni.php";
	private final static String ipniLSIDPrefix = "urn:lsid:ipni.org:names:";
	private final String serviceName = "IPNI";
	
    private boolean useCache = false;
    
	private File cacheFile = null;
	private HashMap<String, HashMap<String,String>> cachedScientificName;
	private Vector<String> newFoundScientificName;
	private static final String ColumnDelimiterInCacheFile = "\t";
    private List<List> log = new LinkedList<List>();

	private String IPNIlsid = null;	
	
	private String IPNISourceId = null;	
    
    public IPNIService(){
    	init();
	}
			
    protected void init() { 
		validatedNameUsage = new NameUsage("IPNI",new ICNafpAuthorNameComparator(.70d, .5d));
    }
    

	public String getLSID(){
		return IPNIlsid;
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
        httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,25000);
        httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,40000);
        httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,"Mozilla/5.0 (X11; Linux i686; rv:34.0) Gecko/20100101 Firefox/34.0");

        List<org.apache.http.NameValuePair> parameters = new ArrayList<org.apache.http.NameValuePair>();
        parameters.add(new BasicNameValuePair("find_wholeName", taxon));
        parameters.add(new BasicNameValuePair("output_format", outputFormat));
        parameters.add(new BasicNameValuePair("query_type", "by_query"));  // &query_type=by_query&back_page=query_ipni.html
        parameters.add(new BasicNameValuePair("back_page", "query_ipni.html"));
		
		try {
            HttpResponse resp;
            HttpPost httpPost = new HttpPost(IPNIurl);
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));
            resp = httpclient.execute(httpPost);
            logger.debug(resp.getStatusLine());
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
            logger.debug(strLine);			
			while( (strLine = responseReader.readLine())!=null ){
	            logger.debug(strLine);			
				String [] info = strLine.split("%");
				if(info.length!=5){
					throw new CurationException("IPNIService failed in simplePlantNameSearch for " + taxon + "since the returned value doesn't contain valid information.");
				}
				String foundId = info[0].trim();
				String foundVersion = info[1].trim();
				String foundTaxon = info[3].trim();
				String foundAuthor = info[4].trim();
	            logger.debug(foundTaxon);				
	            logger.debug(foundAuthor);				
				NameUsage usage = new NameUsage();
				usage.setKey(Integer.parseInt(foundId.replaceAll("[^0-9]", "")));
				usage.setScientificName(foundTaxon);
                usage.setAuthorship(foundAuthor);
                usage.setGuid(constructIPNILSID(foundId));
                usage.setAuthorComparator(new ICNafpAuthorNameComparator(.70d,.5d));
                usage.setOriginalAuthorship(author);
                usage.setOriginalScientificName(taxon);
                NameComparison comparison = usage.getAuthorComparator().compare(author, foundAuthor);
                logger.debug(comparison.getSimilarity());
                usage.setAuthorshipStringEditDistance(comparison.getSimilarity());
                usage.setMatchDescription(comparison.getMatchType());
				logger.debug(usage.getMatchDescription());
				logger.debug(usage.getAuthorshipStringEditDistance());
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
		            logger.debug("Matched");	
		            result.add(usage);
					if(version.equals("") || version.compareTo(foundVersion)<=0){
						//the newly found one is more recent
						version = foundVersion;
						id = foundId;
			            logger.debug(id);						
					}
				}
				
			}
			responseReader.close();
            httpPost.releaseConnection();
            List l = new LinkedList();
            l.add(this.getClass().getSimpleName());
            l.add(starttime);
            l.add(System.currentTimeMillis());
            l.add(httpPost.toString());
            log.add(l);
            logger.debug(id);						
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
			String correctedScientificName = match.getScientificName();
			String correctedAuthor = match.getAuthorship();
			validatedNameUsage.setScientificName(correctedScientificName);
			validatedNameUsage.setAuthorship(correctedAuthor);
			validatedNameUsage.setGuid(IPNIlsid);
			if ( match.getMatchDescription().equals(NameComparison.MATCH_EXACT)
					|| match.getAuthorshipStringEditDistance()==1d) { 
				addToComment("The scientific name and authorship are correct.  " + match.getMatchDescription());
			   curationStatus = CurationComment.CORRECT;
			} else if (match.getMatchDescription().equals(NameComparison.MATCH_SAMEBUTABBREVIATED)) { 
				addToComment("The scientific name and authorship are probably correct, but with a different abbreviation for the author.  " + match.getMatchDescription());
			   curationStatus = CurationComment.CORRECT;
			} else if (match.getMatchDescription().equals(NameComparison.MATCH_ADDSAUTHOR)) { 
				addToComment("An authorship is suggested where none was provided.  " + match.getMatchDescription());
			   curationStatus = CurationComment.CURATED;
			} else if (match.getMatchDescription().equals(NameComparison.MATCH_ERROR) 
					|| match.getMatchDescription().equals(NameComparison.MATCH_DISSIMILAR)) {
				// no match to report
				id = null;
			} else { 
		        if (match.getAuthorshipStringEditDistance()>= match.getAuthorComparator().getSimilarityThreshold()) {
		        	addToComment("Scientific name authorship corrected.  " + match.getMatchDescription() + "  Similarity=" + match.getAuthorshipStringEditDistance());
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
		    		if (match.getAuthorshipStringEditDistance()>= match.getAuthorComparator().getSimilarityThreshold()) {
		    			int iid = match.getKey();
		    			id = Integer.toString(iid);
		    			IPNIlsid = match.getGuid(); 
		    			validatedNameUsage.setScientificName(match.getScientificName());
		    			validatedNameUsage.setAuthorship(match.getAuthorship());
		    			validatedNameUsage.setGuid(IPNIlsid);
		    			addToComment("The scientific name and authorship are correct.");
		    			if (match.getMatchDescription().equals(NameComparison.MATCH_EXACT) || match.getAuthorshipStringEditDistance()==1d) { 
		    				addToComment("The scientific name and authorship are correct.  " + match.getMatchDescription());
		    				curationStatus = CurationComment.CORRECT;
		    			} else { 
		    				addToComment("Scientific name authorship corrected.  " + match.getMatchDescription() + "  Similarity=" + match.getAuthorshipStringEditDistance());
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
  	
	
	public String simplePlantNameSearch(String taxon, String author) throws CurationException {
		String outputFormat = "delimited-minimal";
        long starttime = System.currentTimeMillis();
		
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,25000);
        httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);

        List<org.apache.http.NameValuePair> parameters = new ArrayList<org.apache.http.NameValuePair>();
        parameters.add(new BasicNameValuePair("find_wholeName", taxon));
        parameters.add(new BasicNameValuePair("output_format", outputFormat));
		
		try {
            HttpResponse resp;
            HttpPost httpPost = new HttpPost(IPNIurl);
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));
            resp = httpclient.execute(httpPost);
            logger.debug(resp.getStatusLine());
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
            logger.debug(strLine);			
			while( (strLine = responseReader.readLine())!=null ){
	            logger.debug(strLine);			
				String [] info = strLine.split("%");
				if(info.length!=5){
					throw new CurationException("IPNIService failed in simplePlantNameSearch for " + taxon + "since the returned value doesn't contain valid information.");
				}
				String foundId = info[0].trim();
				String foundVersion = info[1].trim();
				String foundTaxon = info[3].trim();
				String foundAuthor = info[4].trim();
	            logger.debug(foundTaxon);				
	            logger.debug(foundAuthor);				
				
				if(foundTaxon.toLowerCase().equals(taxon.toLowerCase().trim()) &&
						foundAuthor.toLowerCase().equals(author.toLowerCase().trim())){
					//found one
		            logger.debug("Matched");					
					if(version.equals("") || version.compareTo(foundVersion)<=0){
						//the newly found one is more recent
						version = foundVersion;
						id = foundId;
			            logger.debug(id);						
					}
				}
			}
			responseReader.close();
            httpPost.releaseConnection();
            List l = new LinkedList();
            l.add(this.getClass().getSimpleName());
            l.add(starttime);
            l.add(System.currentTimeMillis());
            l.add(httpPost.toString());
            log.add(l);
            logger.debug(id);						
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
	

	@Override
	public AuthorNameComparator getAuthorNameComparator(String authorship,
			String kingdom) {
		return new ICNafpAuthorNameComparator(.75d, .5d);
	}

	@Override
	protected boolean nameSearchAgainstServices(NameUsage toCheck) {
		boolean result = false;
		validatedNameUsage.setOriginalAuthorship(toCheck.getOriginalAuthorship());
		validatedNameUsage.setOriginalScientificName(toCheck.getOriginalScientificName());
		comment = new StringBuffer();
		curationStatus = CurationComment.UNABLE_CURATED;
        log = new LinkedList<List>();
        
		String scientificName = toCheck.getOriginalScientificName();
		String author = toCheck.getOriginalAuthorship();

		//try to find information from the cached file
		//if failed, then access IPNI service or even GNI service
        
		String key = constructKey(scientificName, author);		
		if(useCache && cachedScientificName.containsKey(key)){
			HashMap<String,String> cachedScientificNameInfo = cachedScientificName.get(key);
			
			String expAuthor = cachedScientificNameInfo.get("author");
			if(expAuthor.equals("")){
				//can't be found in either IPNI or GNI
				addToComment("Failed to find scientific name in either IPNI or GNI.");
			} else { 
				String correctedAuthor = "";
				if(expAuthor.equalsIgnoreCase(author)){
					correctedAuthor = author;
					IPNIlsid = constructIPNILSID(cachedScientificNameInfo.get("id")); 
					addToComment("The scientific name and authorship are correct.");
					curationStatus = CurationComment.CORRECT;
					result = true;
				} else{
					correctedAuthor = expAuthor;
					IPNIlsid = constructIPNILSID(cachedScientificNameInfo.get("id")); 
					addToComment("Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.");
					curationStatus = CurationComment.CURATED;
					result = true;
				}
				validatedNameUsage.setScientificName(scientificName);
				validatedNameUsage.setAuthorship(correctedAuthor);
				validatedNameUsage.setGuid(IPNIlsid);
			}
		} else {
			//try to find it in IPNI service			
			try{
				String source = "";
				List<NameUsage> searchResults = plantNameSearch(scientificName, author);
				String id = handleSearchResults(searchResults);
				if(id != null){
				    source = "IPNI";
				    result = true;
				} else {
					//access the GNI and try to get the name that is in the lexical group and from IPNI
					Vector<String> resolvedNameInfo = resolveIPNINameInLexicalGroupFromGNI(scientificName);
					
					if(resolvedNameInfo == null || resolvedNameInfo.size()==0){
						//failed to find it in GNI						
						addToComment("Can't find the scientific name and authorship by searching in IPNI or in lexical group from IPNI in GNI.");
					} else {
						//found it in GNI
						String resolvedScientificName = resolvedNameInfo.get(0);
						String resolvedScientificNameAuthorship = resolvedNameInfo.get(1);

						//searching for this name in IPNI again to get the IPNI LSID
						searchResults = plantNameSearch(resolvedScientificName, resolvedScientificNameAuthorship);
						id = handleSearchResults(searchResults);
						if(id == null){
							//failed to find the name got from GNI in the IPNI
							addToComment("Found a name which is in the same lexical group as the searched scientific name and claimed by GNI to be in IPNI but failed to find this name in IPNI.");
						}else{
							//correct the wrong scientific name or author by searching in both IPNI and GNI
							IPNIlsid = constructIPNILSID(id); 
							addToComment("Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.");
							curationStatus = CurationComment.CURATED;
							source = "IPNI/GNI";
							result = true;
							
							validatedNameUsage.setScientificName(resolvedScientificName);
							validatedNameUsage.setAuthorship(resolvedScientificNameAuthorship);
							validatedNameUsage.setOriginalAuthorship(toCheck.getOriginalAuthorship());
							validatedNameUsage.setOriginalScientificName(toCheck.getOriginalScientificName());
							validatedNameUsage.setGuid(IPNIlsid);
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
					
					cachedScientificNameInfo.put("author", validatedNameUsage.getScientificName());
					
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
					newFoundScientificName.add(validatedNameUsage.getAuthorship());
					newFoundScientificName.add(id);
					newFoundScientificName.add(source);					
				}
			} catch (Exception ex){
				addToComment(ex.getMessage());
				curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
			}
		}		
		return result;
	}

}

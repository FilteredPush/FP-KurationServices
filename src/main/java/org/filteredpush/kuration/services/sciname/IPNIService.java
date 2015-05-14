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
import org.filteredpush.kuration.util.SciNameCacheValue;

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
                logger.debug(author);
                logger.debug(foundAuthor);
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
			logger.debug(match.getGuid());
			logger.debug(match.getMatchDescription());
			if ( match.getMatchDescription().equals(NameComparison.MATCH_EXACT)
					|| match.getAuthorshipStringEditDistance()==1d) { 
				addToComment("The scientific name and authorship are correct.  " + match.getMatchDescription());
			   curationStatus = CurationComment.CORRECT;
			} else if (match.getMatchDescription().equals(NameComparison.MATCH_SAMEBUTABBREVIATED)) { 
				addToComment("The scientific name and authorship are probably correct, but with a different abbreviation for the author (" + match.getOriginalAuthorship() + " vs. " + correctedAuthor + ").  " + match.getMatchDescription());
			   curationStatus = CurationComment.CORRECT;
			} else if (match.getMatchDescription().equals(NameComparison.MATCH_ADDSAUTHOR)) { 
				addToComment("An authorship (" + correctedAuthor + ") is suggested where none was provided.  " + match.getMatchDescription());
			   curationStatus = CurationComment.CURATED;
			} else if (match.getMatchDescription().equals(NameComparison.MATCH_ERROR) 
					|| match.getMatchDescription().equals(NameComparison.MATCH_DISSIMILAR)) {
				// no match to report
				addToComment("No match was found in IPNI.  " + match.getMatchDescription());
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
        
		String scientificName = toCheck.getOriginalScientificName();
		String author = toCheck.getOriginalAuthorship();
		
        String key = getKey(scientificName, author);
        if(useCache && sciNameCache.containsKey(key)){
            SciNameCacheValue hitValue = (SciNameCacheValue) sciNameCache.get(key);
            addToComment(hitValue.getComment());
            curationStatus = hitValue.getStatus();
            addToServiceName(hitValue.getSource());
            this.validatedNameUsage.setAuthorship(hitValue.getAuthor());
            this.validatedNameUsage.setScientificName(hitValue.getTaxon());
            //System.out.println("count  = " + count++);
            //System.out.println(key);
            return hitValue.getHasResult();
        }		

		//Try to find name in access IPNI failing over to GNI service
        
			//try to find it in IPNI service			
			try{
				String source = "";
				List<NameUsage> searchResults = plantNameSearch(scientificName, author);
				String id = handleSearchResults(searchResults);
				if(id != null){
				    source = "IPNI";
				    result = true;
				    logger.debug(id);
				    logger.debug(curationStatus.toString());
					addToComment("Found name in IPNI.");
				} else {
					addToComment("Didn't find name in IPNI.");
				    logger.debug(id);
				    logger.debug(curationStatus.toString());
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
							addToComment("Found a name " + resolvedScientificName + " " + resolvedScientificNameAuthorship +" which is in the same lexical group as the searched scientific name and claimed by GNI to be in IPNI but failed to find this name in IPNI.");
						}else{
							//correct the wrong scientific name or author by searching in both IPNI and GNI
							IPNIlsid = constructIPNILSID(id); 
							addToComment("Updated the scientific name (including authorship) with value found in GNI which is from IPNI and in the same lexicalgroup as the original term.  This is likely to represent an alternative form of the authorship than the authorship provided.");
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
				
			} catch (Exception ex){
				addToComment(ex.getMessage());
				curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
			}
			if (!result) { 
				addToComment("No match found in IPNI with failover to GNI.");
			}			
		return result;
	}

	@Override
	protected String getServiceImplementationName() {
		return "IPNI";
	}

}

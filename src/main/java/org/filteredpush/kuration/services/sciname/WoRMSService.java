package org.filteredpush.kuration.services.sciname;

import edu.harvard.mcz.nametools.AuthorNameComparator;
import edu.harvard.mcz.nametools.ICZNAuthorNameComparator;
import edu.harvard.mcz.nametools.NameComparison;
import edu.harvard.mcz.nametools.NameUsage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationException;
import org.filteredpush.kuration.util.CurationStatus;
import org.marinespecies.aphia.v1_0.AphiaNameServicePortTypeProxy;
import org.marinespecies.aphia.v1_0.AphiaRecord;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Provides support for scientific name validation against the WoRMS 
 * (World Register of Marine Species) Aphia web service. 
 * See: http://www.marinespecies.org/aphia.php?p=webservice
 * 
 * @author Lei Dou
 * @author Paul J. Morris
 *
 */
public class WoRMSService extends SciNameServiceParent {
    private boolean useCache = false;

	private static final Log log = LogFactory.getLog(WoRMSService.class);
	
	private AphiaNameServicePortTypeProxy wormsService;
	protected AuthorNameComparator authorNameComparator;
	
	protected int depth;
	
	private File cacheFile = null;
	private HashMap<String, HashMap<String,String>> cachedScientificName;
	private Vector<String> newFoundScientificName;
	private static final String ColumnDelimiterInCacheFile = "\t";

	private CurationStatus curationStatus;
	private String correctedScientificName = null;
	private String correctedAuthor = null;
	private String WoRMSlsid = null;	
	private String comment = "";

	private String foundKingdom = null;
	private String foundPhylum = null;
	private String foundClass = null;
	private String foundOrder = null;
	private String foundFamily = null;	

	private String wormsSourceId = null;	

	private final static String wormsLSIDPrefix = "urn:lsid:marinespecies.org:taxname:";

	private final String serviceName = "WoRMS";	
	
	public WoRMSService() throws IOException { 
			init();
			test();
	}
	
	protected void test()  throws IOException { 
		log.debug(wormsService.getEndpoint());
		URL test = new URL(wormsService.getEndpoint());
		URLConnection conn = test.openConnection();
		conn.connect();
	}

    protected void init() { 
		authorNameComparator = new ICZNAuthorNameComparator(.75d,.5d);
		validatedNameUsage = new NameUsage("WoRMS",authorNameComparator);
		depth = 0;
		wormsService = new AphiaNameServicePortTypeProxy();
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
		WoRMSlsid = null;
		comment = "";
		curationStatus = CurationComment.UNABLE_CURATED;

		//try to find information from the cached file
		//if failed, then access WoRMS Aphia service or even GNI service

		String key = constructKey(scientificName, author);		
		if(useCache && cachedScientificName.containsKey(key)){
			HashMap<String,String> cachedScientificNameInfo = cachedScientificName.get(key);

			String expAuthor = cachedScientificNameInfo.get("author");
			if(expAuthor.equals("")){
				//can't be found in either WoRMS or GNI
				comment = "Failed to find scientific name in both WoRMS and GNI.";
			}else if(expAuthor.equalsIgnoreCase(author)){
				correctedScientificName = scientificName;
				correctedAuthor = author;
				WoRMSlsid = constructIPNILSID(cachedScientificNameInfo.get("id")); 
				comment = "The scientific name and authorship are correct.";
				curationStatus = CurationComment.CORRECT;
			}else{
				correctedScientificName = scientificName;
				correctedAuthor = expAuthor;
				WoRMSlsid = constructIPNILSID(cachedScientificNameInfo.get("id")); 
				comment = "Updated the scientific name (including authorship) with term found in GNI which is from WoRMS and in the same lexicalgroup as the original term.";
				curationStatus = CurationComment.CURATED;
			}
		}else{
			//try to find it in WoRMS Aphia service			
			try{
				String source = "";
				String id = simpleNameSearch(scientificName, author);
				if(id == null){ 
					// Note that WoRMS also has a fuzzy matching service.  Could implement here.


					//access the GNI and try to get the name that is in the lexical group and from WoRMS
					Vector<String> resolvedNameInfo = resolveIPNINameInLexicalGroupFromGNI(scientificName);

					if(resolvedNameInfo == null || resolvedNameInfo.size()==0){
						//failed to find it in GNI						
						comment = "Can't find the scientific name and authorship by searching in IPNI and the lexical group from IPNI in GNI.";
					}else{
						//find it in GNI
						String resolvedScientificName = resolvedNameInfo.get(0);
						String resolvedScientificNameAuthorship = resolvedNameInfo.get(1);

						//searching for this name in IPNI again to get the IPNI LSID
						id = simpleNameSearch(resolvedScientificName, resolvedScientificNameAuthorship);
						if(id == null){
							//failed to find the name got from GNI in the IPNI
							comment = "Found name which is in the same lexical group as the searched scientific name and from IPNI but failed to find this name really in IPNI.";
						}else{
							//correct the wrong scientific name or author by searching in both IPNI and GNI
							correctedScientificName = resolvedScientificName;
							correctedAuthor = resolvedScientificNameAuthorship;
							WoRMSlsid = constructIPNILSID(id); 
							comment = "Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.";
							curationStatus = CurationComment.CURATED;
							source = "IPNI/GNI";
						}
					}					
				}else{
					//get a match by searching in IPNI
					WoRMSlsid = constructIPNILSID(id); 
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
			}catch(CurationException ex){
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
		return WoRMSlsid;
	}

	public String getComment() {
		return comment;
	}	

	public void setCacheFile(String file) throws CurationException{
		initializeCacheFile(file);		
		importFromCache();
        this.useCache = true;
	}

	public void flushCacheFile() throws CurationException{
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
		return wormsLSIDPrefix+id;
	}

	/**
	 * Find a taxon name record in WoRMS.
	 * 
	 * @param taxon name to look for
	 * @param author authority to look for
	 * @return aphia id for the taxon
	 * @throws CurationException
	 */
	private String simpleNameSearch(String taxon, String author) throws CurationException{
		String id  = null;

		AphiaNameServicePortTypeProxy wormsService = new AphiaNameServicePortTypeProxy();

		boolean marineOnly = false;
		try {
			int taxonid = wormsService.getAphiaID(taxon, marineOnly);

			String foundId = Integer.toString(taxonid);
			AphiaRecord record = wormsService.getAphiaRecordByID(taxonid); 
			String foundTaxon = record.getScientificname();
			String foundAuthor = record.getAuthority();
			foundKingdom = record.getKingdom();
			foundPhylum = record.getPhylum();
			foundClass = record.get_class();
			foundOrder = record.getOrder();
			foundFamily = record.getFamily();

			if(foundTaxon.toLowerCase().equals(taxon.toLowerCase()) && author.toLowerCase().equals(foundAuthor.toLowerCase())){
				id = foundId;
			}

		} catch (NullPointerException ex) {
			// no match found
			id = null;
		} catch (RemoteException e) {
			throw new CurationException("WoRMSService failed to access WoRMS Aphia service for " + taxon + ". " +e.getMessage());
		} 

		return id;
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
		if(wormsSourceId == null){
			wormsSourceId = GNISupportingService.getGNIDataSourceID("WoRMS");
		}

		//If GNI doesn't support the data source of IPNI, then do nothing.
		if(wormsSourceId == null){
			return null;
		}

		//search name in GNI
		String nameFromIPNIInLexicalGroup = GNISupportingService.searchLexicalGroupInGNI(scientificName,wormsSourceId);
		if(nameFromIPNIInLexicalGroup == null){
			return null;
		}

		//parse name into scientific name and author by using the name parsing service in GNI
		return GNISupportingService.parseName(nameFromIPNIInLexicalGroup);
	}

	public String getFoundKingdom() {
		return foundKingdom;
	}

	public void setFoundKingdom(String foundKingdom) {
		this.foundKingdom = foundKingdom;
	}

	/**
	 * @return the foundPhylum
	 */
	public String getFoundPhylum() {
		return foundPhylum;
	}

	/**
	 * @param foundPhylum the foundPhylum to set
	 */
	public void setFoundPhylum(String foundPhylum) {
		this.foundPhylum = foundPhylum;
	}

	public String getFoundClass() {
		return foundClass;
	}

	public void setFoundClass(String foundClass) {
		this.foundClass = foundClass;
	}

	public String getFoundOrder() {
		return foundOrder;
	}

	public void setFoundOrder(String foundOrder) {
		this.foundOrder = foundOrder;
	}

	public String getFoundFamily() {
		return foundFamily;
	}

	public void setFoundFamily(String foundFamily) {
		this.foundFamily = foundFamily;
	}	



	@Override
	public AuthorNameComparator getAuthorNameComparator(String authorship,
			String kingdom) {
		// WoRMS contains some algae and some marine plants
		return AuthorNameComparator.authorNameComparatorFactory(authorship, kingdom);
	}


	@Override
	protected boolean nameSearchAgainstServices(NameUsage toCheck) {
		log.debug("Checking: " + toCheck.getScientificName() + " " + toCheck.getAuthorship());
		depth++;   
		try {
			String taxonName = toCheck.getScientificName();
			String authorship = toCheck.getAuthorship();
			toCheck.setAuthorComparator(authorNameComparator);
			AphiaRecord[] resultsArr = wormsService.getAphiaRecords(taxonName, false, false, false, 1);
			if (resultsArr!=null && resultsArr.length>0) { 
				// We got at least one result
				List<AphiaRecord> results = Arrays.asList(resultsArr);
				Iterator<AphiaRecord> i = results.iterator();
				//Multiple matches indicate homonyms (or in WoRMS, deleted records).
				if (results.size()>1) {
				    log.debug("More than one match: " + resultsArr.length);
					boolean exactMatch = false;
					List<AphiaRecord> matches = new ArrayList<AphiaRecord>();
					while (i.hasNext() && !exactMatch) { 
					    AphiaRecord ar = i.next();
					    matches.add(ar);
					    log.debug(ar.getScientificname());
					    log.debug(ar.getAphiaID());
					    log.debug(ar.getAuthority());
					    log.debug(ar.getUnacceptreason());
					    log.debug(ar.getStatus());
					    if (ar !=null && ar.getScientificname()!=null && taxonName!=null && ar.getScientificname().equals(taxonName)) {
					    	if (ar.getAuthority()!=null && ar.getAuthority().equals(authorship)) {
					    		// If one of the results is an exact match on scientific name and authorship, pick that one. 
					    		validatedNameUsage = new NameUsage(ar);
					    		validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
					    		validatedNameUsage.setMatchDescription(NameComparison.MATCH_EXACT);
					    		validatedNameUsage.setAuthorshipStringEditDistance(1d);
					    		validatedNameUsage.setOriginalAuthorship(toCheck.getAuthorship());
					    		validatedNameUsage.setOriginalScientificName(toCheck.getScientificName());
					    		validatedNameUsage.setScientificNameStringEditDistance(1d);
					    		exactMatch = true;
					    	}
					    }
					}
					if (!exactMatch) {
						// If we didn't find an exact match on scientific name and authorship in the list, pick the 
						// closest authorship and list all of the potential matches.  
						Iterator<AphiaRecord> im = matches.iterator();
						AphiaRecord ar = im.next();
						NameUsage closest = new NameUsage(ar);
						StringBuffer names = new StringBuffer();
						names.append(closest.getScientificName()).append(" ").append(closest.getAuthorship()).append(" ").append(closest.getUnacceptReason()).append(" ").append(closest.getTaxonomicStatus());
						while (im.hasNext()) { 
							ar = im.next();
							NameUsage current = new NameUsage(ar);
						    names.append("; ").append(current.getScientificName()).append(" ").append(current.getAuthorship()).append(" ").append(current.getUnacceptReason()).append(" ").append(current.getTaxonomicStatus());
							if (ICZNAuthorNameComparator.calulateSimilarityOfAuthor(closest.getAuthorship(), authorship) < ICZNAuthorNameComparator.calulateSimilarityOfAuthor(current.getAuthorship(), authorship)) { 
								closest = current;
							}
						}
						validatedNameUsage = closest;
					    validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
					    validatedNameUsage.setMatchDescription(NameComparison.MATCH_MULTIPLE + " " + names.toString());
					    validatedNameUsage.setOriginalAuthorship(toCheck.getAuthorship());
					    validatedNameUsage.setOriginalScientificName(toCheck.getScientificName());
					    validatedNameUsage.setScientificNameStringEditDistance(1d);
					    validatedNameUsage.setAuthorshipStringEditDistance(ICZNAuthorNameComparator.calulateSimilarityOfAuthor(toCheck.getAuthorship(), validatedNameUsage.getAuthorship()));
					}
				} else { 
				  // we got exactly one result
				  while (i.hasNext()) { 
					AphiaRecord ar = i.next();
					if (ar !=null && ar.getScientificname()!=null && taxonName!=null && ar.getScientificname().equals(taxonName)) {
						if (ar.getAuthority()!=null && ar.getAuthority().equals(authorship)) { 
							// scientific name and authorship are an exact match 
							validatedNameUsage = new NameUsage(ar);
							validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
							validatedNameUsage.setMatchDescription(NameComparison.MATCH_EXACT);
							validatedNameUsage.setAuthorshipStringEditDistance(1d);
							validatedNameUsage.setOriginalAuthorship(toCheck.getAuthorship());
							validatedNameUsage.setOriginalScientificName(toCheck.getScientificName());
							validatedNameUsage.setScientificNameStringEditDistance(1d);
						} else {
							// find how 
							if (authorship!=null && ar!=null && ar.getAuthority()!=null) { 
								//double similarity = taxonNameToValidate.calulateSimilarityOfAuthor(ar.getAuthority());
								log.debug(authorship);
								log.debug(ar.getAuthority());
								NameComparison comparison = authorNameComparator.compare(authorship, ar.getAuthority());
								String match = comparison.getMatchType();
								double similarity = comparison.getSimilarity();
								log.debug(similarity);
								//if (match.equals(NameUsage.MATCH_DISSIMILAR) || match.equals(NameUsage.MATCH_ERROR)) {
									// result.setMatchDescription("Same name, authorship different");
								//} else { 
							        validatedNameUsage = new NameUsage(ar);
							        validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
							        validatedNameUsage.setAuthorshipStringEditDistance(similarity);
							        validatedNameUsage.setOriginalAuthorship(toCheck.getAuthorship());
							        validatedNameUsage.setOriginalScientificName(toCheck.getScientificName());
								    validatedNameUsage.setMatchDescription(match);
								//}
							} else { 
								// no authorship was provided in the results, treat as no match
								log.error("Result with null authorship.");
							}
						}
					}
				  }
				}
			} else { 
				log.debug("No match.");
				// Try WoRMS fuzzy matching query
				String[] searchNames = { taxonName + " " + authorship };
				AphiaRecord[][] matchResultsArr = wormsService.matchAphiaRecordsByNames(searchNames, false);
				if (matchResultsArr!=null && matchResultsArr.length>0) {
					Iterator<AphiaRecord[]> i0 = (Arrays.asList(matchResultsArr)).iterator();
					while (i0.hasNext()) {
						// iterate through the inputs, there should be one and only one
						AphiaRecord[] matchResArr = i0.next();
						List<AphiaRecord> matches = Arrays.asList(matchResArr);
						Iterator<AphiaRecord> im = matches.iterator();
						List<NameUsage> potentialMatches = new ArrayList<NameUsage>();
						while (im.hasNext()) { 
							// iterate through the results, no match will have one result that is null
							AphiaRecord ar = im.next();
							if (ar!=null) { 
								NameUsage match = new NameUsage(ar);
								double similarity = ICZNAuthorNameComparator.calulateSimilarityOfAuthor(toCheck.getAuthorship(), match.getAuthorship());
								match.setAuthorshipStringEditDistance(similarity);
								log.debug(match.getScientificName());
								log.debug(match.getAuthorship());
								log.debug(similarity);
								potentialMatches.add(match);
							} else {
								log.debug("im.next() was null");
							}
						} 
						log.debug("Fuzzy Matches: " + potentialMatches.size());
						if (potentialMatches.size()==1) { 
							validatedNameUsage = potentialMatches.get(0);
							String authorComparison = authorNameComparator.compare(toCheck.getAuthorship(), validatedNameUsage.getAuthorship()).getMatchType();
							validatedNameUsage.setMatchDescription(NameComparison.MATCH_FUZZY_SCINAME + "; authorship " + authorComparison);
							validatedNameUsage.setOriginalAuthorship(toCheck.getAuthorship());
							validatedNameUsage.setOriginalScientificName(toCheck.getScientificName());
							validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
						}
					} // iterator over input names, should be just one.
			    } else {
			    	log.error("Fuzzy match query returned null instead of a result set.");
			    }
			}
		} catch (RemoteException e) {
			if (e.getMessage().equals("Connection timed out")) { 
				log.error(e.getMessage() + " " + toCheck.getScientificName() + " " + toCheck.getInputDbPK());
			} else if (e.getCause()!=null && e.getCause().getClass().equals(UnknownHostException.class)) { 
				log.error("Connection Probably Lost.  UnknownHostException: "+ e.getMessage());
			} else {
				log.error(e.getMessage(), e);
			}
			if (depth<4) {
				// Try again, up to three times.
				this.nameSearchAgainstServices(toCheck);
			}
		}
		depth--;
		return false;
	}

}

/** 
 * WoRMSService.java 
 * 
 * Copyright 2012 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

	private static final Log logger = LogFactory.getLog(WoRMSService.class);
	
	private AphiaNameServicePortTypeProxy wormsService;
	protected AuthorNameComparator authorNameComparator;
	
	protected int depth;
	
	private String WoRMSlsid = null;	

	private String foundKingdom = null;
	private String foundPhylum = null;
	private String foundClass = null;
	private String foundOrder = null;
	private String foundFamily = null;	

	private String wormsSourceId = null;	

	private final static String wormsLSIDPrefix = "urn:lsid:marinespecies.org:taxname:";

	public WoRMSService() { 
		super();
		initSciName();
	}
	
	public WoRMSService(boolean test) throws IOException {
		super();
		initSciName();
		if (test) { 
			test();
		}
	}
	
	protected void test()  throws IOException { 
		logger.debug(wormsService.getEndpoint());
		URL test = new URL(wormsService.getEndpoint());
		URLConnection conn = test.openConnection();
		conn.connect();
	}

    protected void initSciName() { 
		authorNameComparator = new ICZNAuthorNameComparator(.75d,.5d);
		validatedNameUsage = new NameUsage("WoRMS",authorNameComparator);
		depth = 0;
		wormsService = new AphiaNameServicePortTypeProxy();
    }
 
	@Override
	protected String getServiceImplementationName() {
		return "IPNI";
	}

	public String getLSID(){
		return WoRMSlsid;
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
		boolean result = false;
		addToServiceName("WoRMS");
		logger.debug("Checking: " + toCheck.getOriginalScientificName() + " " + toCheck.getOriginalAuthorship());
		depth++;   
		try {
			String taxonName = toCheck.getOriginalScientificName();
			String authorship = toCheck.getOriginalAuthorship();
			toCheck.setAuthorComparator(authorNameComparator);
			AphiaRecord[] resultsArr = wormsService.getAphiaRecords(taxonName, false, false, false, 1);
			if (resultsArr!=null && resultsArr.length>0) { 
				// We got at least one result
				List<AphiaRecord> results = Arrays.asList(resultsArr);
				Iterator<AphiaRecord> i = results.iterator();
				//Multiple matches indicate homonyms (or in WoRMS, deleted records).
				if (results.size()>1) {
				    logger.debug("More than one match: " + resultsArr.length);
					boolean exactMatch = false;
					List<AphiaRecord> matches = new ArrayList<AphiaRecord>();
					while (i.hasNext() && !exactMatch) { 
					    AphiaRecord ar = i.next();
					    matches.add(ar);
					    logger.debug(ar.getScientificname());
					    logger.debug(ar.getAphiaID());
					    logger.debug(ar.getAuthority());
					    logger.debug(ar.getUnacceptreason());
					    logger.debug(ar.getStatus());
					    if (ar !=null && ar.getScientificname()!=null && taxonName!=null && ar.getScientificname().equals(taxonName)) {
					    	if (ar.getAuthority()!=null && ar.getAuthority().equals(authorship)) {
					    		// If one of the results is an exact match on scientific name and authorship, pick that one. 
					    		validatedNameUsage = new NameUsage(ar);
					    		validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
					    		validatedNameUsage.setMatchDescription(NameComparison.MATCH_EXACT);
					    		validatedNameUsage.setAuthorshipStringEditDistance(1d);
					    		validatedNameUsage.setOriginalAuthorship(toCheck.getOriginalAuthorship());
					    		validatedNameUsage.setOriginalScientificName(toCheck.getOriginalScientificName());
					    		validatedNameUsage.setScientificNameStringEditDistance(1d);
					    		exactMatch = true;
					    		addToComment("Found exact match in WoRMS.");
					    		setCurationStatus(CurationComment.CORRECT);
					    		result = true;
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
					    validatedNameUsage.setOriginalAuthorship(toCheck.getOriginalAuthorship());
					    validatedNameUsage.setOriginalScientificName(toCheck.getOriginalScientificName());
					    validatedNameUsage.setScientificNameStringEditDistance(1d);
					    validatedNameUsage.setAuthorshipStringEditDistance(ICZNAuthorNameComparator.calulateSimilarityOfAuthor(toCheck.getAuthorship(), validatedNameUsage.getAuthorship()));
					    addToComment("Found plausible match in WoRMS: " + validatedNameUsage.getScientificName() + " " + validatedNameUsage.getAuthorship() +  " " + validatedNameUsage.getMatchDescription());
					    result = true;
					}
				} else { 
				  // we got exactly one result
				  logger.debug(resultsArr.length);
				  while (i.hasNext()) { 
					AphiaRecord ar = i.next();
					if (ar !=null && ar.getScientificname()!=null && taxonName!=null && ar.getScientificname().equals(taxonName)) {
						logger.debug(ar.getScientificname());
						logger.debug(ar.getAuthority());
						if (ar.getAuthority()!=null && ar.getAuthority().equals(authorship)) { 
							// scientific name and authorship are an exact match 
							validatedNameUsage = new NameUsage(ar);
							validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
							validatedNameUsage.setMatchDescription(NameComparison.MATCH_EXACT);
							validatedNameUsage.setAuthorshipStringEditDistance(1d);
							validatedNameUsage.setOriginalAuthorship(toCheck.getOriginalAuthorship());
							validatedNameUsage.setOriginalScientificName(toCheck.getOriginalScientificName());
							validatedNameUsage.setScientificNameStringEditDistance(1d);
					    	addToComment("Found exact match in WoRMS.");
					    	setCurationStatus(CurationComment.CORRECT);
					    	logger.debug(getCurationStatus());
					        result = true;
						} else {
							// find how 
							if (authorship!=null && ar!=null && ar.getAuthority()!=null) { 
								//double similarity = taxonNameToValidate.calulateSimilarityOfAuthor(ar.getAuthority());
								logger.debug(authorship);
								logger.debug(ar.getAuthority());
								NameComparison comparison = authorNameComparator.compare(authorship, ar.getAuthority());
								String match = comparison.getMatchType();
								double similarity = comparison.getSimilarity();
								logger.debug(similarity);
								if (match.equals(NameComparison.MATCH_DISSIMILAR) || match.equals(NameComparison.MATCH_ERROR)) {
									addToComment("Found a possible match " + ar.getScientificname() + " " + ar.getAuthority() + "in WoRMS, but authorship is different (" + match + "), so not asserting a match.");
								} else { 
							        validatedNameUsage = new NameUsage(ar);
							        validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
							        validatedNameUsage.setAuthorshipStringEditDistance(similarity);
							        validatedNameUsage.setOriginalAuthorship(toCheck.getOriginalAuthorship());
							        validatedNameUsage.setOriginalScientificName(toCheck.getOriginalScientificName());
								    validatedNameUsage.setMatchDescription(match);		
					    	        addToComment("Found plausible match in WoRMS: " + match);
					    	        setCurationStatus(CurationComment.CURATED);
					                result = true;
								}
							} else { 
					    	    addToComment("Possible match in WoRMS, but it lacks an authorship.");
								// no authorship was provided in the results, treat as no match
								logger.error("Result with null authorship.");
							}
						}
					}
				  }
				}
			} else { 
				logger.debug("No match.");
				addToComment("Trying for fuzzy match in WoRMS.");
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
								logger.debug(match.getScientificName());
								logger.debug(match.getAuthorship());
								logger.debug(similarity);
								potentialMatches.add(match);
							} else {
								logger.debug("im.next() was null");
							}
						} 
						logger.debug("Fuzzy Matches: " + potentialMatches.size());
						if (potentialMatches.size()==1) { 
							validatedNameUsage = potentialMatches.get(0);
							String authorComparison = authorNameComparator.compare(toCheck.getAuthorship(), validatedNameUsage.getAuthorship()).getMatchType();
							validatedNameUsage.setMatchDescription(NameComparison.MATCH_FUZZY_SCINAME + "; authorship " + authorComparison);
							validatedNameUsage.setOriginalAuthorship(toCheck.getOriginalAuthorship());
							validatedNameUsage.setOriginalScientificName(toCheck.getOriginalScientificName());
							validatedNameUsage.setInputDbPK(toCheck.getInputDbPK());
				            addToComment("Found Fuzzy Match in WoRMS: " + validatedNameUsage.getMatchDescription());
							result = true;
						}
					} // iterator over input names, should be just one.
			    } else {
			    	logger.error("Fuzzy match query returned null instead of a result set.");
			    }
			}
		} catch (RemoteException e) {
			if (e.getMessage().equals("Connection timed out")) { 
				logger.error(e.getMessage() + " " + toCheck.getScientificName() + " " + toCheck.getInputDbPK());
			} else if (e.getCause()!=null && e.getCause().getClass().equals(UnknownHostException.class)) { 
				logger.error("Connection Probably Lost.  UnknownHostException: "+ e.getMessage());
			} else {
				logger.error(e.getMessage(), e);
			}
			if (depth<4) {
				// Try again, up to three times.
				result = this.nameSearchAgainstServices(toCheck);
			}
		}
		depth--;
		if (!result) { 
			addToComment("No match found in WoRMS.");
		}	
		return result;
	}

}

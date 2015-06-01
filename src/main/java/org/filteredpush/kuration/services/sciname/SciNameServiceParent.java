/** 
 * SciNameServiceParent.java 
 * 
 * Copyright 2014 President and Fellows of Harvard College
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
import edu.harvard.mcz.nametools.ICNafpAuthorNameComparator;
import edu.harvard.mcz.nametools.NameComparison;
import edu.harvard.mcz.nametools.NameUsage;

import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.interfaces.INewScientificNameValidationService;
import org.filteredpush.kuration.util.*;


/**
 * Parent class for validation of Scientific Names against taxonomic or nomenclatural
 * services, with a failover to lookup in GNI and check of lexical group against services.  
 * 
 * @author Tianhong Song
 * @author Paul J. Morris
 *
 */
public abstract class SciNameServiceParent implements INewScientificNameValidationService {

	private static final Log logger = LogFactory.getLog(SciNameServiceParent.class);
	
	/**
	 * Search the supported service for a taxon name, and set the metadata of the SciNameService instance
	 * to reflect the results.  Sets values for curationStatus, comment, and validatedNameUsage.
	 * 
	 * @param toCheck NameUsage where originalScientificName and originalScientificNameAuthorship are set
	 * @return true if a match is found in the service, false otherwise.    
	 */
    protected abstract boolean nameSearchAgainstServices(NameUsage toCheck); 
    
    protected abstract String getServiceImplementationName();
    
    protected abstract void init();

    protected CurationStatus curationStatus;
    protected NameUsage validatedNameUsage = null;
    protected StringBuffer comment = new StringBuffer();
    protected StringBuffer serviceName = new StringBuffer();
    protected String selectedMode = MODE_NOMENCLATURAL;
    
    // private String GBIF_name_GUID = "";
    // Documented at http://api.gbif.org/
    private final static String GBIF_GUID_Prefix = "http://api.gbif.org/v1/species/";

    protected static HashMap<String, CacheValue> sciNameCache = new HashMap<String, CacheValue>();
    boolean useCache = true;
    protected static int count = 0;

    public SciNameServiceParent() { 
    	init();
    }
    
    
	@Override
	public void setValidationMode(String validationMode) {
		if (validationMode.equals(MODE_TAXONOMIC)) { 
			selectedMode = MODE_TAXONOMIC;
		} else { 
			selectedMode = MODE_NOMENCLATURAL;
		}
	}    
    
   public void validateScientificName(String scientificName, String author){
	   validateScientificName(scientificName, author, "", "", "", "", "", "", "", "", "", "", "");
   }

   /**
    * 
    */
   public void validateScientificName(String scientificNameToValidate, String authorToValidate, String genus, String subgenus, String specificEpithet, String verbatimTaxonRank, String infraspecificEpithet, String taxonRank, String kingdom, String phylum, String tclass, String order, String family) {

	   // (1) set up initial conditions 
	   
       // To carry over the original sciname and author:
       // This has the appearance of an assignment to the wrong variable, but it isn't
       // this data is extracted by MongoSummaryWriter to provide "WAS:" values
       serviceName = new StringBuffer();
       serviceName.append("scientificName:"+ scientificNameToValidate + "#scientificNameAuthorship:" + authorToValidate + "#");
       comment = new StringBuffer();
       addToServiceName(this.getServiceImplementationName());
	   
       NameUsage toCheck = new NameUsage();
	   if (authorToValidate!=null && authorToValidate.length()>0 && scientificNameToValidate!=null && scientificNameToValidate.endsWith(authorToValidate)) { 
		   // remove author from scientific name
		   int endIndex = scientificNameToValidate.lastIndexOf(authorToValidate) -1;
		   if (endIndex>-1) { 
		       scientificNameToValidate = scientificNameToValidate.substring(0, endIndex).trim();
		       logger.debug(scientificNameToValidate);
               addToComment("Removed authorship from scientificName for validation, retained in scientificNameAuthorship.");
		   }
	   }
	   toCheck.setOriginalScientificName(scientificNameToValidate);
	   toCheck.setOriginalAuthorship(authorToValidate);
	   toCheck.setKingdom(kingdom);
	   logger.debug(toCheck.getOriginalScientificName());
	   logger.debug(toCheck.getOriginalAuthorship());
	   validatedNameUsage = new NameUsage(getServiceImplementationName(), getAuthorNameComparator(toCheck.getOriginalAuthorship(), kingdom),toCheck.getOriginalScientificName(),toCheck.getOriginalScientificName());
	   validatedNameUsage.setOriginalAuthorship(authorToValidate);
	   validatedNameUsage.setOriginalScientificName(scientificNameToValidate);
       //System.err.println("servicestart#"+_id + "#" + System.currentTimeMillis());
       
       // Default response, unable to determine validity.  
       curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
       
       // (2) perform internal consistency check
       HashMap<String, String> result1 = SciNameServiceUtil.checkConsistencyToAtomicField(scientificNameToValidate, genus, subgenus, specificEpithet, verbatimTaxonRank, taxonRank, infraspecificEpithet);
       // TODO: [#384] Comment and status is confusing when atomic values are null 
       addToComment(result1.get("comment"));
       curationStatus = new CurationStatus(result1.get("curationStatus"));

       // (3) try to find the name in the supported service.
       boolean matched = nameSearchAgainstServices(toCheck);
       
       // (3a) try harder for authorship if needed
       if (matched && validatedNameUsage.getAuthorship().length()==0) {
    	   // got a match, but didn't find the authorship
    	   
    	   if (kingdom!=null && (kingdom.equals("Plantae") || kingdom.equals("Fungi")) && SciNameServiceUtil.isAutonym(validatedNameUsage.getScientificName()) ) { 
    	       // Skip special case, Botanical autonyms
    		   addToComment("Authorship is correctly absent, appears to be a botanical autonym.");
    	   } else { 
    		   // try GBIF checklist bank.
    		   HashMap<String, String> result3a = SciNameServiceUtil.checklistBankNameSearch(scientificNameToValidate, "", taxonRank, kingdom, phylum, tclass, order, family, GBIFService.KEY_GBIFBACKBONE);
    		   addToServiceName("GBIF CheckListBank Backbone");
               addToComment(result3a.get("comment"));
               curationStatus = new CurationStatus(result3a.get("curationStatus"));
               if(result3a.get("scientificName") != null){
                   validatedNameUsage.setScientificName(result3a.get("scientificName"));
                   validatedNameUsage.setAuthorship(result3a.get("author"));
                   addToComment("Got a valid result from GBIF checklistbank Backbone");
                   validatedNameUsage.setGuid(GBIF_GUID_Prefix + result3a.get("guid"));
               }
    	   }
       }
       
       // (3b) compare the authors
       if (matched) {
    	   if (validatedNameUsage.getAuthorComparator()==null) { 
    		   validatedNameUsage.setAuthorComparator(getAuthorNameComparator(validatedNameUsage.getOriginalAuthorship(), kingdom));
    	   }
    	   NameComparison comparison = validatedNameUsage.getAuthorComparator().compare(validatedNameUsage.getOriginalAuthorship(), validatedNameUsage.getAuthorship());
    	   double nameSimilarity = ICNafpAuthorNameComparator.stringSimilarity(validatedNameUsage.getScientificName(), validatedNameUsage.getOriginalScientificName());
    	   double authorSimilarity = comparison.getSimilarity();
    	   String match = comparison.getMatchType();
    	   logger.debug(match);
    	   if (authorSimilarity==1d && nameSimilarity==1d) {
    		   // author similarity is more forgiving than exact string match, don't correct things that aren't substantive errors.
    		   validatedNameUsage.setMatchDescription(NameComparison.MATCH_EXACT);
    		   curationStatus = CurationComment.CORRECT;
    	   } else { 
               if (match.equals(NameComparison.MATCH_SAMEBUTABBREVIATED)) { 
           				       addToComment("The scientific name and authorship are probably correct, but with a different abbreviation for the author.  ");
                		       curationStatus = CurationComment.CORRECT;
               } else { 
    		   validatedNameUsage.setMatchDescription(match);
    		   curationStatus = CurationComment.CURATED;
               }
    	   }
    	   validatedNameUsage.setAuthorshipStringEditDistance(authorSimilarity);     

    	   String authorshipSimilarity = " Authorship: " +  validatedNameUsage.getMatchDescription() + " Similarity: " + Double.toString(authorSimilarity);

    	   addToComment(authorshipSimilarity);
    	   
       }
           
       //System.err.println("step1#"+_id + "#" + System.currentTimeMillis());

       // (4) failover by trying alternative supporting services.
       if (!matched && result1.get("scientificName") != null){
    	   // (4a) Try the global names resolver.
           HashMap<String, String> result2 = SciNameServiceUtil.checkMisspelling(result1.get("scientificName"));
           addToServiceName("Global Name Resolver");
           addToComment(result2.get("comment"));
           curationStatus = new CurationStatus(result2.get("curationStatus"));

           //System.err.println("step2#"+_id + "#" + System.currentTimeMillis());
           // (4b) Try GNI and the GBIF backbone taxonomy. 
           if (result2.get("scientificName") != null){
               boolean hasResult = validateScientificNameAgainstServices(result2.get("scientificName"), authorToValidate, taxonRank, kingdom, phylum, tclass, order, family);
               if (hasResult){
                   if(validatedNameUsage.getAuthorship().trim().equals("") || validatedNameUsage.getScientificName().trim().equals("")){
                	   // TODO: Handle botanical autonyms, which shouldn't have authorship.
                       curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
                       if (validatedNameUsage.getAuthorship().trim().equals("")) {  addToComment("validated author is empty"); } 
                       if (validatedNameUsage.getScientificName().trim().equals("")) {  addToComment("validated sciName is empty"); } 
                   }else {
                	   String validatedAuthor = validatedNameUsage.getAuthorship();
                	   String validatedScientificName = validatedNameUsage.getScientificName();
                       if (validatedAuthor.trim().equals(authorToValidate) && validatedScientificName.trim().equals(scientificNameToValidate)) {
                           curationStatus = CurationComment.CORRECT;
                           addToComment("The original SciName and Authorship are valid");
                       } else {
                           curationStatus = CurationComment.CURATED;
                           addToComment("The original SciName and Authorship are curated");
                       }
                	   NameComparison comparison = validatedNameUsage.getAuthorComparator().compare(validatedNameUsage.getOriginalAuthorship(), validatedNameUsage.getAuthorship());
                	   double nameSimilarity = ICNafpAuthorNameComparator.stringSimilarity(validatedNameUsage.getScientificName(), validatedNameUsage.getOriginalScientificName());
                	   double authorSimilarity = comparison.getSimilarity();
                	   String match = comparison.getMatchType();
                	   logger.debug(match);
                	   if (authorSimilarity==1d && nameSimilarity==1d) {
                		   // author similarity is more forgiving than exact string match, don't correct things that aren't substantive errors.
                		   validatedNameUsage.setMatchDescription(NameComparison.MATCH_EXACT);
                		   curationStatus = CurationComment.CORRECT;
                	   } else { 
                		   if (match.equals(NameComparison.MATCH_SAMEBUTABBREVIATED)) { 
           				       addToComment("The scientific name and authorship are probably correct, but with a different abbreviation for the author.  ");
                		       curationStatus = CurationComment.CORRECT;
                	       } else if (match.equals(NameComparison.MATCH_ADDSAUTHOR)) { 
                	    	   logger.debug(match);
                	    	   logger.debug(validatedNameUsage.getAuthorship());
                	    	   logger.debug(validatedNameUsage.getAcceptedAuthorship());
                	    	   logger.debug(validatedAuthor);
                		       validatedNameUsage.setMatchDescription(match);
                		       curationStatus = CurationComment.CURATED;
                	       }else { 
                		       validatedNameUsage.setMatchDescription(match);
                		       curationStatus = CurationComment.CURATED;
                	       }
                	   }
                	   validatedNameUsage.setAuthorshipStringEditDistance(authorSimilarity);     

                	   String authorshipSimilarity = " Authorship: " +  validatedNameUsage.getMatchDescription() + " Similarity: " + Double.toString(authorSimilarity);

                	   addToComment(authorshipSimilarity);                       
                   }
               }else{
                   curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
                   addToComment("The original SciName and Authorship cannot be curated");
               }

               //todo: next
           }
           else{
               //no result, stop
           }
       }else{
           //System.err.println("step2not#"+_id + "#" + System.currentTimeMillis());
           //no result, stop
       }
       //System.err.println("serviceend#"+_id + "#" + System.currentTimeMillis());
       
       // Handle replacements in taxonomic mode
       if (this.selectedMode.equals(MODE_TAXONOMIC)) { 
    	   if (curationStatus.toString().equals(CurationComment.CORRECT.toString())) { 
    		   if (!validatedNameUsage.getScientificName().equals(validatedNameUsage.getAcceptedName()) && validatedNameUsage.getAcceptedName().length()>0) {
    			   curationStatus = CurationComment.CURATED;
    			   addToComment("Replacing " + validatedNameUsage.getScientificName() + " with name in current use " + validatedNameUsage.getAcceptedName());                       
    		   }	   
    	   } else {
    		   if (!validatedNameUsage.getScientificName().equals(validatedNameUsage.getAcceptedName()) && validatedNameUsage.getAcceptedName().length()>0) {
    			   addToComment("Replacing " + validatedNameUsage.getScientificName() + " with name in current use " + validatedNameUsage.getAcceptedName());                       
    		   }
    	   }
       }
   }

   /**
    * Attempt to validate the provided scientific name against alternative services.
    * 
    * @param taxon
    * @param author
    * @param taxonRank
    * @param kingdom
    * @param phylum
    * @param tclass
    * @param order
    * @param family
    * @return
    */
    protected boolean validateScientificNameAgainstServices(String taxon, String author, String taxonRank, String kingdom, String phylum, String tclass, String order, String family){
        boolean failedAtGNI = false;
        //System.err.println("remotestart#"+_id + "#" + System.currentTimeMillis());
        NameUsage toCheck = new NameUsage();
        toCheck.setOriginalScientificName(taxon);
        toCheck.setOriginalAuthorship(author);
        boolean hasResult = nameSearchAgainstServices(toCheck);
        //System.err.println("remoteend#"+_id + "#" + System.currentTimeMillis());
        if(!hasResult){
            // no match was found, over to GNI
        	addToServiceName("Global Name Index");
            // access the GNI and try to get the name that is in the lexical group and from IPNI
            Vector<String> resolvedNameInfo = null;
            try {
                resolvedNameInfo = GNISupportingService.resolveDataSourcesNameInLexicalGroupFromGNI(taxon);
            } catch (CurationException e) {
                addToComment("Fail to access GNI service");
                failedAtGNI = true;
                //return false;
            }

            if(failedAtGNI || resolvedNameInfo == null || resolvedNameInfo.size()==0){
                //failed to find it in GNI
                if(!failedAtGNI){
                    addToComment("Can't find the scientific name and authorship by searching the lexical group in GNI.");
                }//failover to GBIF Checklistbank Backbone
                HashMap<String, String> result2 = SciNameServiceUtil.checklistBankNameSearch(taxon, author, taxonRank, kingdom, phylum, tclass, order, family, "d7dddbf4-2cf0-4f39-9b2a-bb099caae36c");
                addToServiceName("GBIF CheckListBank Backbone");
                addToComment(result2.get("comment"));
                curationStatus = new CurationStatus(result2.get("curationStatus"));

                if(result2.get("scientificName") != null){
                    validatedNameUsage.setScientificName(result2.get("scientificName"));
                    validatedNameUsage.setAuthorship(result2.get("author"));
                    addToComment("Got a valid result from GBIF checklistbank Backbone");
                    validatedNameUsage.setGuid(GBIF_GUID_Prefix + result2.get("guid"));
                    return true;
                }else{
                    return false;
                }

            }else{
                //find it in GNI
                String resolvedScientificName = resolvedNameInfo.get(0);
                String resolvedScientificNameAuthorship = resolvedNameInfo.get(1);

                //searching for this name in service again.

                toCheck.setOriginalScientificName(resolvedScientificName);
                toCheck.setOriginalAuthorship(resolvedScientificNameAuthorship);
                boolean hasResult2 = nameSearchAgainstServices(toCheck);
                if(!hasResult2){
                    //failed to find the name got from GNI in service
                    curationStatus = CurationComment.UNABLE_CURATED;
                    addToComment("Found a name in the same lexical group as the searched scientific name but failed to find this name in remote service.");
                }else{
                	// TODO: Not sure that we need to set this, nameSearchAgainstServices should set it.
                    //correct the wrong scientific name or author by searching in both IPNI and GNI
                    //validatedNameUsage.setScientificName(resolvedScientificName);
                    //validatedNameUsage.setAuthorship(resolvedScientificNameAuthorship);
                    //GBIF_name_GUID = constructGBIFGUID(id);
                    addToComment("Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.");
                    curationStatus = CurationComment.CURATED;
                }
                return true;
            }
        }else{
            return true;
        }

    }

    @Override
    public CurationStatus getCurationStatus(){
        return curationStatus;
    }

    @Override
    public String getCorrectedScientificName(){
    	if (selectedMode.equals(MODE_TAXONOMIC)) { 
    		validatedNameUsage.fixAuthorship();
    		return validatedNameUsage.getAcceptedName();
    	} 
        return validatedNameUsage.getScientificName();
    }

    @Override
    public String getCorrectedAuthor(){
    	if (selectedMode.equals(MODE_TAXONOMIC)) { 
    		validatedNameUsage.fixAuthorship();
    		return validatedNameUsage.getAcceptedAuthorship();
    	} 
        return validatedNameUsage.getAuthorship();
    }

    @Override
    public String getComment() {
        return comment.toString();
    }
    
    /**
     * Add a comment to the list of comments for validation of a record 
     * by the implemented service.  Adds an appropriate comment separator.
     * 
     * @param aComment comment to add to the current list of comments.
     */
    public void addToComment(String aComment) { 
    	if (comment.toString().length()==0) { 
    		comment.append(aComment);
    	} else { 
    		comment.append(" | ").append(aComment);
    	}
    }

    @Override
    public String getGUID(){
        return validatedNameUsage.getGuid();
    }

    @Override
    public String getServiceName() {
        return serviceName.toString();  
    }
    
    /**
     * Add a service to the list of invoked services for validation of a record.
     * Adds an appropriate separator.
     * 
     * @param aServiceName comment to add to the current list of comments.
     */
    public void addToServiceName(String aServiceName) { 
    	if (serviceName.toString().length()==0) { 
    		serviceName.append(aServiceName);
    	} else { 
    		serviceName.append(" | ").append(aServiceName);
    	}
    }
    

    protected String getKey(String name, String author){
        return name+author;
    }

    // cache doesn't seem to be a cache, but is related to workflow provenance??
    protected void addToCache(boolean hasResult){
    	if (hasResult) { 
    		try { 
    			String validatedScientificName = this.validatedNameUsage.getScientificName();
    			String validatedAuthor = this.validatedNameUsage.getAuthorship();
    			String key = getKey(validatedScientificName, validatedAuthor);
    			CacheValue newValue = new SciNameCacheValue().setHasResult(hasResult).setAuthor(validatedAuthor).setTaxon(validatedScientificName).setComment(getComment()).setStatus(curationStatus).setSource(getServiceName());
    			if(!sciNameCache.containsKey(key)) {
    				sciNameCache.put(key, newValue);
    			} else {
    				if (!sciNameCache.get(key).equals(newValue)) {
    					//need to handle the case where the cached value is different than the new value
    				}
    			}
    		} catch (NullPointerException e) { 
    			// expected if there is nothing to cache.
    		}
    	}
    }

	@Override
	public AuthorNameComparator getAuthorNameComparator(String authorship,
			String kingdom) {
		return AuthorNameComparator.authorNameComparatorFactory(authorship, kingdom);
	}

}
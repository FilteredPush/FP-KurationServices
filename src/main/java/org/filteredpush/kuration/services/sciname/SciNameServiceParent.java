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
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.interfaces.INewScientificNameValidationService;
import org.filteredpush.kuration.services.BaseCurationService;
import org.filteredpush.kuration.util.*;
import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.nameparser.NameParser;
import org.gbif.nameparser.UnparsableException;


/**
 * Parent class for validation of Scientific Names against taxonomic or nomenclatural
 * services, with a failover to lookup in GNI and check of lexical group against services.  
 * 
 * @author Tianhong Song
 * @author Paul J. Morris
 *
 */
public abstract class SciNameServiceParent extends BaseCurationService implements INewScientificNameValidationService {



	private static final Log logger = LogFactory.getLog(SciNameServiceParent.class);
	
	/**
	 * Regular expression pattern that recognizes comments that denote higher taxa being filled in.
	 * Can be used to remove comments when the filled in fields aren't included in the output produced
	 * by code that uses the scientific name validate method.
	 */
	public static final String FILL_IN_HIGHER_REGEX = "\\| Filled In (Kingdom|Phylum|Class|Order|Family) {0,1}";
	
	/**
	 * Search the supported service for a taxon name, and set the metadata of the SciNameService instance
	 * to reflect the results.  Sets values for curationStatus, comment, and validatedNameUsage.
	 * 
	 * @param toCheck NameUsage where originalScientificName and originalScientificNameAuthorship are set
	 * @return true if a match is found in the service, false otherwise.    
	 */
    protected abstract boolean nameSearchAgainstServices(NameUsage toCheck); 
    
    protected abstract String getServiceImplementationName();
    
    protected abstract void initSciName();

    //protected CurationStatus curationStatus;
    protected NameUsage validatedNameUsage = null;
    //protected StringBuffer comment = new StringBuffer();
    //protected StringBuffer serviceName = new StringBuffer();
    protected String selectedMode = MODE_NOMENCLATURAL;
    
    // private String GBIF_name_GUID = "";
    // Documented at http://api.gbif.org/
    private final static String GBIF_GUID_Prefix = "http://api.gbif.org/v1/species/";

    protected static HashMap<String, CacheValue> sciNameCache = new HashMap<String, CacheValue>();
    boolean useCache = true;
    protected static int count = 0;

    public SciNameServiceParent() {
    	super();
    	initSciName();
    }
    
    
	@Override
	public void setValidationMode(String validationMode) {
		if (validationMode.equals(MODE_TAXONOMIC)) { 
			selectedMode = MODE_TAXONOMIC;
		} else { 
			selectedMode = MODE_NOMENCLATURAL;
		}
	}    
    
    /**
     * Convenience method for validating scientific names on minimal information.
     * 
     * @param scientificNameToValidate
     * @param authorToValidate
     * 
     */
	public void validateScientificName(String scientificName, String author){
	   validateScientificName(scientificName, author, "", "", "", "", "", "", "", "", "", "", "","");
   }

   /**
    * Validate a scientific name, make results of the validation available from other methods of this
    * class.
    * 
    * @param scientificNameToValidate
    * @param authorToValidate
    * @param genus
    * @param subgenus
    * @param specificEpithet
    * @param verbatimTaxonRank
    * @param infraspecificEpithet
    * @param taxonRank
    * @param kingdom
    * @param phylum
    * @param tclass
    * @param order
    * @param family
    * @param genericEpithet not yet available as a DarwinCore term.
    */
   public void validateScientificName(String scientificNameToValidate, String authorToValidate, String genus, String subgenus, String specificEpithet, String verbatimTaxonRank, String infraspecificEpithet, String taxonRank, String kingdom, String phylum, String tclass, String order, String family, String genericEpithet) {

	   // (1) set up initial conditions 
	   
	   initBase();
       // To carry over the original sciname and author:
       // This has the appearance of an assignment to the wrong variable, but it isn't
	   // this data is extracted by MongoSummaryWriter to provide "WAS:" values
	   addToServiceName("scientificName:"+ scientificNameToValidate + "#scientificNameAuthorship:" + authorToValidate + "#");
	   addInputValue(SpecimenRecord.dwc_scientificName, scientificNameToValidate);
	   addInputValue(SpecimenRecord.dwc_scientificNameAuthorship,authorToValidate);
	   addInputValue("genus",genus);
	   addInputValue("subgenus",subgenus);
	   addInputValue("specificEpithet",specificEpithet);
	   addInputValue("infraSpecificEpithet",infraspecificEpithet);
	   // TODO: Add all input values

	   addToServiceName(this.getServiceImplementationName());

	   if (scientificNameToValidate!=null) {
		   scientificNameToValidate = scientificNameToValidate.trim();
	   }
	   if (authorToValidate!=null) { 
		   authorToValidate = authorToValidate.trim();
	   }
	   
	   // If no scientific name was provided, try to fill one in. 
	   StringBuffer tempAssembly = new StringBuffer();
	   if (scientificNameToValidate==null || scientificNameToValidate.length()==0) { 
		   addToComment("No value provided for scientificName");
		   if (genericEpithet!=null && genericEpithet.trim().length()>0) { 
			   // If we've been given something we know is a parse of the generic part of the scientific name.
			   tempAssembly.append(genericEpithet);
			   if (specificEpithet!=null && specificEpithet.length()>0) { 
				   tempAssembly.append(" ").append(specificEpithet);
				   if (infraspecificEpithet!=null && infraspecificEpithet.length()>0) { 
					   if (verbatimTaxonRank!=null && verbatimTaxonRank.length()>0) { 
						   tempAssembly.append(verbatimTaxonRank).append(" ");
					   } else { 
						   if (taxonRank!=null && taxonRank.toLowerCase().trim().equals("variety")) { 
							   tempAssembly.append("var. ");
						   }
						   if (taxonRank!=null && taxonRank.toLowerCase().trim().equals("form")) { 
							   tempAssembly.append("f. ");
						   }
					   }
					   tempAssembly.append(infraspecificEpithet);
				   }

			   }
			   if (tempAssembly.length()>0) {
				   addToComment("Constructing a scientific name from atomic parts.");
				   addToComment("Constructed: " + tempAssembly);
				   scientificNameToValidate = tempAssembly.toString();
			   }			   
		   } else { 
			   // if we've been given a genus, which may be a parse or may be a current placement
			   if (genus!=null && genus.length()>0) {  
				   tempAssembly.append(genus);
				   if (specificEpithet!=null && specificEpithet.length()>0) { 
					   tempAssembly.append(" ").append(specificEpithet);
					   if (infraspecificEpithet!=null && infraspecificEpithet.length()>0) { 
						   if (verbatimTaxonRank!=null && verbatimTaxonRank.length()>0) { 
							   tempAssembly.append(verbatimTaxonRank).append(" ");
						   } else { 
							   if (taxonRank!=null && taxonRank.toLowerCase().trim().equals("variety")) { 
								   tempAssembly.append("var. ");
							   }
							   if (taxonRank!=null && taxonRank.toLowerCase().trim().equals("form")) { 
								   tempAssembly.append("f. ");
							   }
						   }
						   tempAssembly.append(infraspecificEpithet);
					   }
				   }
			   }
			   if (tempAssembly.length()>0) {
				   addToComment("Constructing a scientific name from genus plus atomic parts.  Depending on the source, this may not be the correct genus.");
				   addToComment("Constructed: " + tempAssembly);
				   scientificNameToValidate = tempAssembly.toString();
			   }
		   }
	   }

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
	   boolean wasFilledIn = false;
	   if (tempAssembly.length()>0) { 
		   setCurationStatus(CurationComment.FILLED_IN);
		   wasFilledIn = true;
	   } else { 
		   setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
	   }

	   if (scientificNameToValidate!=null && scientificNameToValidate.length()>0) { 
		   // If we've got something to validate.
		   String parsedRank = null;

		   // (1a) Check for hybrid (and find rank of input name)
		   NameParser parser = new NameParser();
		   try {
			   ParsedName parse = parser.parse(scientificNameToValidate);
			   if (parse!=null && parse.getRank()!=null) { 
			       parsedRank = parse.getRank().toString();
			   }
			   logger.debug(parsedRank);
		   } catch (UnparsableException e) {
			   parsedRank = "unknown";
			   if (e.getMessage().contains("Name of type HYBRID unparsable")) { 
				   String[] bits = scientificNameToValidate.split(" × ");
				   if (bits.length==2) { 
					   // TODO: Here we could check each of the parts.
				   }
			   }
		   }

		   // (2) perform internal consistency check
		   HashMap<String, String> result1 = SciNameServiceUtil.checkConsistencyToAtomicField(scientificNameToValidate, genus, subgenus, specificEpithet, verbatimTaxonRank, taxonRank, infraspecificEpithet);
		   // TODO: [#384] Comment and status is confusing when atomic values are null 
		   addToComment(result1.get("comment"));
		   setCurationStatus(new CurationStatus(result1.get("curationStatus")));

		   // (3) try to find the name in the supported service.
		   boolean matched = nameSearchAgainstServices(toCheck);
		   logger.debug(matched);

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
				   setCurationStatus(new CurationStatus(result3a.get("curationStatus")));
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
				   setCurationStatus(CurationComment.CORRECT);
			   } else { 
				   if (match.equals(NameComparison.MATCH_SAMEBUTABBREVIATED)) { 
					   addToComment("The scientific name and authorship are probably correct, but with a different abbreviation for the author.  ");
					   setCurationStatus(CurationComment.CORRECT);
				   } else { 
					   validatedNameUsage.setMatchDescription(match);
					   setCurationStatus(CurationComment.CURATED);
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
			   logger.debug(result1.get("scientificName"));
			   HashMap<String, String> result2 = SciNameServiceUtil.checkMisspelling(result1.get("scientificName"));
			   addToServiceName("Global Name Resolver");
			   addToComment(result2.get("comment"));
			   setCurationStatus(new CurationStatus(result2.get("curationStatus")));

			   //System.err.println("step2#"+_id + "#" + System.currentTimeMillis());
			   // (4b) Try GNI and the GBIF backbone taxonomy. 
			   if (result2.get("scientificName") != null){
				   boolean hasResult = validateScientificNameAgainstServices(result2.get("scientificName"), authorToValidate, taxonRank, kingdom, phylum, tclass, order, family);
				   if (hasResult){
					   if(validatedNameUsage.getAuthorship().trim().equals("") || validatedNameUsage.getScientificName().trim().equals("")){
						   // TODO: Handle botanical autonyms, which shouldn't have authorship.
						   setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
						   if (validatedNameUsage.getAuthorship().trim().equals("")) {  addToComment("validated author is empty"); } 
						   if (validatedNameUsage.getScientificName().trim().equals("")) {  addToComment("validated sciName is empty"); } 
					   }else {
						   String validatedAuthor = validatedNameUsage.getAuthorship();
						   String validatedScientificName = validatedNameUsage.getScientificName();
						   if (validatedAuthor.trim().equals(authorToValidate) && validatedScientificName.trim().equals(scientificNameToValidate)) {
							   setCurationStatus(CurationComment.CORRECT);
							   addToComment("The original SciName and Authorship are valid");
						   } else {
							   setCurationStatus(CurationComment.CURATED);
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
							   setCurationStatus(CurationComment.CORRECT);
						   } else { 
							   if (match.equals(NameComparison.MATCH_SAMEBUTABBREVIATED)) { 
								   addToComment("The scientific name and authorship are probably correct, but with a different abbreviation for the author.  ");
								   setCurationStatus(CurationComment.CORRECT);
							   } else if (match.equals(NameComparison.MATCH_ADDSAUTHOR)) { 
								   logger.debug(match);
								   logger.debug(validatedNameUsage.getAuthorship());
								   logger.debug(validatedNameUsage.getAcceptedAuthorship());
								   logger.debug(validatedAuthor);
								   validatedNameUsage.setMatchDescription(match);
								   setCurationStatus(CurationComment.CURATED);
							   }else { 
								   validatedNameUsage.setMatchDescription(match);
								   setCurationStatus(CurationComment.CURATED);
							   }
						   }
						   validatedNameUsage.setAuthorshipStringEditDistance(authorSimilarity);     

						   String authorshipSimilarity = " Authorship: " +  validatedNameUsage.getMatchDescription() + " Similarity: " + Double.toString(authorSimilarity);

						   addToComment(authorshipSimilarity); 
						   
					   }
				   }else{
					   setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
					   addToComment("The original SciName and Authorship cannot be curated");
				   }
			   } else {
				   //no result, stop
			   }
			   
			   // add a comment if the proposed correction differs in rank from the name provided.
			   if (getCurationStatus().toString().equals(CurationComment.CURATED.toString())) { 
				   try {
					   ParsedName cparse = parser.parse(this.getCorrectedScientificName());
					   ParsedName oparse = parser.parse(scientificNameToValidate);
					   if (!cparse.getRank().equals(oparse.getRank())) { 
						   addToComment("Proposed correction is at a different rank than provided name.");
					   }
				   } catch (NullPointerException ex) { 
					   logger.debug(ex.getMessage());
				   } catch (UnparsableException e) {
					   logger.error(e.getMessage());
				   }
			   }
		   } else {
			   //System.err.println("step2not#"+_id + "#" + System.currentTimeMillis());
			   //no result, stop
		   }

	   }
	   //System.err.println("serviceend#"+_id + "#" + System.currentTimeMillis());
       
       if (getCurationStatus().toString().equals(CurationComment.CURATED.toString()) && authorToValidate!=null) { 
    	   // Sanity check on authorship strings with particular meanings
    	   
           // If we've asserted a change of "auct." to an author or "auct. non {author}" to "{author}" we shouldn't
           // but should pass on the original value (and mark the name as suspect, as it can't be tied to a
    	   // nomenclatural act.
    	   if (authorToValidate.equals("auct.") && !this.getCorrectedAuthor().equals(authorToValidate)) { 
    		   addToComment("Retaining original authorship string 'auct.' = of authors, meaning not intended as in the sense of " + this.getCorrectedAuthor());
    		   setCurationStatus(CurationComment.UNABLE_CURATED);
    		   validatedNameUsage.setAuthorship(authorToValidate);
    	   }
    	   if (authorToValidate.equals("auct. non " + this.getCorrectedAuthor())) { 
    		   addToComment("Retaining original authorship string '"+ authorToValidate +"' = of authors not " + this.getCorrectedAuthor() +".");
    		   setCurationStatus(CurationComment.UNABLE_CURATED);
    		   validatedNameUsage.setAuthorship(authorToValidate);
    	   }
    	   if (authorToValidate.equals("auct non " + this.getCorrectedAuthor())) { 
    		   addToComment("Retaining original authorship string '"+ authorToValidate +"' = of authors not " + this.getCorrectedAuthor() +".");
    		   setCurationStatus(CurationComment.UNABLE_CURATED);
    		   validatedNameUsage.setAuthorship(authorToValidate);
    	   }    	   
       }
       
       
       // Handle replacements in taxonomic mode
       if (this.selectedMode.equals(MODE_TAXONOMIC)) { 
    	   if (getCurationStatus().toString().equals(CurationComment.CORRECT.toString())) { 
    		   if (!validatedNameUsage.getScientificName().equals(validatedNameUsage.getAcceptedName()) && validatedNameUsage.getAcceptedName().length()>0) {
    			   setCurationStatus(CurationComment.CURATED);
    			   addToComment("Replacing " + validatedNameUsage.getScientificName() + " with name in current use " + validatedNameUsage.getAcceptedName());                       
    		   }	   
    	   } else {
    		   if (!validatedNameUsage.getScientificName().equals(validatedNameUsage.getAcceptedName()) && validatedNameUsage.getAcceptedName().length()>0) {
    			   addToComment("Replacing " + validatedNameUsage.getScientificName() + " with name in current use " + validatedNameUsage.getAcceptedName());                       
    		   }
    	   }
       }
       if (getCurationStatus().equals(CurationComment.CURATED)) { 
    	   this.addCuratedValue(SpecimenRecord.dwc_scientificName, this.getCorrectedScientificName());
    	   addCuratedValue(SpecimenRecord.dwc_scientificNameAuthorship, this.getCorrectedAuthor());
    	   // TODO: Add other fields
       }
       
       if (getCurationStatus().equals(CurationComment.CORRECT) && wasFilledIn && (genericEpithet==null || genericEpithet.trim().length()==0)) { 
    	   // If we filled in a value and it was correct, we could change the state to assert that we filled it in.
    	   // It is important that we assert filled in rather than correct here, as we are probably in this state 
    	   // because the scientificName has been populated from genus+specificEpithet+rank+infraspecificEpithet, 
    	   // and dwc:genus is defined as current generic placement, and not defined as the genus parsed from 
    	   // the scientificName.  If a genericEpithet term becomes available, and we assemble scientificName from
    	   // that instead of dwc:genus, then we can assert correct instead here.
    	   this.setCurationStatus(CurationComment.FILLED_IN);
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
                setCurationStatus(new CurationStatus(result2.get("curationStatus")));

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
                    setCurationStatus(CurationComment.UNABLE_CURATED);
                    addToComment("Found a name in the same lexical group as the searched scientific name but failed to find this name in remote service.");
                }else{
                	// TODO: Not sure that we need to set this, nameSearchAgainstServices should set it.
                    //correct the wrong scientific name or author by searching in both IPNI and GNI
                    //validatedNameUsage.setScientificName(resolvedScientificName);
                    //validatedNameUsage.setAuthorship(resolvedScientificNameAuthorship);
                    //GBIF_name_GUID = constructGBIFGUID(id);
                    addToComment("Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.");
                    setCurationStatus(CurationComment.CURATED);
                }
                return true;
            }
        }else{
            return true;
        }

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
    public String getGUID(){
        return validatedNameUsage.getGuid();
    }
    
    @Override
    public String getCorrectedKingdom(){
        return validatedNameUsage.getKingdom();
    }    

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.INewScientificNameValidationService#getCorrectedPhylum()
	 */
	@Override
	public String getCorrectedPhylum() {
        return validatedNameUsage.getPhylum();
	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.INewScientificNameValidationService#getCorrectedOrder()
	 */
	@Override
	public String getCorrectedOrder() {
        return validatedNameUsage.getOrder();
	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.INewScientificNameValidationService#getCorrectedClass()
	 */
	@Override
	public String getCorrectedClass() {
        return validatedNameUsage.getClazz();
	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.INewScientificNameValidationService#getCorrectedFamily()
	 */
	@Override
	public String getCorrectedFamily() {
        return validatedNameUsage.getFamily();
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
    			CacheValue newValue = new SciNameCacheValue().setHasResult(hasResult).setAuthor(validatedAuthor).setTaxon(validatedScientificName).setComment(getComment()).setStatus(getCurationStatus()).setSource(getServiceName());
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
	
	@Override
	public List<List> getLog() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUseCache(boolean use) {
		// TODO Auto-generated method stub
		
	}	

}
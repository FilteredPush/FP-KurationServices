package org.filteredpush.kuration.services.sciname;


import edu.harvard.mcz.nametools.AuthorNameComparator;
import edu.harvard.mcz.nametools.NameUsage;

import java.util.HashMap;
import java.util.Vector;

import org.filteredpush.kuration.interfaces.INewScientificNameValidationService;
import org.filteredpush.kuration.util.*;


/**
 * Parent class for validation of Scientific Names against taxonomic or nomenclatural
 * services, with a failover to lookup in GNI and check of lexical group against services.  
 * 
 * @author Paul J. Morris
 *
 */
public abstract class SciNameServiceParent implements INewScientificNameValidationService {

	public abstract NameUsage validate(NameUsage taxonNameUsage);
    
    protected abstract boolean nameSearchAgainstServices(NameUsage toCheck); 

    protected CurationStatus curationStatus;
    protected String validatedScientificName = null;
    protected String validatedAuthor = null;
    protected String comment = "";
    protected String serviceName;
    private String GBIF_name_GUID = "";
    private final static String GBIF_GUID_Prefix = "http://api.gbif.org/v1/species/";
    // Documented at http://api.gbif.org/

    protected static HashMap<String, CacheValue> sciNameCache = new HashMap<String, CacheValue>();
    boolean useCache = true;
    protected static int count = 0;

   public void validateScientificName(String scientificName, String author){
	   validateScientificName(scientificName, author, "", "", "", "", "", "", "", "", "", "", "");
   }

   //public void validateScientificName(String scientificNameToValidate, String authorToValidate, String rank, String kingdom, String phylum, String tclass, String genus, String subgenus, String verbatimTaxonRank, String infraspecificEpithe){
   public void validateScientificName(String scientificNameToValidate, String authorToValidate, String genus, String subgenus, String specificEpithet, String verbatimTaxonRank, String infraspecificEpithet, String taxonRank, String kingdom, String phylum, String tclass, String order, String family){

	   NameUsage toCheck = new NameUsage();
	   toCheck.setOriginalScientificName(scientificNameToValidate);
	   toCheck.setOriginalAuthorship(authorToValidate);
	   toCheck.setKingdom(kingdom);
       //System.err.println("servicestart#"+_id + "#" + System.currentTimeMillis());
       comment = "";
       GBIF_name_GUID = "";
       //to carry over the orignial sciname and author
       serviceName = "scientificName:"+ scientificNameToValidate + "#scientificNameAuthorship:" + authorToValidate + "#";
       //curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;

       //try to find information from the cached file
       //if failed, then access GBIF service, or if that fails, GNI service


       //try to find it in GBIF service failing over to check against GNI
       //validateScientificNameAgainstServices(scientificNameToValidate, authorToValidate, key, rank, kingdom, phylum, tclass);
       NameUsage returned = validate(toCheck);
       if (returned!=null) { 
           validatedScientificName = returned.getScientificName();
           validatedAuthor = returned.getAuthorship();
       } 
       
      // validatedAuthor = authorToValidate;
       curationStatus = CurationComment.CORRECT;
       // start with consistency check
       HashMap<String, String> result1 = SciNameServiceUtil.checkConsistencyToAtomicField(scientificNameToValidate, genus, subgenus, specificEpithet, verbatimTaxonRank, taxonRank, infraspecificEpithet);
       comment = result1.get("comment");
       curationStatus = new CurationStatus(result1.get("curationStatus"));

       if (returned!=null) { 
           comment = comment + returned.getMatchDescription();
       }
           
       //System.err.println("step1#"+_id + "#" + System.currentTimeMillis());

       // second check misspelling
       if (result1.get("scientificName") != null){
           HashMap<String, String> result2 = SciNameServiceUtil.checkMisspelling(result1.get("scientificName"));
           serviceName = serviceName + "Global Name Resolver";
           comment = comment + result2.get("comment");
           curationStatus = new CurationStatus(result2.get("curationStatus"));

           //System.err.println("step2#"+_id + "#" + System.currentTimeMillis());
           // third, go to GBIF checklist bank
           if (result2.get("scientificName") != null){
               boolean hasResult = validateScientificNameAgainstServices(result2.get("scientificName"), authorToValidate, taxonRank, kingdom, phylum, tclass, order, family);
               if (hasResult){
                   if(validatedAuthor.trim().equals("") || validatedScientificName.trim().equals("")){
                       curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
                       if (validatedAuthor.trim().equals("")) comment = comment + " | validated author is empty";
                       if (validatedScientificName.trim().equals("")) comment = comment + " | validated sciName is empty";
                   }else {
                       if (validatedAuthor.trim().equals(authorToValidate) && validatedScientificName.trim().equals(scientificNameToValidate)) {
                           curationStatus = CurationComment.CORRECT;
                           comment = comment + " | The original SciName and Authorship are valid";
                       } else {
                           curationStatus = CurationComment.CURATED;
                           comment = comment + " | The original SciName and Authorship are curated";
                       }
                   }
               }else{
                   curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
                   comment = comment + " | The original SciName and Authorship cannot be curated";
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
   }

    private boolean validateScientificNameAgainstServices(String taxon, String author, String taxonRank, String kingdom, String phylum, String tclass, String order, String family){
        boolean failedAtGNI = false;
        //System.err.println("remotestart#"+_id + "#" + System.currentTimeMillis());
        NameUsage toCheck = new NameUsage();
        toCheck.setOriginalScientificName(taxon);
        toCheck.setOriginalAuthorship(author);
        boolean hasResult = nameSearchAgainstServices(toCheck);
        //System.err.println("remoteend#"+_id + "#" + System.currentTimeMillis());
        if(!hasResult){
            // no match was found, over to GNI
            serviceName = serviceName + " | Global Name Index";
            // access the GNI and try to get the name that is in the lexical group and from IPNI
            Vector<String> resolvedNameInfo = null;
            try {
                resolvedNameInfo = GNISupportingService.resolveDataSourcesNameInLexicalGroupFromGNI(taxon);
            } catch (CurationException e) {
                comment = comment + " | Fail to access GNI service";
                failedAtGNI = true;
                //return false;
            }

            if(failedAtGNI || resolvedNameInfo == null || resolvedNameInfo.size()==0){
                //failed to find it in GNI
                if(!failedAtGNI){
                    comment = comment + " | Can't find the scientific name and authorship by searching the lexical group in GNI.";
                }//failover to GBIF Checklistbank Backbone
                HashMap<String, String> result2 = SciNameServiceUtil.checklistBankNameSearch(taxon, author, taxonRank, kingdom, phylum, tclass, order, family, "d7dddbf4-2cf0-4f39-9b2a-bb099caae36c");
                serviceName = serviceName + " | GBIF CheckListBank Backbone";
                comment = comment + result2.get("comment");
                curationStatus = new CurationStatus(result2.get("curationStatus"));

                if(result2.get("scientificName") != null){
                    validatedScientificName = result2.get("scientificName");
                    validatedAuthor = result2.get("author");
                    comment = comment + " | Got a valid result from GBIF checklistbank Backbone";
                    GBIF_name_GUID = GBIF_GUID_Prefix + result2.get("guid");
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
                    comment = comment + " | Found a name in the same lexical group as the searched scientific name but failed to find this name in remote service.";
                }else{
                    //correct the wrong scientific name or author by searching in both IPNI and GNI
                    validatedScientificName = resolvedScientificName;
                    validatedAuthor = resolvedScientificNameAuthorship;
                    //GBIF_name_GUID = constructGBIFGUID(id);
                    comment = comment + " | Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.";
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
        return validatedScientificName;
    }

    @Override
    public String getCorrectedAuthor(){
        return validatedAuthor;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public String getGUID(){
        return GBIF_name_GUID;
    }

    @Override
    public String getServiceName() {
        return serviceName;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected String getKey(String name, String author){
        return name+author;
    }

    protected void addToCache(boolean hasResult){
        String key = getKey(validatedScientificName, validatedAuthor);
        CacheValue newValue = new SciNameCacheValue().setHasResult(hasResult).setAuthor(validatedAuthor).setTaxon(validatedScientificName).setComment(comment).setStatus(curationStatus).setSource(serviceName);
        if(!sciNameCache.containsKey(key)) sciNameCache.put(key, newValue);
        else {
            if (!sciNameCache.get(key).equals(newValue)) {
                //need to handle the case where the cached value is different than the new value
            }
        }
    }

	@Override
	public AuthorNameComparator getAuthorNameComparator(String authorship,
			String kingdom) {
		return AuthorNameComparator.authorNameComparatorFactory(authorship, kingdom);
	}

}
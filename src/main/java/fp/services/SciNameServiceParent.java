package fp.services;


import fp.util.CurationComment;
import fp.util.CurationStatus;
import fp.util.CurrationException;
import fp.util.SciNameServiceUtil;

import java.util.HashMap;
import java.util.Vector;


/**
 * Validate Scientific Names against GBIF's Checklist Bank web services, with 
 * a failover to validation against the GNI.
 * 
 * @author Paul J. Morris
 *
 */
public class SciNameServiceParent implements INewScientificNameValidationService {


   protected CurationStatus curationStatus;
   protected String validatedScientificName = null;
   protected String validatedAuthor = null;
   protected String comment = "";
   protected String serviceName;


   public void validateScientificName(String scientificName, String author){
	   validateScientificName(scientificName, author, "", "", "", "", "", "", "", "", "", "", "");
   }

   //public void validateScientificName(String scientificNameToValidate, String authorToValidate, String rank, String kingdom, String phylum, String tclass, String genus, String subgenus, String verbatimTaxonRank, String infraspecificEpithe){
   public void validateScientificName(String scientificNameToValidate, String authorToValidate,String genus, String subgenus, String specificEpithet, String verbatimTaxonRank, String infraspecificEpithet, String taxonRank, String kingdom, String phylum, String tclass, String order, String family){
       comment = "";
       serviceName = "";
       curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;

       //try to find information from the cached file
       //if failed, then access GBIF service, or if that fails, GNI service


       //try to find it in GBIF service failing over to check against GNI
       //validateScientificNameAgainstServices(scientificNameToValidate, authorToValidate, key, rank, kingdom, phylum, tclass);

      // validatedAuthor = authorToValidate;
       curationStatus = CurationComment.CORRECT;
       // start with consistency check
       HashMap<String, String> result1 = SciNameServiceUtil.checkConsistencyToAtomicField(scientificNameToValidate, genus, subgenus, specificEpithet, verbatimTaxonRank, taxonRank, infraspecificEpithet);
       comment = result1.get("comment");
       curationStatus = new CurationStatus(result1.get("curationStatus"));
       serviceName = "";

       // second check misspelling
       if (result1.get("scientificName") != null){
           HashMap<String, String> result2 = SciNameServiceUtil.checkMisspelling(result1.get("scientificName"));
           serviceName = serviceName + "Global Name Resolver";
           comment = comment + result2.get("comment");
           curationStatus = new CurationStatus(result2.get("curationStatus"));

           // third, go to GBIF checklist bank
           if (result2.get("scientificName") != null){
               boolean hasResult = validateScientificNameAgainstServices(result2.get("scientificName"), authorToValidate, taxonRank, kingdom, phylum, tclass, order, family);
               if (hasResult){
                   if (validatedAuthor.trim().equals(authorToValidate) && validatedScientificName.trim().equals(scientificNameToValidate)){
                       curationStatus = CurationComment.CORRECT;
                       comment = comment + " | The original SciName and Authorship are valid";
                   }else{
                       curationStatus = CurationComment.CURATED;
                       comment = comment + " | The original SciName and Authorship are curated";
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
           //no result, stop
       }

   }

    private boolean validateScientificNameAgainstServices(String taxon, String author, String taxonRank, String kingdom, String phylum, String tclass, String order, String family){
        boolean failedAtGNI = false;
        boolean hasResult = nameSearchAgainstServices(taxon);
        if(!hasResult){
            // no match was found, over to GNI
            serviceName = serviceName + " | Global Name Index";
            // access the GNI and try to get the name that is in the lexical group and from IPNI
            Vector<String> resolvedNameInfo = null;
            try {
                resolvedNameInfo = GNISupportingService.resolveDataSourcesNameInLexicalGroupFromGNI(taxon);
            } catch (CurrationException e) {
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
                    return true;
                }else{
                    return false;
                }

            }else{
                //find it in GNI
                String resolvedScientificName = resolvedNameInfo.get(0);
                String resolvedScientificNameAuthorship = resolvedNameInfo.get(1);

                //searching for this name in GNI again.
                boolean hasResult2 = nameSearchAgainstServices(resolvedScientificName);
                if(!hasResult2){
                    //failed to find the name got from GNI in the IPNI
                    curationStatus = CurationComment.UNABLE_CURATED;
                    comment = comment + " | Found name which is in the same lexical group as the searched scientific name but failed to find this name really in remote service.";
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

    protected boolean nameSearchAgainstServices(String name){
        return true;
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
    public String getServiceName() {
        return serviceName;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
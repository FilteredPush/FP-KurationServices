package org.filteredpush.kuration.util;


import edu.harvard.mcz.nametools.NameUsage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.services.sciname.GNISupportingService;
import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.api.vocabulary.Rank;
import org.gbif.nameparser.NameParser;
import org.gbif.nameparser.UnparsableException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;


/**
 * Validate Scientific Names against GBIF's Checklist Bank web services, with 
 * a failover to validation against the GNI.
 * 
 * @author Paul J. Morris
 *
 */
public class SciNameServiceUtil {
	
	private static final Log logger = LogFactory.getLog(SciNameServiceUtil.class);

    private boolean useCache = false;
   private File cacheFile = null;
   private HashMap<String, HashMap<String,String>> cachedScientificName;
   private Vector<String> newFoundScientificName;
   private static final String ColumnDelimiterInCacheFile = "\t";
    private static final String GBIF_SERVICE = "http://api.gbif.org/v1";

   //private CurationStatus curationStatus;
   //private String validatedScientificName = null;
   //private String validatedAuthor = null;
   //private String GBIF_name_GUID = null;
   //private String comment = "";

    /**
     * Check to see if a scientific name is an autonym 
     * (trinomial where specific and infraspecific epithets are the same).
     * 
     * @param scientificName
     * @return
     */
    public static boolean isAutonym(String scientificName) {
    	boolean result = false;
    	if (scientificName!=null) { 
		NameParser parser = new NameParser();
		ParsedName nameBits = null;
		try {
			nameBits = parser.parse(scientificName);
			result = nameBits.isAutonym();
		} catch (UnparsableException e) {
			logger.debug(e.getMessage());
		}
    	}
		return result;
    }

   /**
    * Retrieve usages from Checklist Bank by name ID.  See documentation at http://ecat-dev.gbif.org/api/clb
    */
   private final static String GBIF_GUID_Prefix = "http://ecat-dev.gbif.org/ws/usage/?nid=";
   // Above ecat-dev uri is experimental and targeted for removal in Dec 2013.
   // Use instead: http://api.gbif.org/dev
   // Documented at http://dev.gbif.org/wiki/display/POR/Webservice+API

   private  String serviceName;

   private JSONParser parser;

   /**
    * Default no-argument constructor.
    */
   public SciNameServiceUtil(){
	   parser = new JSONParser();
   }


    public static HashMap<String, String> checkConsistencyToAtomicField(String scientificName, String genus, String subgenus, String specificEpithet, String verbatimTaxonRank, String taxonRank, String infraspecificEpithet){
        CurationStatus curationStatus = null;
        String comment = "";
        String name = "";
        HashMap<String, String> resultMap = new HashMap <String, String>();

        if (genus != null && !genus.equals("") && specificEpithet != null && !specificEpithet.equals("") && infraspecificEpithet != null && !infraspecificEpithet.equals("")){
            String constructName= genus + " ";

            if (subgenus != null){
                if(!subgenus.equals("")) constructName += subgenus + " ";
            }
            if (!specificEpithet.equals("")) constructName = constructName + specificEpithet + " ";

            if (verbatimTaxonRank != null) constructName = constructName + verbatimTaxonRank + " ";
            else if (taxonRank != null) constructName = constructName + taxonRank + " ";

            constructName = constructName + infraspecificEpithet;


            NameParser parser = new NameParser();
            ParsedName pn = null;
            ParsedName cn = null;
            //System.out.println("scientificName = " + scientificName);
            //System.out.println("constructName = " + constructName);
            try {
                //System.out.println("constructName111 = " + constructName.trim());
                //System.out.println("scientificName = " + scientificName.equals(constructName.trim()));
                pn = parser.parse(scientificName);
            } catch (UnparsableException e) {
            	// Hybrids are a likely cause of failure, put this into the output.
                System.out.println("Parsing error: " + e);
                comment = comment + " | " +  e.getMessage();
            }
            try { 
                cn = parser.parse(constructName.trim());
            } catch (UnparsableException e) {
                System.out.println("Parsing error: " + e);
            }

            if(cn != null && pn != null && !cn.getGenusOrAbove().equals(pn.getGenusOrAbove())){
                //add the following line in order to handle dwc:genus and dwc:subgenus
                //check against global name resolver to check whether this genus exist
                HashMap<String, String> result2 = SciNameServiceUtil.checkMisspelling(pn.getGenusOrAbove());
                CurationStatus returnedStatus = new CurationStatus(result2.get("curationStatus"));
                if(returnedStatus.equals(CurationComment.CORRECT) || returnedStatus.equals(CurationComment.CURATED)){
                    pn.setGenusOrAbove(result2.get("scientificName"));
                    comment = comment + "| Genus in SciName is not consistent to atomic field, genus has been changed to dwc:Genus: \"" + genus + "\"";
                    //todo: need to handle overwritten status
                }else{
                    cn.setGenusOrAbove(pn.getGenusOrAbove());
                    comment += " | Genus in SciName is not consistent to atomic field, but dwc:Genus: \"" + genus + "\" cannot be found in Global Name Resolver";
                }
            }


            if(pn == null){
                if(cn == null){
                    curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
                    comment = comment + "| cannot get a valid scientificName from the record";
                    //name =  pn.canonicalName();
                    //validatedAuthor = pn.getAuthorship();
                } else{
                    //validatedAuthor = null;
                    curationStatus = CurationComment.CURATED;
                    comment = comment + "| scientificName " + constructName +  " has been constructed from atomic fields";
                    name = constructName;
                }
            }else{
                if(pn.equals(cn)){
                    curationStatus = CurationComment.CORRECT;
                    comment = comment + "| scientificName is consistent with atomic fields";
                    name = pn.canonicalName();
                    //validatedAuthor = pn.getAuthorship();
                } else{

                    if(cn != null){
                        //validatedAuthor = null;
                        curationStatus = CurationComment.UNABLE_CURATED;
                        comment = comment + "| scientificName is inconsistent with atomic fields";
                        name = null;
                    }else{


                        curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
                        name =  scientificName;
                    }
                }
            }

        }else{
            curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
            if (scientificName==null || scientificName.trim().length()==0) { 
                comment = "No scientific name was provided in either the scientific name or atomic fields.";
            } else { 
                comment = "Atomic fields for scientific name are blank, nothing to compare with scientific name.";
            }
            name =  scientificName;
        }

        resultMap.put("scientificName", name);
        resultMap.put("curationStatus", curationStatus.toString());
        resultMap.put("comment", comment);
        resultMap.put("source", null);
        return resultMap;
    }

    /**
     * Search for potential scientific name matches on the global names resolver service.
     * 
     * @param name scientific name to look for in the global names resolver.
     * @return a hashmap with keys scientificName curationStatus and comment.
     */
    public static HashMap checkMisspelling (String name){

    	logger.debug("Checking " + name + " against global names resolver.");
    	
        CurationStatus curationStatus = null;
        String comment = "";
        String resultName = null;
        HashMap<String, String> resultMap = new HashMap <String, String>();

        StringBuilder result = new StringBuilder();
        URL url;
        try {
            String uname = name.replace(" ", "+");
            url = new URL("http://resolver.globalnames.org/name_resolvers.json?names=" + uname +"&resolve_once=true");
            //System.out.println(url);
            URLConnection connection = url.openConnection();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                //System.out.println("line = " + line);
                result.append(line);
            }
        } catch (IOException e) {
        	System.out.println(e.getMessage());
        	logger.error(e.getMessage(),e);
        }


        JSONParser parser = new JSONParser();
        JSONObject last = new JSONObject();
        try {
            //System.out.println("result = " + result.toString());
            JSONObject object = (JSONObject)parser.parse(result.toString());
            //System.out.println("object = " + object.toString());

            JSONArray jdata = (JSONArray)object.get("data");
            JSONArray jresults = null;
            if (jdata!=null) { 
                JSONObject jone = (JSONObject)jdata.get(0);
                if (jone!=null) { 
                    jresults = (JSONArray)jone.get("results");
                }
            }
            //if there is no possible correction, return now
            if (jresults == null){
                //comment = comment + " | the name is misspelled and cannot be corrected.";
                comment = comment + " | the provided name cannot be found in GlobalNames Resolver";
                resultMap.put("scientificName", null);
                resultMap.put("curationStatus", CurationComment.UNABLE_CURATED.toString());
                resultMap.put("comment", comment);
                return resultMap;
            }
            last = (JSONObject)jresults.get(0);
            //System.out.println("last = " + last.toString());

        } catch (ParseException e) {
            comment = comment + " | cannot get result from GlobalNames resolver due to error";
            resultMap.put("scientificName", null);
            resultMap.put("curationStatus", CurationComment.UNABLE_DETERMINE_VALIDITY.toString());
            resultMap.put("comment", comment);
            return resultMap;
        }
        double score = Double.parseDouble(getValFromKey(last,"score"));
        int type = Integer.parseInt(getValFromKey(last,"match_type"));
        String resolvedName = getValFromKey(last,"name_string");
    	/*
    	if (resolvedName != "" ){
    		if (includeAuthor == false)
    		System.out.println("Changed to current name: " + resolvedName +"**");
		} else {
			resolvedName = getValFromKey(last,"name_string");
		}
		*/
        //System.out.println(score);
        //System.out.println(type);
        //System.out.println(resolvedName);

        //if not exact match, print out reminder
        if (type > 2){
            if (score > 0.9){
            	boolean found = false;
               NameParser nparser = new NameParser();
     		   try {
     			   ParsedName iparse = nparser.parse(name);
     			   String inputRank = iparse.getRank().toString();
     			   logger.debug(inputRank);
     			   ParsedName oparse = nparser.parse(resolvedName);
     			   String outputRank = oparse.getRank().toString();
     			   logger.debug(outputRank);
     			   if (inputRank.equals(outputRank)) {
     				   // good match at same rank
     				   found = true;   
     			   } else if (Rank.SPECIES_OR_BELOW.contains(inputRank) && outputRank.equals(Rank.SPECIES) && !inputRank.equals(Rank.SPECIES)) {
     				   // match, but output is a species and input was below species (likely match on parent of infraspecific rank, not same name)
     				   logger.debug(iparse.getScientificName());
     				   logger.debug(oparse.getScientificName());
     				   if (iparse.getInfraSpecificEpithet().equals(oparse.getSpecificEpithet())) { 
     					   // likely match on infraspecific name treated as a species.
     					   found = true;
     				   }
     			   } else { 
     				   logger.debug(iparse.getScientificName());
     				   logger.debug(oparse.getScientificName());
     			   }
     		   } catch (UnparsableException e) {
     			   logger.error("unable to parse name " + e.getMessage());
     		   }
     		   if (found) { 
                   //System.out.println("The provided name: \"" + name + "\" is misspelled, changed to \"" + resolvedName + "\".");
                   comment = comment + " | The provided name: " + name + " is misspelled, changed to " + resolvedName;
                   curationStatus = CurationComment.CURATED;
     		   } else { 
                   curationStatus = CurationComment.UNABLE_CURATED;
                   comment = comment + " | The provided name: " + name + " cannot be found in GlobalNames Resolver";
                   resolvedName = null;
     		   }
            } else {
                //System.out.println("The provided name: \"" + name + "\" has spelling issue, changed to \"" + resolvedName + "\" for now.");
                //System.out.println("The provided name: \"" + name + "\" has spelling issue and it cannot be curated");
                curationStatus = CurationComment.UNABLE_CURATED;
                comment = comment + " | The provided name: " + name + " cannot be found in GlobalNames Resolver";
                resolvedName = null;
            }
        }else{
            comment = comment + " | The provided name: " + name + " has a match in the GlobalNames Resolver";
            curationStatus = CurationComment.CORRECT;
        }

        resultMap.put("scientificName", resolvedName);
        resultMap.put("curationStatus", curationStatus.toString());
        resultMap.put("comment", comment);
        return resultMap;
    }

    public static String getValFromKey(JSONObject json, String key) {
        if (json==null || json.get(key)==null) {
            return "";
        } else {
            return json.get(key).toString();
        }
    }


   /**
    * Resolve a taxon name with the GBIF checklist bank service: http://ecat-dev.gbif.org/api/clb
    *
    * @param taxon
    * @param author
    * @param rank one letter rank code for the gbif service rank constraint.
    * @return the GBIF ChecklistBank ID for the name if found, otherwise null.
    * //@throws ptolemy.kernel.util.CurrationException
    */
   public static HashMap<String, String> checklistBankNameSearch(String taxon, String author, String rank, String kingdom, String phylum, String tclass, String order, String family, String datasetKey) {

        CurationStatus curationStatus = CurationComment.CORRECT;
        String comment = null;
        String resultName = null;
        String resultAuthor = null;
        HashMap<String, String> resultMap = new HashMap <String, String>();
       //todo: add dataset selection code
       //taxon = "Amaranthus retroflexus";

       //for selecting one result in a good dataset
      // HashSet<String> authoritativeDatasets = new HashSet<String>();
       //authoritativeDatasets.add("2d59e5db-57ad-41ff-97d6-11f5fb264527");    //WORMS
      // authoritativeDatasets.add("046bbc50-cae2-47ff-aa43-729fbf53f7c5");    //IPNI
       //authoritativeDatasets.add("bf3db7c9-5e5d-4fd0-bd5b-94539eaf9598");    //INDEXFUNGORUM
      // authoritativeDatasets.add("7ddf754f-d193-4cc9-b351-99906754a03b");    //Catalog of Life
       // authoritativeDatasets.add("d7dddbf4-2cf0-4f39-9b2a-bb099caae36c ");  //backbone


       //String key = searchBackbone(taxon);
       //todo: need to handle result in multiple datsets
       HashSet<NameUsage> nameUsageSet = fetchTaxonToUsage(taxon, datasetKey);

       if (nameUsageSet.size() < 1){
           //System.out.println("No match");
           resultMap.put("scientificName", null);
           resultMap.put("curationStatus", CurationComment.UNABLE_DETERMINE_VALIDITY.toString());
           resultMap.put("comment", "| Can't determine validity due to empty results");
           return resultMap;
       } else{
           ////////////////////start of resolveHomonyms  /////////////////////////
           if (nameUsageSet.size() > 1){ //homonyms
               HashSet<NameUsage> deletingNameSet = new HashSet<NameUsage>();
               for (NameUsage name : nameUsageSet)  {

                   //System.out.println("name.getScientificName() = " + name.getScientificName());
                   //System.out.println("name.getAuthorship() = " + name.getAuthorship());
                   //System.out.println("author = " + author);

                   //check Hemihomonyms and higher taxon
                   boolean keepGoing = true;
                   if (rank != null){
                       if (!name.getRank().equals(rank)) keepGoing = false;
                   }
                   if (keepGoing && kingdom != null) {
                       if (!name.getKingdom().equals(kingdom)) keepGoing = false;
                   }
                   if (keepGoing && phylum != null){
                       if (!name.getPhylum().equals(phylum)) keepGoing = false;
                   }
                   if (keepGoing && tclass != null){
                       if (!name.getTclass().equals(tclass)) keepGoing = false;
                   }
                   if (keepGoing && order != null){
                       if (!name.getOrder().equals(order)) keepGoing = false;
                   }
                   if (keepGoing && family != null){
                       if (!name.getFamily().equals(family)) keepGoing = false;
                   }
                   //System.out.println("keepGoing = " + keepGoing);
                   if (keepGoing && author != null){   //todo: may need better algorithm for matching two authorship
                       if (!name.getAuthorship().contains(author) &&
                               !author.contains(name.getAuthorship())) keepGoing = false;
                   }

                   //System.out.println("check: " + !name.getAuthorship().contains(author));
                   //System.out.println("keepGoing = " + keepGoing);


                   //adding the nameUsage not matching the working taxon
                   if(!keepGoing) deletingNameSet.add(name);

               }

               //after enumerating all the names, check whether solved or not
               //still not unique
               if (nameUsageSet.size() - deletingNameSet.size() > 1){
                   curationStatus = CurationComment.UNABLE_CURATED;
                   comment = comment + " | homonyms detected but cannot be resolved";
                   nameUsageSet = null;
               }else{   //already unique
                   //curationStatus = CurationComment.CURATED;
                   comment = comment + " | homonyms resolved ";
                   //System.out.println("deletingNameSet.size() = " + deletingNameSet.size());
                   //System.out.println("originalSet.size() = " + originalSet.size());
                   nameUsageSet.removeAll(deletingNameSet); //remove all the badNames
                   //System.out.println("originalSet.size() = " + originalSet.size());
                   if (nameUsageSet.size() == 0){
                       curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
                       comment = comment + " | homonyms detected but none of the results matches the record";
                       nameUsageSet = null;
                   }
                }
           }
           /////////////end of resolveHomonyms////////////////////

           if(nameUsageSet == null){  //cannot solve homonyms
               comment = comment + " | Cannot solve homonyms";
               resultMap.put("scientificName", null);
               resultMap.put("curationStatus", CurationComment.UNABLE_DETERMINE_VALIDITY.toString());
               resultMap.put("comment", comment);
               return resultMap;
           }else{  //solved homonyms
        	   for (NameUsage name : nameUsageSet){
        		   //first get the name form the result

        		   logger.debug(name.getAcceptedName());            	   
        		   logger.debug(name.getAcceptedAuthorship());            	   
        		   logger.debug(name.getScientificName());            	   
        		   logger.debug(name.getAuthorship()); 

        		   boolean isSynonyms = name.getSynonyms();
        		   if (isSynonyms){
        			   try{
        				   Vector<String> nameBits = GNISupportingService.parseName(name.getAcceptedName());
        				   if (nameBits.size()==2) {
        					   //todo check whether the match is senior or junior synonyms?
        					   resultName = nameBits.get(0);
        					   resultAuthor = nameBits.get(1);
        					   curationStatus = CurationComment.CURATED;
        					   comment = comment + " | found synonyms and synonyms have been resolved";
        				   }else{
        					   throw new CurationException("can't solve synonyms");
        				   }
        			   }catch (CurationException e){
        				   comment = comment + " | found synonyms but can't parse accepted name";
        				   resultMap.put("scientificName", null);
        				   resultMap.put("curationStatus", CurationComment.UNABLE_DETERMINE_VALIDITY.toString());
        				   resultMap.put("comment", comment);
        				   return resultMap;
        			   }

        		   } else{
        			   resultName = name.getCanonicalName();
        			   resultAuthor = name.getAuthorship();
        		   }
        		   /*
                   System.out.println("taxon = " + taxon);
                   System.out.println("author = " + author);
                   System.out.println("validatedAuthor = " + resultAuthor);
                   System.out.println("validatedScientificName = " + resultName);
                   System.out.println("curationStatus = " + curationStatus);

                   if (resultAuthor.trim().equals(author) && resultName.trim().equals(taxon)){
                       curationStatus = CurationComment.CORRECT;
                       comment = comment + " | The original SciName and Authorship are valid after checking with GBIF checklist bank";
                   }else{
                       resultName = name.getCanonicalName();
                       resultAuthor = name.getAuthorship();
                       curationStatus = CurationComment.CURATED;
                       comment = comment + " | Curated by searching GBIF checklist bank API";
                   }
        		    */

        		   resultMap.put("scientificName", resultName);
        		   resultMap.put("author", resultAuthor);
        		   resultMap.put("curationStatus", curationStatus.toString());
        		   resultMap.put("comment", comment);
        		   resultMap.put("guid", Integer.toString(name.getKey()));
        		   return resultMap;
        	   }
           }
       }
       System.out.println("something wrong...");
       return null;
   }



    private static HashSet fetchTaxonToUsage(String taxon, String targetChecklist) {
        HashSet<NameUsage> nameUsageSet = new HashSet<NameUsage>();
        StringBuilder result = new StringBuilder();
        String datasetKey = "";
        if (targetChecklist!=null) {
            datasetKey = "datasetKey=" + targetChecklist;
        }
        URL url = null;
        try {
            url = new URL(GBIF_SERVICE + "/species?name=" + taxon.replace(" ", "%20") + "&limit=100&" + datasetKey);
            //System.out.println("url.toString() = " + url.toString());
            URLConnection connection = url.openConnection();

            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            //System.out.println("result = " + result.toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("---sciNameServiceUtil---");
            logger.error(e.getMessage(),e);
            System.out.println("url = " + url.toString());
            System.out.println("========================");
        }
        //start parsing
        JSONParser parser=new JSONParser();
        try {
            JSONArray array = new JSONArray();
            try {
                JSONObject o = (JSONObject)parser.parse(result.toString());
                if (o.get("results")!=null) {
                    array = (JSONArray)o.get("results");
                } else {
                    // array = (JSONArray)parser.parse(json);
                    System.out.println("no result in json object");
                }
            } catch (ClassCastException e) {
            	logger.debug(e.getMessage(), e);
            }

            Iterator i = array.iterator();
            while (i.hasNext()) {
                JSONObject obj = (JSONObject)i.next();
                //System.out.println(obj.toJSONString());
                NameUsage name = new NameUsage(obj);
                //System.out.println("name = " + name.getCanonicalName());
                //System.out.println("targetName = " + taxon);
                //System.out.println("name = " + name.getAuthorship());
                //System.out.println("name.getDatasetKey() = " + name.getDatasetKey());
                // if (name.getCanonicalName().equals(taxon)) {
                //return name;
                nameUsageSet.add(name);

            }

        } catch (ParseException e) {
            logger.debug(e.getMessage(), e);
        }   catch (Exception e){
        	logger.error("parsing error url = " + url.toString());
        	logger.error(e.getMessage());
        }
        return nameUsageSet;
    }


}

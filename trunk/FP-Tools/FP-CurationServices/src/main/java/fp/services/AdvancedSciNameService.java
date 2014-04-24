package fp.services;


import fp.util.CurationComment;
import fp.util.CurationStatus;
import fp.util.CurrationException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Soundex;
import org.gbif.api.model.checklistbank.NameUsage;
import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.nameparser.NameParser;
import org.gbif.nameparser.UnparsableException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;




/**
 * Validate Scientific Names against GBIF's Checklist Bank web services, with 
 * a failover to validation against the GNI.
 * 
 * @author Paul J. Morris
 *
 */
public class AdvancedSciNameService implements IAdvancedScientificNameValidationService {

    private boolean useCache = false;
   private File cacheFile = null;
   private HashMap<String, HashMap<String,String>> cachedScientificName;
   private Vector<String> newFoundScientificName;
   private static final String ColumnDelimiterInCacheFile = "\t";
    private static final String GBIF_SERVICE = "http://api.gbif.org";

   private CurationStatus curationStatus;
   private String validatedScientificName = null;
   private String validatedAuthor = null;
   private String GBIF_name_GUID = null;
   private String comment = "";

   private String foundKingdom = null;
   private String foundPhylum = null;
   private String foundClass = null;
   private String foundOrder = null;
   private String foundFamily = null;



   // private String IPNISourceId = null;

   /**
    * Checklist bank name resolver url, currently does not appear to be working.
    */
   private final static String GBIF_CB_RESOLVE_URL = "http://ecat-dev.gbif.org/ws/resolve/";
   // Above ecat-dev uri is experimental and targeted for removal in Dec 2013.
   // Use instead: http://api.gbif.org/dev
   // Documented at http://dev.gbif.org/wiki/display/POR/Webservice+API

   /**
    * Checklist bank usage search URL.  See documentation at: http://ecat-dev.gbif.org/api/clb
    */
   private final static String GBIF_CB_USAGE_URL = "http://ecat-dev.gbif.org/ws/usage/";
   // Above ecat-dev uri is experimental and targeted for removal in Dec 2013.
   // Use instead: http://api.gbif.org/dev
   // Documented at http://dev.gbif.org/wiki/display/POR/Webservice+API

   /**
    * Retrieve usages from Checklist Bank by name ID.  See documentation at http://ecat-dev.gbif.org/api/clb
    */
   private final static String GBIF_GUID_Prefix = "http://ecat-dev.gbif.org/ws/usage/?nid=";
   // Above ecat-dev uri is experimental and targeted for removal in Dec 2013.
   // Use instead: http://api.gbif.org/dev
   // Documented at http://dev.gbif.org/wiki/display/POR/Webservice+API

   private  String serviceName = "GBIF";

   private JSONParser parser;

   /**
    * Default no-argument constructor.
    */
   public AdvancedSciNameService(){
	   parser = new JSONParser();
   }

   public void validateScientificName(String scientificName, String author){
	   validateScientificName(scientificName, author, "", "", "", "", "", "");
   }

   //public void validateScientificName(String scientificNameToValidate, String authorToValidate, String rank, String kingdom, String phylum, String tclass, String genus, String subgenus, String verbatimTaxonRank, String infraspecificEpithe){
   public void validateScientificName(String scientificNameToValidate, String authorToValidate,String genus, String subgenus, String specificEpithet, String verbatimTaxonRank, String infraspecificEpithet, String taxonRank){
       GBIF_name_GUID = null;
       comment = "";
       curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;

       //try to find information from the cached file
       //if failed, then access GBIF service, or if that fails, GNI service

       String key = constructKey(scientificNameToValidate, authorToValidate);
       if(useCache && cachedScientificName.containsKey(key)){
           // check for name in cache
           HashMap<String,String> cachedScientificNameInfo = cachedScientificName.get(key);

           String expAuthor = cachedScientificNameInfo.get("author");
           if(expAuthor.equals("")){
               //can't be found in either GBIF or GNI
               comment = "Failed to find scientific name in either GBIF and GNI.";
               // Assumes that the cache is recent.
               // Alternately, could retry the services here.
               // validateScientificNameAgainstServices(scientificName, author);
           }else if(expAuthor.equalsIgnoreCase(authorToValidate)){
               validatedScientificName = scientificNameToValidate;
               validatedAuthor = authorToValidate;
               GBIF_name_GUID = constructGBIFGUID(cachedScientificNameInfo.get("id"));
               comment = "The scientific name and authorship are correct.";
               curationStatus = CurationComment.CORRECT;
           }else{
               validatedScientificName = scientificNameToValidate;
               validatedAuthor = expAuthor;
               GBIF_name_GUID = constructGBIFGUID(cachedScientificNameInfo.get("id"));
               comment = "Updated the scientific name (including authorship) with term found in GNI which is from GBIF and in the same lexical group as the original term.";
               curationStatus = CurationComment.CURATED;
           }
       }else{
           //try to find it in GBIF service failing over to check against GNI
           //validateScientificNameAgainstServices(scientificNameToValidate, authorToValidate, key, rank, kingdom, phylum, tclass);

           validatedAuthor = authorToValidate;
           // start with consistency check
           String consistentName = checkConsistencyToAtomicField(scientificNameToValidate, genus, subgenus, specificEpithet, verbatimTaxonRank, taxonRank, infraspecificEpithet);
           // second check misspelling
           if (consistentName != null){
               checkMisspelling(consistentName);
           }
           // third, go to GBIF checklist bank
       }
   }

    private String checkConsistencyToAtomicField(String scientificName, String genus, String subgenus, String specificEpithet, String verbatimTaxonRank, String taxonRank, String infraspecificEpithet){

        if (genus != null && specificEpithet != null && infraspecificEpithet != null){
            String constructName= genus + " ";

            if (subgenus != null) constructName = constructName + subgenus + " ";
            constructName = constructName + specificEpithet + " ";

            if (verbatimTaxonRank != null) constructName = constructName + verbatimTaxonRank + " ";
            else if (taxonRank != null) constructName = constructName + taxonRank + " ";

            constructName = constructName + infraspecificEpithet;

            NameParser parser = new NameParser();
            ParsedName pn = null;
            ParsedName cn = null;
            //System.out.println("scientificName = " + scientificName);
            //System.out.println("constructName = " + constructName);
            try {
                pn = parser.parse(scientificName);
                cn = parser.parse(constructName);
            } catch (UnparsableException e) {
                System.out.println("Parsing error: " + e);
            }

            if(pn.equals(cn)){
                return pn.canonicalName();
                //validatedAuthor = pn.getAuthorship();
            } else{
                //validatedAuthor = null;
                curationStatus = CurationComment.UNABLE_CURATED;
                comment = comment + "scientificName is inconsistent with atomic fields";
                serviceName = null;
                return null;
            }

        }else{
            comment = comment + "can't construct sciName from atomic fields";
            return scientificName;
        }
    }

        /*
        //now start checking
        if (pn.getAuthorship() != null){
            String validatedauthorship =  consensusField(pn.getAuthorship(), authorship);
        }
        String validatedGenus  =  consensusField(pn.getGenusOrAbove(), genus);
        String validatedVerbatimTaxonRank  =  consensusField(pn., verbatimTaxonRank);
        String validatedSubgenus  =  consensusField(pn.get, subgenus);
        String validatedInfraspecificEpithet  =  consensusField(pn.getGenusOrAbove(), infraspecificEpithet);

        return consistentName;
    }

    private String consensusField(String nameFromAtomic, String nameFromComp) {
        String returnedField = null;
        if (nameFromAtomic == null) returnedField = nameFromComp;
        if (nameFromComp == null) returnedField = nameFromAtomic;
        if (nameFromComp != null && nameFromAtomic != null) {
            if (nameFromComp == nameFromAtomic) returnedField = nameFromComp;
            else returnedField = "INCONSISTENT";
        }
        return returnedField;
    }
      */


    private String checkMisspelling (String name){
        StringBuilder result = new StringBuilder();
        URL url;
        try {
            name = name.replace(" ", "+");
            url = new URL("http://resolver.globalnames.org/name_resolvers.json?names=" + name +"&resolve_once=true");
            //System.out.println(url);
            URLConnection connection = url.openConnection();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                //System.out.println("line = " + line);
                result.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        JSONParser parser = new JSONParser();
        JSONObject last = new JSONObject();
        try {
            //System.out.println("result = " + result.toString());
            JSONObject object = (JSONObject)parser.parse(result.toString());
            //System.out.println("object = " + object.toString());

            JSONArray jdata = (JSONArray)object.get("data");
            JSONObject jone = (JSONObject)jdata.get(0);
            JSONArray jresults = (JSONArray)jone.get("results");
            //if there is no possible correction, return now
            if (jresults == null){
                return null;
            }
            last = (JSONObject)jresults.get(0);
            //System.out.println("last = " + last.toString());

        } catch (ParseException e) {
            System.out.println("Provided name: " + name + " is not solvable");
            return null;
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
                System.out.println("The provided name: \"" + name + "\" is misspelled, changed to \"" + resolvedName + "\".");
            }
            else {
                System.out.println("The provided name: \"" + name + "\" has spelling issue, changed to \"" + resolvedName + "\" for now.");
            }
        }

        return resolvedName;
    }

    public static String getValFromKey(JSONObject json, String key) {
        if (json==null || json.get(key)==null) {
            return "";
        } else {
            return json.get(key).toString();
        }
    }


    private void validateScientificNameAgainstServices(String scientificNameToValidate, String authorToValidate, String key, String rank, String kingdom, String phylum, String tclass) {
	   System.out.println("Validate:" + scientificNameToValidate);
	   // Tests follow on authorship String object methods, so make sure it isn't null
	   if (authorToValidate==null)  { authorToValidate = ""; }
       try{
           String source = "";
           // Check name against GBIF checklist bank
           String id = checklistBankNameSearch(scientificNameToValidate, authorToValidate, rank, kingdom, phylum, tclass, false);
           // Was there a result?
           if(id == null){
               // no match was found
        	   // Perhaps the authorship isn't matching, try a match on canonnical name alone.
               id = checklistBankNameSearch(scientificNameToValidate, authorToValidate, rank, kingdom, phylum, tclass, true);
           }
           if(id == null){
               // no match was found
        	   // Perhaps the authorship is in the name and isn't matching on the service, try parsing authorship from scientificName
        	   Vector<String> nameBits = GNISupportingService.parseName(scientificNameToValidate);
        	   if (nameBits.size()==2) {
        		   id = checklistBankNameSearch(nameBits.get(0), nameBits.get(1), rank, kingdom, phylum, tclass, false);
        	   }
           }
           // Was there a result?
           if(id == null){
               // no match was found


               // access the GNI and try to get the name that is in the lexical group and from IPNI
               Vector<String> resolvedNameInfo = GNISupportingService.resolveDataSourcesNameInLexicalGroupFromGNI(scientificNameToValidate);

               // TODO: Fix double pass stuff copied from IPNI/GNI service check

               if(resolvedNameInfo == null || resolvedNameInfo.size()==0){
                   //failed to find it in GNI
                   comment = "Can't find the scientific name and authorship by searching in IPNI and the lexical group from IPNI in GNI.";
               }else{
                   //find it in GNI
                   String resolvedScientificName = resolvedNameInfo.get(0);
                   String resolvedScientificNameAuthorship = resolvedNameInfo.get(1);

                   //searching for this name in GNI again.
                   id = checklistBankNameSearch(resolvedScientificName, resolvedScientificNameAuthorship, rank, kingdom, phylum, tclass, false);
                   if(id == null){
                       //failed to find the name got from GNI in the IPNI
                       comment = "Found name which is in the same lexical group as the searched scientific name and from IPNI but failed to find this name really in IPNI.";
                   }else{
                       //correct the wrong scientific name or author by searching in both IPNI and GNI
                       validatedScientificName = resolvedScientificName;
                       validatedAuthor = resolvedScientificNameAuthorship;
                       GBIF_name_GUID = constructGBIFGUID(id);
                       comment = "Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.";
                       curationStatus = CurationComment.CURATED;
                       source = "IPNI/GNI";
                   }
               }
           }else{
        	   Soundex soundex = new Soundex();
               // Yes, got a match by searching in GBIF Checklist bank
               GBIF_name_GUID = id;   // which is the GBIF name ID, not an IPNI LSID in this case.
               if (validatedAuthor.trim().toLowerCase().equals(authorToValidate.trim().toLowerCase())) {
            	   comment = "The scientific name and authorship are correct.";
            	   curationStatus = CurationComment.CORRECT;
               } else {
				   comment = "A correction to the authorship has been proposed.";
            	   try {
					   if (soundex.difference(validatedAuthor,authorToValidate)==4) {
					       comment = "A similar sounding correction to the authorship has been proposed.";
					   }
				   } catch (EncoderException e) {
				   }
            	   curationStatus = CurationComment.CURATED;
               }
               source = "GBIFChecklistBank";
               System.out.println("Matched in advanced sciNameValidator");
               System.out.println("id:" + id);
               System.out.println("scientificName:" + scientificNameToValidate);
               System.out.println("correctedScientificName:" + getCorrectedScientificName());
               System.out.println("authorship:" + authorToValidate);
               System.out.println("correctedAuthorship:" + getCorrectedAuthor());
               System.out.println("comment:" + comment);
           }

           //write newly found information into hashmap and later write into the cached file if it exists
           if(useCache){
               HashMap<String,String> cachedScientificNameInfo = new HashMap<String,String>();

               if(validatedAuthor == null){
                   cachedScientificNameInfo.put("author", "");
               }else{
                   cachedScientificNameInfo.put("author", validatedAuthor);
               }

               if(id == null){
                   cachedScientificNameInfo.put("id", "");
               }else{
                   cachedScientificNameInfo.put("id", id);
               }

               cachedScientificNameInfo.put("source", source);

               cachedScientificName.put(key,cachedScientificNameInfo);

               newFoundScientificName.add(scientificNameToValidate);
               newFoundScientificName.add(authorToValidate);
               newFoundScientificName.add(validatedAuthor);
               newFoundScientificName.add(id);
               newFoundScientificName.add(source);
           }
       }catch(CurrationException ex){
           comment = ex.getMessage();
           curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
           return;
       }
   }

   public CurationStatus getCurationStatus(){
       return curationStatus;
   }

   public String getCorrectedScientificName(){
       return validatedScientificName;
   }

   public String getCorrectedAuthor(){
       return validatedAuthor;
   }

   public String getLSID(){
       return GBIF_name_GUID;
   }

   public String getComment() {
       return comment;
   }

   public void setCacheFile(String file) throws CurrationException{
	   initializeCacheFile(file);
	   importFromCache();
       this.useCache = true;
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
                       strLine = strLine + "\t" + newFoundScientificName.get(j);
                   }
                   strLine = strLine.trim();
                   writer.write(strLine+"\n");
               }
               writer.close();
           }
       } catch (IOException e) {
           throw new CurrationException(getClass().getName()+" failed to write newly found scientific name information into cached file "+cacheFile.toString()+" since "+e.getMessage());
       }
   }

    @Override
    public List<List> getLog() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setUseCache(boolean use) {
        this.useCache = use;
        cachedScientificName = new HashMap<String,HashMap<String,String>>();
       newFoundScientificName = new Vector<String>();
    }

    public String getServiceName() {
       return serviceName;
   }

   private void initializeCacheFile(String fileStr) throws CurrationException {
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
               String[] info = strLine.split(ColumnDelimiterInCacheFile);
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

   private String constructGBIFGUID(String id){
       return GBIF_GUID_Prefix+id;
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
   private String checklistBankNameSearch(String taxon, String author, String rank, String kingdom, String phylum, String tclass, boolean useCanonical) throws CurrationException{

       //start of insertion
       //todo: add dataset selection code
       //taxon = "Amaranthus retroflexus";



       //String key = searchBackbone(taxon);
       String json = fetchTaxon(taxon, null);
       NameUsage match = parseNameUsageFromJSON(taxon, json);
        if (json == null){
         //System.out.println("jason null");
       }
       else {
             //System.out.println("json = " + json.toString());
         }
       if (match == null){
           //System.out.println("No match");
           curationStatus=CurationComment.UNABLE_DETERMINE_VALIDITY;
           comment = "Can't determine validity due to homonyms or empty results";
       }
       else{
            //System.out.println("match found");
           curationStatus = CurationComment.CURATED;
           validatedScientificName = match.getCanonicalName();
           comment = "curated by search GBIF checklist bank API";

       }

       /*
       String foundId = match.getSourceID();
       String foundTaxon = match.getAcceptedName();
       System.out.println("Found on Service: " + foundTaxon);

       String id = constructGBIFGUID(foundId);
       System.out.println("Match ID: " + id);
       // Set class properties
       GBIF_name_GUID = id;

       validatedScientificName = match.getAcceptedName();
       validatedAuthor = match.getAuthorship();
       */

       return null;
   }

    private String fetchTaxon(String taxon, String targetChecklist) {
        StringBuilder result = new StringBuilder();
        String datasetKey = "";
        if (targetChecklist!=null) {
            datasetKey = "datasetKey=" + targetChecklist;
        }
        URL url;
        try {
            url = new URL(GBIF_SERVICE + "/name_usage/" + taxon + "?limit=100&" + datasetKey);
            URLConnection connection = url.openConnection();
            System.out.println(connection.getContent().getClass().getName());
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return result.toString();
    }


    private NameUsage parseNameUsageFromJSON(String targetName, String json) {
        JSONParser parser=new JSONParser();

        try {
            JSONArray array = new JSONArray();
            try {
                JSONObject o = (JSONObject)parser.parse(json);
                if (o.get("results")!=null) {
                    array = (JSONArray)o.get("results");
                } else {
                    // array = (JSONArray)parser.parse(json);
                }
            } catch (ClassCastException e) {
                array = (JSONArray)parser.parse(json);
            }

            Iterator i = array.iterator();
            while (i.hasNext()) {
                JSONObject obj = (JSONObject)i.next();
                // System.out.println(obj.toJSONString());
                //NameUsage name = new NameUsage(obj);
                NameUsage name = new NameUsage();
                if (name.getCanonicalName().equals(targetName)) {
                    return name;
                }
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

       //end of insertion


      /*
       String outputFormat = "delimited-minimal";
       
       org.apache.http.client.HttpClient httpclient = new DefaultHttpClient();
       httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
       httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);

       List<org.apache.http.NameValuePair> parameters = new ArrayList<org.apache.http.NameValuePair>();
       // See documentation of Name Usage service at:
       //  http://ecat-dev.gbif.org/api/clb

       // Note parameterCount array size declaration above.
       if (useCanonical) { 
           parameters.add(new BasicNameValuePair("q", taxon));
       } else { 
           parameters.add(new BasicNameValuePair("q", taxon + " " + author));
       }
       
       // "Restricts a search to usages with a taxonomic status of either accepted synonym. "
       parameters.add(new BasicNameValuePair("status", "accepted"));
       
       // "The list of higher darwin core ranks to show for each result" 
       parameters.add(new BasicNameValuePair("showRanks", "kpcof"));
       
       // "optional rank filter to search usages of the given rank only.
       // "Please use the first letter of any one of the following list " 
       // "of rank abbreviations k p c o f sf g sg s is for kingdom, phylum, "
       // "class, order, family, subfamily, genus, subgenus, species or infraspecific respectively. "
       if (rank==null || rank.equals("")) { 
           parameters.add(new BasicNameValuePair("rank", ""));
       } else { 
           parameters.add(new BasicNameValuePair("rank", rank));
       }
       // "The checklist type(s) to limit the request to."
       // "Defaults to "unti", i.e. all checklists but occurrence derived ones."
       // "Please use the first letters for unknown any arbitrary list of species, nomenclator name authority, taxonomic taxonomic authority, inventory not authoritative for names or taxonomy, but complete regarding metadata scope, occurrences derived from occurrence records: u n t i o"
       parameters.add(new BasicNameValuePair("type", "nt"));
       
       // " The scientific name search type. One of fullname canonical defaults to fullname which does a prefix seach on the full scientific name including authorship and rank marker. The canonical search does an exact, case sensitive match on the canonical name excluding authorshi or a rank marker like var."
       if (useCanonical) { 
           parameters.add(new BasicNameValuePair("searchType", "canonical"));
       } else { 
           parameters.add(new BasicNameValuePair("searchType", "fullname"));
       }
       
       
       // output format specification not available for GBIF CB API.
       // parameters[1] = new NameValuePair("output_format", outputFormat);
       
       String id  = null;


       
       return id;
*/



   
   private String firstWord(String words) { 
	   String result = words;
	   if (words!=null) { 
	      if (words.trim().contains(" ")) { 
		     result = words.trim().substring(0, words.trim().indexOf(' '));
	      }
	   } 
	   return result;
   }


}

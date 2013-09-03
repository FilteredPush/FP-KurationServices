package fp.services;

import fp.util.CurationComment;
import fp.util.CurationStatus;
import fp.util.CurrationException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.language.Soundex;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.json.simple.JSONArray;   // code.google.com/p/json-simple
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

/**
 * Validate Scientific Names against GBIF's Checklist Bank web services, with 
 * a failover to validation against the GNI.
 * 
 * @author Paul J. Morris
 *
 */
public class GBIFService implements IScientificNameValidationService{ 

    private boolean useCache = false;
    private File cacheFile = null;
    private HashMap<String, HashMap<String,String>> cachedScientificName;
    private Vector<String> newFoundScientificName;
    private static final String ColumnDelimiterInCacheFile = "\t";
   
    private CurationStatus curationStatus;
    private String correctedScientificName = null;
    private String correctedAuthor = null;
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
   
   /**
    * Checklist bank usage search URL.  See documentation at: http://ecat-dev.gbif.org/api/clb
    */
   private final static String GBIF_CB_USAGE_URL = "http://ecat-dev.gbif.org/ws/usage/";
   
   /**
    * Retrieve usages from Checklist Bank by name ID.  See documentation at http://ecat-dev.gbif.org/api/clb
    */
   private final static String GBIF_GUID_Prefix = "http://ecat-dev.gbif.org/ws/usage/?nid=";
   
   private final String serviceName = "GBIF";  
   
   private JSONParser parser;
   
   /**
    * Default no-argument constructor.
    */
   public GBIFService(){ 
	   parser = new JSONParser();	   
   }
   
   public void validateScientificName(String scientificName, String author){
	   validateScientificName(scientificName, author, "", "", "", "");
   }
   
   public void validateScientificName(String scientificNameToValidate, String authorToValidate, String rank, String kingdom, String phylum, String tclass){
       correctedScientificName = null;
       correctedAuthor = null;
       GBIF_name_GUID = null;
       comment = "";
       curationStatus = CurationComment.UNABLE_CURATED;

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
               correctedScientificName = scientificNameToValidate;
               correctedAuthor = authorToValidate;
               GBIF_name_GUID = constructGBIFGUID(cachedScientificNameInfo.get("id")); 
               comment = "The scientific name and authorship are correct.";
               curationStatus = CurationComment.CORRECT;
           }else{
               correctedScientificName = scientificNameToValidate;
               correctedAuthor = expAuthor;
               GBIF_name_GUID = constructGBIFGUID(cachedScientificNameInfo.get("id")); 
               comment = "Updated the scientific name (including authorship) with term found in GNI which is from GBIF and in the same lexical group as the original term.";
               curationStatus = CurationComment.CURATED;
           }
       }else{
           //try to find it in GBIF service failing over to check against GNI          
           validateScientificNameAgainstServices(scientificNameToValidate, authorToValidate, key, rank, kingdom, phylum, tclass);
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
                       correctedScientificName = resolvedScientificName;
                       correctedAuthor = resolvedScientificNameAuthorship;
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
               if (correctedAuthor.trim().toLowerCase().equals(authorToValidate.trim().toLowerCase())) { 
            	   comment = "The scientific name and authorship are correct.";
            	   curationStatus = CurationComment.CORRECT;
               } else { 
				   comment = "A correction to the authorship has been proposed.";
            	   try {
					   if (soundex.difference(correctedAuthor,authorToValidate)==4) { 
					       comment = "A similar sounding correction to the authorship has been proposed.";
					   } 
				   } catch (EncoderException e) {
				   }
            	   curationStatus = CurationComment.CURATED;
               }	   
               source = "GBIFChecklistBank";               
               System.out.println("Matched.");
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
               
               if(correctedAuthor == null){
                   cachedScientificNameInfo.put("author", "");
               }else{
                   cachedScientificNameInfo.put("author", correctedAuthor);
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
               newFoundScientificName.add(correctedAuthor);
               newFoundScientificName.add(id);
               newFoundScientificName.add(source);                 
           }
       } catch (CurrationException ex) {
           comment = ex.getMessage();
           curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
           return;
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
       return GBIF_name_GUID;
   }
   
   public String getComment() {
       return comment;
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

   public void setCacheFile(String file) throws CurrationException {
	   initializeCacheFile(file);
	   importFromCache();
       this.useCache = true;
   }

   public void flushCacheFile() throws CurrationException {
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

   private void importFromCache() throws CurrationException {
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
    * @throws CurrationException
    */
   private String checklistBankNameSearch(String taxon, String author, String rank, String kingdom, String phylum, String tclass, boolean useCanonical) throws CurrationException {
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
       try {
           HttpResponse resp;
           HttpPost httpPost = new HttpPost(GBIF_CB_USAGE_URL);
           httpPost.setEntity(new UrlEncodedFormEntity(parameters));

           resp = httpclient.execute(httpPost);
           if (resp.getStatusLine().getStatusCode() != 200) {
                   throw new CurrationException("GBIFService failed to connect to GBIF with status: "+resp.getStatusLine().getStatusCode());
           }

           InputStream response = resp.getEntity().getContent();

           // parse the response
           String version = "";
           BufferedReader responseReader = new BufferedReader(new InputStreamReader(response));
           String strLine = responseReader.readLine();
           
           // GBIF CB API returns JSON in a single line
           System.out.println("Response: " + strLine);
           
           // TODO: Fix encoding of characters in author names.
           if (parser==null) { 
        	   parser = new JSONParser();	
           }
           while(strLine != null){
               
               // TODO: replace with a JSON parser
               
/*             
                 [run] /ws/usage/
                 [run] Host: ecat-dev.gbif.org
                 [run] 
                 [run] q=Kalydon+&status=accepted&rank=g&showRanks=kpcof
                 [run] 200
                 [run] {"success":true,"data":[{"taxonID":102734186,"datasetID":1037,"higherTaxonID":null,"nameID":3772063,"scientificName":"Kalydon","rank":"genus","isSynonym":false,"taxonomicStatus":null,"numChildren":0,"numDescendants":0,"class":null,"classID":null,"family":null,"familyID":null,"kingdom":null,"kingdomID":null,"order":null,"orderID":null,"phylum":null,"phylumID":null,"higherClassification":"","datasetName":"Interim Register of Marine and Nonmarine Genera","datasetType":"unknown"},{"taxonID":102317497,"datasetID":1037,"higherTaxonID":101333162,"nameID":3772064,"scientificName":"Kalydon Hutton, 1884","rank":"genus","isSynonym":false,"taxonomicStatus":null,"numChildren":0,"numDescendants":0,"class":"Gastropoda","classID":102390277,"family":"Muricidae de Costa, 1776","familyID":101333162,"kingdom":"Animalia","kingdomID":101719444,"order":"Neogastropoda","orderID":101856868,"phylum":"Mollusca","phylumID":102545077,"higherClassification":"Animalia - Mollusca - G astropoda - Neogastropoda - Muricidae de Costa, 1776","datasetName":"Interim Register of Marine and Nonmarine Genera","datasetType":"unknown"}]}
*/             
    
        	   try { 
        		   Object object = parser.parse(strLine);
        		   JSONObject jsonObject = (JSONObject)object;
        		   Boolean success = (Boolean)jsonObject.get("success");
        		   if (success==true) { 
        			   JSONArray data = (JSONArray)jsonObject.get("data");
        			   if (data.size()>0) { 
        				   Iterator<JSONObject> i = data.iterator();
        				   // If using searchType=fullname (default) then the
        				   // GBIF service does wildcard q=term% rather than exact q=term search, 
        				   // thus have to rule out matches with start of string.
        				   while (i.hasNext()) { 
        					   boolean removed = false;
        					   JSONObject checkRow = i.next();
        					   String checkTaxon = checkRow.get("scientificName").toString().trim();
        					   System.out.println("Comparing [" +checkTaxon.toLowerCase()+ "] to ["+taxon.toLowerCase()+" "+"]");
        					   if(!checkTaxon.toLowerCase().startsWith(taxon.toLowerCase()+" ")) { 
        					      i.remove();
        					      removed = true;
        					   }
        					   // Exclude likely homonyms by checking for missmatches in higher taxa if available
        					   String checkKingdom = ""; 
        					   if (checkRow.get("kingdom")!=null) { 
        					    	checkKingdom = checkRow.get("kingdom").toString().trim().toLowerCase();
        					   } 	
        					   String checkPhylum = ""; 
        					   if (checkRow.get("phylum")!=null) { 
        					      checkPhylum = checkRow.get("phylum").toString().trim().toLowerCase();
        					   }
        					   String checkClass = ""; 
        					   if (checkRow.get("class")!=null) { 
        					       checkClass = checkRow.get("class").toString().trim().toLowerCase();
        					   } 
        					   if (kingdom.length()>0 && !removed) { 
        						   if (!kingdom.toLowerCase().trim().equals(checkKingdom)) { 
        							   i.remove();
        							   removed = true;
        						   }
        					   }
        					   if (phylum.length()>0 && !removed) { 
        						   if (!phylum.toLowerCase().trim().equals(checkPhylum)) { 
        							   i.remove();
        							   removed = true;
        						   }
        					   }
        					   if (tclass.length()>0 && !removed) { 
        						   if (!tclass.toLowerCase().trim().equals(checkClass)) { 
        							   i.remove();
        							   removed = true;
        						   }
        					   }
        				   }
        				   // Reload iterator with narrowed scope of results.
        				   i = data.iterator();
        				   boolean matched = false;
        				   while (i.hasNext() && !matched) { 
        					   JSONObject dataRow = i.next();
        					   System.out.println(dataRow.get("nameID"));
        					   Object statusObject = dataRow.get("taxonomicStatus");
        					   if (statusObject !=null) { 
        						   String status = statusObject.toString().trim();
        						   System.out.println(status);
        						   if (status.equals("Accepted")) { 

        							   String foundId = dataRow.get("nameID").toString().trim();
        							   String foundTaxon = dataRow.get("scientificName").toString().trim();
        							   System.out.println("Found on Service: " + foundTaxon);

        								   id = constructGBIFGUID(foundId);
        							       System.out.println("Match ID: " + id);
        								   // Set class properties
        								   GBIF_name_GUID = id; 
        							       
        							       // GBIF Service returns taxon name and authorship concatenated.
        							       Vector<String> nameBits = GNISupportingService.parseName(foundTaxon);
        							       if (nameBits.size()==2) { 
        								       correctedScientificName = nameBits.get(0);
        								       correctedAuthor = nameBits.get(1);
        							       } else { 
        							    	   // guess that authorship is whatever follows taxon name proviede
        								       correctedScientificName = foundTaxon;
        								       correctedAuthor = foundTaxon.substring(taxon.length()+1);
        							       } 
        								   comment = "Updated the scientific name (including authorship) and blank higher taxa with values found in GBIF Checklist Bank .";
        								   curationStatus = CurationComment.CURATED;
 
        								   // some data sources in GBIF can contain authorities for higher taxa, thus limit to first word.
        								   if (dataRow.get("kingdom")!=null) {  
        								      foundKingdom = firstWord(dataRow.get("kingdom").toString().trim());
        								   } else { 
        									   foundKingdom = "";
        								   }
        								   if (dataRow.get("phylum")!=null) { 
        								       foundPhylum = firstWord(dataRow.get("phylum").toString().trim());
        								   } else { 
        									   foundPhylum = "";
        								   }
        								   if (dataRow.get("class")!=null) {
        								       foundClass = firstWord(dataRow.get("class").toString().trim());
        								   } else { 
        									   foundClass = "";
        								   }
        								   if (dataRow.get("order")!=null) {
        								       foundOrder = firstWord(dataRow.get("order").toString().trim());
        								   } else { 
        									   foundOrder = "";
        								   }
        								   if (dataRow.get("family")!=null) {
        								       foundFamily = firstWord(dataRow.get("family").toString().trim());
        								   } else { 
        									   foundFamily = "";
        								   } 
        								   
        								   System.out.println(foundKingdom + " " + foundPhylum + " "  + foundClass + " " + foundOrder + " " + foundFamily );
        								   
        								   // TODO: Handle dataset name containing match
        								   System.out.println(dataRow.get("datasetName").toString());
        								   
        								   // TODO: Evaluate all matches, check for homonyms, check for ambiregnal homonyms. 
        								   matched = true;
        						   }
        					   } 
        				   } 
        			   } else { 
        				   // no match found.
        				   id = null;
        				   curationStatus = CurationComment.UNABLE_CURATED;
        			   }
        		   } 
        	   } catch (ClassCastException e) { 
        		   throw new CurrationException("GBIFService had unexpected return type. "+ e.getMessage());
        	   } catch (ParseException e) {
        		   throw new CurrationException("GBIFService failed to parse result from GBIF ChecklistBank service "+e.getMessage());
        	   }
               strLine = responseReader.readLine();
           }
           responseReader.close();
       } catch (IOException e) {
           throw new CurrationException("GBIFService failed to access GBIF ChecklistBank service with "+e.getMessage());
	   } 
       
       return id;
   }
   
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

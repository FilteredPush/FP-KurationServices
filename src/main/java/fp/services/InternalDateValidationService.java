package fp.services;

import com.hp.hpl.jena.rdf.model.*;
import fp.util.CurationComment;
import fp.util.CurationStatus;
import fp.util.CurrationException;
import org.joda.time.DateMidnight;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

//todo: cache machanism is not finished
public class InternalDateValidationService implements IInternalDateValidationService {

    private boolean useCache;

	public void validateDate(String eventDate, String verbatimEventDate, String startDayOfYear, String year, String month, String day, String modified, String collector) {
        comment = "";
        DateMidnight consesEventDate = parseDate(eventDate, verbatimEventDate, startDayOfYear, year, month, day, modified);
        if(consesEventDate != null){
            if (collector == null || collector == ""){
                comment = comment + " | collector name is not available";
            }else{
                // can switch between using old Harvard list or new Solr dataset
                boolean inAuthorLife = false;
                if (UsingSolr){
                    inAuthorLife = checkWithAuthorSolr(consesEventDate, collector);
                }
               else{
                    inAuthorLife = checkWithAuthorHarvard(consesEventDate, collector);
                }
            }
        }
    }

    public void validateDate(String eventDate, String collector, String latitude, String longitude) {
        //todo: in order to avoid another level of interface
    }

    public void setCacheFile(String file) throws CurrationException {
        useCache = true;
        initializeCacheFile(file);
        importFromCache();
    }

	public String getCorrectedDate() {
		return correctEventDate;
	}
	
	public String getComment(){
		return comment;
	}
		
	public CurationStatus getCurationStatus() {
		return curationStatus;
	}

	public void flushCacheFile() throws CurrationException {
	}

    @Override
    public List<List> getLog() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setUseCache(boolean use) {
        this.useCache = use;
        authoritativeFloweringTimeMap = new HashMap<String,Vector<String>>();
    }

    public String getServiceName(){
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
		authoritativeFloweringTimeMap = new HashMap<String,Vector<String>>();

		try {
			BufferedReader phenologyFileReader = new BufferedReader(new FileReader(cacheFile));
			String strLine = phenologyFileReader.readLine();
			while(strLine!=null){
				String[] info = strLine.split(ColumnDelimiterInCacheFile,-1);
				if(info.length!=3){
					throw new CurrationException(getClass().getName()+" failed since the authoritative file "+cacheFile.toString()+" is invalid at "+strLine);
				}
				String taxon = info[0].trim().toLowerCase();
				String floweringTime = info[1].trim();
				authoritativeFloweringTimeMap.put(taxon, getMonthVector(floweringTime));

				strLine = phenologyFileReader.readLine();
			}
		} catch (FileNotFoundException e) {
			throw new CurrationException(getClass().getName()+" failed to find the phenology authoritative file "+cacheFile.toString()+" for "+e.getMessage());
		} catch (IOException e) {
			throw new CurrationException(getClass().getName()+" failed to read the phenology authoritative file "+cacheFile.toString()+" for "+e.getMessage());
		}
	}

	private Vector<String> getMonthVector(String flowerTimeStr){
		Vector<String> monthVector = new Vector<String>();
		String [] monthArray = flowerTimeStr.split(";");		
		for(int i=0;i<monthArray.length;i++){
			monthVector.add(monthArray[i].trim());
		}
		return monthVector;
	}

    //////////////////////////////////
    private DateMidnight parseDate (String eventDate, String verbatimEventDate, String startDayOfYear, String year, String month, String day, String modified) {
        DateMidnight parsedEventDate = null;      //also for consensus date
        DateMidnight constructedDate = null;
        DateTimeFormatter format = ISODateTimeFormat.date();

        //get two eventDate first

        try{
            parsedEventDate = DateMidnight.parse(eventDate, format);
        } catch(IllegalFieldValueException e){
            //can't parse eventDate
            //System.out.println("can't parse eventDate");
            comment = comment + " | Can't parse eventDate";
            parsedEventDate=null;
        } catch(NullPointerException e){
            System.out.println("eventDate = " + eventDate);
            comment = comment + " | eventDate is null?";
        }

        try{
            int yearInt = Integer.parseInt(year);
            int monthInt = Integer.parseInt(month);
            int dayInt = Integer.parseInt(day);
            constructedDate = new DateMidnight(yearInt, monthInt, dayInt);
        }catch (NumberFormatException e) {
           // System.out.println("unable to cast date string to int = " + e);
            //System.out.println("can't construct eventDate from atomic fields: string casting error");
            comment = comment +" | can't construct eventDate from atomic fields: string casting error";
            constructedDate = null;
        }catch (IllegalFieldValueException e) {
            //can't construct eventDate
            //System.out.println("can't construct eventDate from atomic fields: parsing error");
            comment = comment +" | can't construct eventDate from atomic fields: parsing error";
            constructedDate = null;
        }

        //second, compare in different cases
        if (parsedEventDate == null && constructedDate == null){
            curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
            comment = " | Can't get a valid eventDate from the record";
            return null;
        }else if (parsedEventDate != null && constructedDate == null){
            if (!parsedEventDate.toString(format).equals(eventDate)) {
                curationStatus = CurationComment.CURATED;
                comment = comment + " | eventDate:" + eventDate + " has been formatted to ISO format: " + parsedEventDate.toString(format) +".";
                correctEventDate=parsedEventDate.toString(format);
            }else{
                comment = comment + " | eventDate is in ISO format";
            }
            //todo: status will be overwritten if inconsistent, needs to be preserved?
        }else if (parsedEventDate == null && constructedDate != null){
            curationStatus = CurationComment.Filled_in;
            comment = comment + " | EventDate is constructed from atomic fields";
            parsedEventDate = constructedDate;
            correctEventDate=parsedEventDate.toString(format);
        }else{
            //if two dates don't conform, use startDayOfYear to distinguish
            if(!parsedEventDate.isEqual(constructedDate)){
                if(startDayOfYear != null){
                    int startDayInt = Integer.parseInt(startDayOfYear);
                    if (constructedDate.dayOfYear().get() == startDayInt) {
                        parsedEventDate = constructedDate;
                    }else if (parsedEventDate.dayOfYear().get() != startDayInt){
                        curationStatus = CurationComment.UNABLE_CURATED;
                        comment = comment + " | Internal inconsistent: startDayOfYear:" + startDayInt + " and eventDate:" + parsedEventDate.toString(format) + " don't conform.";
                        return null;
                    }
                }else{
                    curationStatus = CurationComment.UNABLE_CURATED;
                    comment = comment + " | Internal inconsistent: EventDate:" + parsedEventDate.toString(format) + " doesn't conform to constructed date:" + constructedDate;
                    return null;
                }
            }else{
                curationStatus = CurationComment.CORRECT;
                comment = comment + " | eventDate is consistent with atomic fields";
            }
        }

        //compare with other dates
        try{
            if (parsedEventDate.isAfter(DateMidnight.parse(modified.split(" ")[0], format)) ) {
                curationStatus = CurationComment.UNABLE_CURATED;
                comment = comment + " | Internal inconsistent: EventDate:" + parsedEventDate.toString(format) + " occurs after modified date:" + DateMidnight.parse(modified.split(" ")[0], format) + ".";
                return null;
            } else{
                comment = comment + " | eventDate is consistent with modified date";
            }

        }catch(IllegalFieldValueException e) {
            //can't format modified date
            comment = comment + " | cannot parse modified date";
        }

        return parsedEventDate;
    }

    private Boolean checkWithAuthorHarvard(DateMidnight eventDate, String collector){

        String baseUrl = "http://kiki.huh.harvard.edu/databases/rdfgen.php?query=agent&name=";
        String url = baseUrl + collector.replace(" ", "%20"); //may need to change

        Model model = ModelFactory.createDefaultModel();
        //System.out.println("url = " + url);
        model.read(url);
        //model.write(System.out);
        //use object to find subject
        StmtIterator nis = model.listStatements();
        Resource birthSubject = null;
        Resource deathSubject = null;
        while (nis.hasNext()){
            Statement stmt = nis.next();
            //Property prop = new Property();
            if (stmt.getObject().toString().equals("http://purl.org/vocab/bio/0.1/Birth")){
                birthSubject = stmt.getSubject();
                //System.out.println("birthSubject = " + birthSubject);
            }else if (stmt.getObject().toString().equals("http://purl.org/vocab/bio/0.1/Death")) {
                deathSubject = stmt.getSubject();
                //System.out.println("deathSubject = " + deathSubject);
            }
        }
        //use subject and predicate to find object
        StmtIterator nis2 = model.listStatements();
        String birthDate = null;
        String deathDate = null;
        while (nis2.hasNext()){
            Statement stmt = nis2.next();
            //Property prop = new Property();
            birthDate = null;
            deathDate = null;

            if (stmt.getSubject().equals(birthSubject) && stmt.getPredicate().toString().equals("http://purl.org/vocab/bio/0.1/date")){
                birthDate = stmt.getObject().toString().replace("^^http://www.w3.org/2001/XMLSchema#date","");  //todo:manually select the number here
                //System.out.println("birth = " + birthDate);
            }else if (stmt.getSubject().equals(deathSubject) && stmt.getPredicate().toString().equals("http://purl.org/vocab/bio/0.1/date")) {
                deathDate = stmt.getObject().toString().replace("^^http://www.w3.org/2001/XMLSchema#date","");
                //System.out.println("deathDate = " + deathDate);
            }
        }

        int year = eventDate.year().get();
        if ( (birthDate != null && (year < Integer.parseInt(birthDate) + 10)) ||          //assume before 10 years old is not valid
                (deathDate != null && (year > Integer.parseInt(deathDate))) ) {
            curationStatus = CurationComment.UNABLE_CURATED;
            comment = "Internal inconsistent: eventDate:" + eventDate + " doesn't lie within the life span of collector:" + collector;
            return false;
        }else if (birthDate == null || deathDate == null){
            //curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
            comment = comment + " | Unable to get the Life span data of collector:" + collector;
            return false;
        }else{
            curationStatus = CurationComment.CORRECT;
            comment = comment + " | life span check is OK";
            return true;
        }
    }

    private Boolean checkWithAuthorSolr(DateMidnight eventDate, String collector){
        String url = "http://fp1.acis.ufl.edu:8983/solr/biologist" ;
        String birthLabel = "_birth_date_index";
        String deathLabel = "_death_date_index";

        try {
            SolrServer server = new HttpSolrServer(url);
            SolrQuery query = new SolrQuery();
            query.setQuery( "_biologist_name_index:\"" + collector + "\"");// + "AND _birth_date_index:*" );
            query.setFields(birthLabel, deathLabel);  //for efficiency
            // query.setQuery("*:*");
            QueryResponse rsp = server.query( query );
            SolrDocumentList docs = rsp.getResults();
            Iterator it = docs.iterator();
            while (it.hasNext()){
                //System.out.println(it.next().toString());
                SolrDocument doc = (SolrDocument)it.next();
                String birth = doc.get(birthLabel).toString();
                String death = doc.get(deathLabel).toString();
                //todo: handle multiple results
            }

        } catch (SolrServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //todo: code to compare birth and death of eventDate
        return true;
    }
	
	
	private File cacheFile = null;
    private boolean UsingSolr = false;

	private CurationStatus curationStatus;
	private String comment = "";
    private String correctEventDate;
	
	private HashMap<String,Vector<String>> authoritativeFloweringTimeMap = null; 
	private static final String ColumnDelimiterInCacheFile = "\t";
	
	private final String serviceName = "Harvard List of Botanists";
}
          /*
        SimpleDateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedEventDate = null;
        try {
            parsedEventDate = dataFormatter.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(parsedEventDate);
        */

        /*
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if(authoritativeFloweringTimeMap != null && authoritativeFloweringTimeMap.containsKey(scientificName.toLowerCase())){
			foundFloweringTime = authoritativeFloweringTimeMap.get(scientificName.toLowerCase());
		}

		if(foundFloweringTime == null){
			curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
			comment = "Can't find the flowering time of the "+scientificName+" in the current availabel phenoloty data from FNA.";
			correctedFloweringTime = null;
		}else{
			if(months==null || !months.containsAll(foundFloweringTime) || !foundFloweringTime.containsAll(months) ){
				curationStatus = CurationComment.CURATED;
				comment= "Update flowering time by using authoritative data from FNA";
				correctedFloweringTime = foundFloweringTime;
			}else{
				curationStatus = CurationComment.CORRECT;
				comment= "The flowering time is correct according to the authoritative data from FNA";
				correctedFloweringTime = months;
			}
		}
		*/
package org.filteredpush.kuration.services;

import com.hp.hpl.jena.rdf.model.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.filteredpush.kuration.interfaces.IInternalDateValidationService;
import org.filteredpush.kuration.util.CacheValue;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationException;
import org.filteredpush.kuration.util.CurationStatus;
import org.joda.time.DateMidnight;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Interval;
import org.joda.time.format.*;

import java.io.*;
import java.util.*;

//TODO: cache mechanism is not finished
public class InternalDateValidationService implements IInternalDateValidationService {
	
	private static final Log logger = LogFactory.getLog(InternalDateValidationService.class);

    private static boolean useCache = false;  //TODO: need to to fix cache
    private static int count = 0;
    HashMap<String, CacheValue> eventDateCache = new HashMap<String, CacheValue>();
    
	private File cacheFile = null;
    private boolean UsingSolr = true;
    private Double scoreThredhold = 3.0;

	private CurationStatus curationStatus;
	private String comment = "";
    private String correctEventDate = "";
	
	private HashMap<String,Vector<String>> authoritativeFloweringTimeMap = null; 
	private static final String ColumnDelimiterInCacheFile = "\t";
	
	private String serviceName = "";    
	
	private void init() { 
        comment = "";
        serviceName = "";
        correctEventDate = null;
	}

	public void validateDate(String eventDate, String verbatimEventDate, String startDayOfYear, String year, String month, String day, String modified, String collector) {
		init();
        curationStatus = CurationComment.CORRECT;

        // TODO: Assess if eventDate is a range
        
        DateMidnight consesEventDate = parseDate(eventDate, verbatimEventDate, startDayOfYear, year, month, day, modified);
        if(consesEventDate != null){
            // insert cache check functionality, put after consistency check to maximize hit rate

            /*
            if(useCache){
                if(eventDateCache.containsKey(collector)){
                    CacheValue hitValue = eventDateCache.get(collector);
                    comment += hitValue.getComment();
                    curationStatus = hitValue.getStatus();
                    serviceName = hitValue.getSource();
                    //System.out.println("count  = " + count++);
                    //System.out.println(collector);
                    return;
                }
            }
            */

            if (collector == null || collector.equals("")){
                comment = comment + " | collector name is not available";
            }else{
                // can switch between using old Harvard list or new Solr dataset
                Boolean inAuthorLife = false;
                if (UsingSolr){
                    inAuthorLife = checkWithAuthorSolr(consesEventDate, collector);
                    if (inAuthorLife==null) { 
                        inAuthorLife = checkWithAuthorHarvard(consesEventDate, collector);
                    }
                    //if(inAuthorLife) curationStatus = CurationComment.CORRECT;
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

    public void setCacheFile(String file) throws CurationException {
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

	public void flushCacheFile() throws CurationException {
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

	private void initializeCacheFile(String fileStr) throws CurationException {
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

	private void importFromCache() throws CurationException {
		authoritativeFloweringTimeMap = new HashMap<String,Vector<String>>();

		try {
			BufferedReader phenologyFileReader = new BufferedReader(new FileReader(cacheFile));
			String strLine = phenologyFileReader.readLine();
			while(strLine!=null){
				String[] info = strLine.split(ColumnDelimiterInCacheFile,-1);
				if(info.length!=3){
					throw new CurationException(getClass().getName()+" failed since the authoritative file "+cacheFile.toString()+" is invalid at "+strLine);
				}
				String taxon = info[0].trim().toLowerCase();
				String floweringTime = info[1].trim();
				authoritativeFloweringTimeMap.put(taxon, getMonthVector(floweringTime));

				strLine = phenologyFileReader.readLine();
			}
		} catch (FileNotFoundException e) {
			throw new CurationException(getClass().getName()+" failed to find the phenology authoritative file "+cacheFile.toString()+" for "+e.getMessage());
		} catch (IOException e) {
			throw new CurationException(getClass().getName()+" failed to read the phenology authoritative file "+cacheFile.toString()+" for "+e.getMessage());
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
    public DateMidnight parseDate (String eventDate, String verbatimEventDate, String startDayOfYear, String year, String month, String day, String modified) {
        DateMidnight parsedEventDate = null;      //also for consensus date
        DateMidnight constructedDate = null;
        // TODO: Handle ISO date ranges.
        
        // JODA ISO Date format supports only single dates, might be able to use interval.
        DateTimeFormatter format = ISODateTimeFormat.date();
        //for multiple date formats
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy/MM/dd").getParser(),
                ISODateTimeFormat.date().getParser() };
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
        
        // check to see if event date is a range.
        boolean isRange = false;
        String[] dateBits = eventDate.split("/");
        if (dateBits!=null && dateBits.length==2) { 
        	//probably a range.
        	try { 
        	   DateMidnight startDate = DateMidnight.parse(dateBits[0],formatter);
        	   DateMidnight endDate = DateMidnight.parse(dateBits[1],formatter);
        	   if (startDate.isAfter(endDate)) { 
                   comment = comment + " | Event date ("+eventDate+") appears to be a range, but the start date is after the end date.";
                   curationStatus = CurationComment.UNABLE_CURATED;
                   return null;
        	   }
               comment = comment + " | Event date ("+eventDate+") appears to be a range, treating the start date as the event date.";
        	   parsedEventDate = startDate;
        	   isRange = true;
        	} catch (Exception e) { 
                 comment = comment + " | Event date ("+eventDate+") appears to be a range, but can't parse out the start and end dates.";
        	}
        }
        
        
        if (parsedEventDate==null) { 
        	//get two eventDate first
        	try{
        		parsedEventDate = DateMidnight.parse(eventDate, formatter);
        	} catch(IllegalFieldValueException e){
        		//can't parse eventDate
        		//System.out.println("can't parse eventDate");
        		comment = comment + " | Can't parse eventDate";
        		parsedEventDate=null;
        	} catch(NullPointerException e){
        		//System.out.println("eventDate = " + eventDate);
        		comment = comment + " | eventDate is null?";
        	} catch(IllegalArgumentException e){
        		if(eventDate.length() == 0) comment += " | eventDate is empty";
        		else comment += " | not encoded format for date: " + eventDate;
        	}
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
            //System.out.println("parsedEventDate = " + parsedEventDate.toString());
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
                        curationStatus = CurationComment.CURATED;
                        comment += " | found and solved internal inconsistency with constructed date";
                    }else if (parsedEventDate.dayOfYear().get() != startDayInt){
                        curationStatus = CurationComment.UNABLE_CURATED;
                        comment = comment + " | Internal inconsistent: startDayOfYear:" + startDayInt + " and eventDate:" + parsedEventDate.toString(format) + " don't conform.";
                        return null;
                    }else if((parsedEventDate.dayOfYear().get() == startDayInt)) {
                        curationStatus = CurationComment.CORRECT;
                        comment += " | found and solved internal inconsistency with original date";
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

        if(modified != null && !modified.isEmpty()){
            //System.out.println("parsedEventDate = " + parsedEventDate.toString());
            //System.out.println("modified = " + modified);

            try{
                if (parsedEventDate.isAfter(DateMidnight.parse(modified.split(" ")[0], formatter)) ) {
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
        }
        return parsedEventDate;
    }

    /**
     * 
     * @param eventDate
     * @param collector
     * @return true if event date is inside collector's lifespan, false if it is outside, null if collector
     * is not found or if there is an error.
     */
    public Boolean checkWithAuthorHarvard(DateMidnight eventDate, String collector){
    	Boolean result = null;
        serviceName += "Harvard List of Botanists";
        String baseUrl = "http://kiki.huh.harvard.edu/databases/rdfgen.php?query=agent&name=";
        String url = baseUrl + collector.replace(" ", "%20"); //may need to change

        Model model = ModelFactory.createDefaultModel();
        logger.debug("url = " + url);
        model.read(url);
        //model.write(System.out);
        //use object to find subject
        if (model.isEmpty()) {
        	// no matches found.
        	return null;
        }
        StmtIterator nis = model.listStatements();
        Resource birthSubject = null;
        Resource deathSubject = null;
        while (nis.hasNext()){
            Statement stmt = nis.next();
            //Property prop = new Property();
            if (stmt.getObject().toString().equals("http://purl.org/vocab/bio/0.1/Birth")){
                birthSubject = stmt.getSubject();
                logger.debug("birthSubject = " + birthSubject);
            }else if (stmt.getObject().toString().equals("http://purl.org/vocab/bio/0.1/Death")) {
                deathSubject = stmt.getSubject();
                logger.debug("deathSubject = " + deathSubject);
            }
        }
        //use subject and predicate to find object
        StmtIterator nis2 = model.listStatements();
        String birthDate = null;
        String deathDate = null;
        while (nis2.hasNext()){
            Statement stmt = nis2.next();
            //Property prop = new Property();
            //birthDate = null;
            //deathDate = null;

            if (stmt.getSubject().equals(birthSubject) && stmt.getPredicate().toString().equals("http://purl.org/vocab/bio/0.1/date")){
                birthDate = stmt.getObject().toString().replace("^^http://www.w3.org/2001/XMLSchema#date","");  //todo:manually select the number here
                logger.debug("birthDate = " + birthDate);
            }else if (stmt.getSubject().equals(deathSubject) && stmt.getPredicate().toString().equals("http://purl.org/vocab/bio/0.1/date")) {
                deathDate = stmt.getObject().toString().replace("^^http://www.w3.org/2001/XMLSchema#date","");
                logger.debug("deathDate = " + deathDate);
            }
        }

        int year = eventDate.year().get();
        logger.debug(year);
        if ( (birthDate != null && (year < Integer.parseInt(birthDate) + 10)) ||          //assume before 10 years old is not valid
                (deathDate != null && (year > Integer.parseInt(deathDate))) ) {
            curationStatus = CurationComment.UNABLE_CURATED;
            comment = "Internal inconsistent: eventDate:" + eventDate + " doesn't lie within the life span of collector:" + collector;
            logger.debug(comment);
            // found a result and there is a mismatch = false;
            result =  false;
        } else if (birthDate == null || deathDate == null){
            //curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
            comment = comment + " | Unable to get the Life span data of collector:" + collector;
            logger.debug(comment);
            // did not find a result = null
            result = null;
        }else{
            curationStatus = CurationComment.CORRECT;
            comment = comment + " | life span check is OK";
            logger.debug(comment);
            // found a result and it is consistent
            result = true;
        }
        return result;
    }

    /**
     * Check if name is known on solr indexed list and if date is in lifespan.
     * 
     * @param eventDate to check if occurs within collectors lifespan
     * @param collector to check against date.
     * @return true if found and date is inside lifespan, false if found and date outside lifespan, null if
     * not found or a service error. 
     */
    public Boolean checkWithAuthorSolr(DateMidnight eventDate, String collector){
        serviceName += "Filteredpush Entomologists List";
        String url = "http://fp2.acis.ufl.edu:8983/solr/ento-bios/" ;
        String birthLabel = "birth";
        String deathLabel = "death";
        HashSet<HashMap<String, String>> lifeSpan = new HashSet<HashMap<String, String>>();
        collector = collector.replace("\"", "");

        //insert space after a dot
        int index = collector.indexOf(".", 0);
        while (index != -1){
            if (index < collector.length()-1 && (!collector.substring(index + 1, index + 2).equals(" "))){
                collector = collector.substring(0, index+1) + " " + collector.substring(index+1);
            }
            index = collector.indexOf(".", index+2);
        }

        if(collector.contains("[") || collector.contains("]") || collector.contains("(") || collector.contains(")") || collector.contains("&") || collector.contains(":")){
            //System.out.println("wired names:" + collector + "|"+wired);
            return false;
        }else if(!collector.contains(" ")){
            //todo: need to handle one component only
            //System.out.println("one name:" + collector + "|" + oneName);
            return false;
        }else{
            ModifiableSolrParams params = null;
            try {
                SolrServer server = new HttpSolrServer(url);
                params = new ModifiableSolrParams();
                //remove"[]"
                String distance = "~3";
                if (collector.trim().matches(".* .* .* .*")) {
                	// for names with four words, try a wider net
                	distance = "~4";
                }
                
                params.set("q", "namePre:\""+ collector + "\""+ distance);
                for (String item : collector.split(" ")){
                    if(!item.contains(".") && item.length() > 1){
                        params.add("fq", "name:" + item);
                    }
                }
                params.set("fl", "*,score");
                QueryResponse rsp = null;

                rsp = server.query( params );

                SolrDocumentList docs = rsp.getResults();
                Iterator it = docs.iterator();

                if(docs.size() == 0){
                    logger.debug("no result: " + collector);
                    comment = comment + " | Unable to get the Life span data of collector:" + collector;
                    if(useCache) eventDateCache.put(collector, new CacheValue().setComment(comment).setSource(serviceName).setStatus(curationStatus));
                    return null;
                }

                while (it.hasNext()){
                    SolrDocument doc = (SolrDocument)it.next();
                    if(Double.valueOf(doc.get("score").toString()) > scoreThredhold){
                        HashMap<String, String> birthAdnDeath = new HashMap<String, String>();
                        try {
                            birthAdnDeath.put(birthLabel, doc.get(birthLabel).toString());
                            birthAdnDeath.put(deathLabel, doc.get(deathLabel).toString());
                        }catch(Exception e){
                            logger.debug("doc.toString() = " + doc.toString());
                        }

                        lifeSpan.add(birthAdnDeath);
                    }
                    //TODO: handle multiple results
                }

            } catch (SolrException e) {
                System.out.println("-----");
                e.printStackTrace();
                System.out.println("params = " + params.toString());
                System.out.println("collector = " + collector);
                System.out.println("=====");
            } catch (SolrServerException e) {
                e.printStackTrace();
            }

            if(lifeSpan.size() == 0){
                logger.debug("no valid result: " + collector);
                comment = comment + " | Unable to get the valid life span data of collector:" + collector;
                if(useCache) eventDateCache.put(collector, new CacheValue().setComment(comment).setSource(serviceName).setStatus(curationStatus));
                return null;
            }else{
                logger.debug("has result: " + collector);
                boolean liesIn = true;
                //for handling empty birth or death date, set the default boundary
                int birth = 1000;
                int death = 2020;
                for(HashMap<String, String> birthAndDeath : lifeSpan){

                    if(birthAndDeath.containsKey(birthLabel) && !birthAndDeath.get(birthLabel).equals(" ")) {
                        birth = Integer.valueOf(birthAndDeath.get(birthLabel));
                        logger.debug("birth collector = " + birth);
                    }else{
                        logger.debug("no birth for collector = " + birth);
                    }
                    if(birthAndDeath.containsKey(deathLabel) && !birthAndDeath.get(deathLabel).equals(" ")) {
                        death = Integer.valueOf(birthAndDeath.get(deathLabel));
                        logger.debug("death collector = " + death);
                    } else{
                        logger.debug("no death for collector = " + collector);
                    }
                    if(eventDate.getYear() > death || eventDate.getYear() < birth){
                    	logger.debug("event date before birth or after death");
                        liesIn = false;
                    } else { 
                    	logger.debug(birth);
                    	logger.debug(eventDate.getYear());
                    	logger.debug(death);
                    }
                }
                logger.debug(liesIn);

                if(liesIn){
                    comment += " | eventDate "  + eventDate.getYear() + " lies within the life span (" + birth + "-" + death + ") of collector: " + collector + " (" + birth + " - " + death + ").";
                    logger.debug(comment);
                    if(useCache) eventDateCache.put(collector, new CacheValue().setComment(comment).setSource(serviceName).setStatus(curationStatus));
                    return true;

                } else{
                    comment += " | eventDate "  + eventDate.getYear() + " lies outside of the life span (" + birth + "-" + death + ") of collector: " + collector;
                    logger.debug(comment);
                    curationStatus = CurationComment.UNABLE_CURATED;
                    if(useCache) eventDateCache.put(collector, new CacheValue().setComment(comment).setSource(serviceName).setStatus(curationStatus));
                    return false;
                }
            }
        }
    }
	
    /**
     * Given a string that may be a date or a date range, extract an interval of
     * time from that date range.
     * 
     * @param eventDate
     * @return
     */
    public static Interval extractInterval(String eventDate) {
    	Interval result = null;
    	DateTimeParser[] parsers = { 
    			DateTimeFormat.forPattern("yyyy-MM").getParser(),
    			DateTimeFormat.forPattern("yyyy").getParser(),
    			ISODateTimeFormat.date().getParser() 
    	};
    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    	if (isRange(eventDate)) {
    		String[] dateBits = eventDate.split("/");
    		try { 
    			// must be at least a 4 digit year.
    			if (dateBits[0].length()>3 && dateBits[1].length()>3) { 
    				DateMidnight startDate = DateMidnight.parse(dateBits[0],formatter);
    				DateMidnight endDate = DateMidnight.parse(dateBits[1],formatter);
    				// both start date and end date must parse as dates.
    				result = new Interval(startDate, endDate);
    			}
    		} catch (Exception e) { 
    			// not a date range
               logger.error(e.getMessage());
    		}
    	} else {
    		try { 
               DateMidnight startDate = DateMidnight.parse(eventDate, formatter);
               logger.debug(startDate);
               if (eventDate.length()==4) { 
                  result = new Interval(startDate,startDate.plusMonths(12).minusDays(1));
               } else if (eventDate.length()==7) { 
                  result = new Interval(startDate,startDate.plusMonths(1).minusDays(1));
               } else { 
                  result = new Interval(startDate,startDate.plusDays(1));
               }
    		} catch (Exception e) { 
    			// not a date
               logger.error(e.getMessage());
    		}
    	}
    	return result;
    }
    
    /**
     * Test to see if a string appears to represent a date range.
     * 
     * @param eventDate to check
     * @return true if a date range, false otherwise.
     */
    public static boolean isRange(String eventDate) { 
    	boolean isRange = false;
    	String[] dateBits = eventDate.split("/");
    	if (dateBits!=null && dateBits.length==2) { 
    		//probably a range.
    		DateTimeParser[] parsers = { 
                    DateTimeFormat.forPattern("yyyy-MM").getParser(),
                    DateTimeFormat.forPattern("yyyy").getParser(),
    				ISODateTimeFormat.date().getParser() 
    		};
    		DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    		try { 
    			// must be at least a 4 digit year.
    			if (dateBits[0].length()>3 && dateBits[1].length()>3) { 
    			   DateMidnight startDate = DateMidnight.parse(dateBits[0],formatter);
    			   DateMidnight endDate = DateMidnight.parse(dateBits[1],formatter);
    			   // both start date and end date must parse as dates.
    			   isRange = true;
    			}
    		} catch (Exception e) { 
    			// not a date range
    		}
    	}
    	return isRange;
    }
}
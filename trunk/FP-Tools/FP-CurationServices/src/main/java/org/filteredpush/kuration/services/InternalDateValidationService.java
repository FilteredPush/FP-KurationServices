package org.filteredpush.kuration.services;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.JenaException;

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
import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.Interval;
import org.joda.time.format.*;

import java.io.*;
import java.net.ConnectException;
import java.util.*;

//TODO: cache mechanism is not finished
/**
 * Check collecting dates for internal consistency and consistency with known collector birth/death
 * records.
 * 
 * @author Tianhong Song
 * @author mole
 *
 */
public class InternalDateValidationService extends BaseCurationService implements IInternalDateValidationService {
	
	private static final Log logger = LogFactory.getLog(InternalDateValidationService.class);

    private static boolean useCache = false;  //TODO: need to to fix cache
    private static int count = 0;
    HashMap<String, CacheValue> eventDateCache = new HashMap<String, CacheValue>();
    
	private File cacheFile = null;
    private boolean UsingSolr = true;
    private Double scoreThredhold = 3.0;

    private String correctEventDate = "";
	
	private HashMap<String,Vector<String>> authoritativeFloweringTimeMap = null; 
	private static final String ColumnDelimiterInCacheFile = "\t";
	
	public InternalDateValidationService() { 
		super();
		initDate();
	}
	
	private void initDate() { 
        correctEventDate = null;
	}

	public void validateDate(String eventDate, String verbatimEventDate, String startDayOfYear, String year, String month, String day, String modified, String collector) {
		initDate();
        correctEventDate = null;
		setCurationStatus(CurationComment.CORRECT);
        addToServiceName("eventDate:" + eventDate + "#");
        this.addInputValue("eventDate", eventDate);

        if (eventDate!=null && (eventDate.length()==4 || eventDate.length()==7 )) {
        	Interval interval = extractInterval(eventDate);
        	eventDate = interval.getStart().toString("yyyy-MM-dd") + "/" + interval.getEnd().toString("yyyy-MM-dd");
        	setCurationStatus(CurationComment.CURATED);
            addToComment("expanded event date to a date range");
        }
        
        DateMidnight consesEventDate = parseDate(eventDate, verbatimEventDate, startDayOfYear, year, month, day, modified);
        if(consesEventDate != null){
        	logger.debug(consesEventDate.toString("yyyy-MM-dd"));
            // insert cache check functionality, put after consistency check to maximize hit rate

            /*
            if(useCache){
                if(eventDateCache.containsKey(collector)){
                    CacheValue hitValue = eventDateCache.get(collector);
                    comment += hitValue.getComment();
                    setCurationStatus(hitValue.getStatus();
                    serviceName = hitValue.getSource();
                    //System.out.println("count  = " + count++);
                    //System.out.println(collector);
                    return;
                }
            }
            */

            if (collector == null || collector.equals("")){
                addToComment("collector name is not available");
            }else{
            	Boolean inAuthorLife = null;
            	if (isRange(eventDate)) { 
            	   inAuthorLife = checkAgentAuthorities(eventDate, collector);
            	} else { 
            	   inAuthorLife = checkAgentAuthorities(consesEventDate.toString("yyyy-MM-dd"), collector);
            	}
            	/*
                // can switch between using old Harvard list or new Solr dataset
                Boolean inAuthorLife = false;
                if (UsingSolr){
                    inAuthorLife = checkWithAuthorSolr(consesEventDate, collector);
                    if (inAuthorLife==null) { 
                        inAuthorLife = checkWithAuthorHarvard(consesEventDate, collector);
                    }
                    //if(inAuthorLife) setCurationStatus(CurationComment.CORRECT;
                }
               else{
                    inAuthorLife = checkWithAuthorHarvard(consesEventDate, collector);
                }
                */
            }
        }
    }
	
	/**
	 * Check to see if an event date lies inside an agent's lifetime.
	 * 
	 * @param eventString event date as a string
	 * @param collector to check to see if the event date lies inside a known lifetime
	 * @return true if the event lies inside or overlaps lifetime of the collector, false if the event
	 * lies outside the lifetime of the collector, null if check could not be made.
	 */
	public Boolean checkAgentAuthorities(String eventString, String collector) {
		Boolean result = null;
		Boolean correctionProposed = false;
	    String lifeYears = "";
		if (isRange(eventString)) { 
		    Interval eventInterval = InternalDateValidationService.extractInterval(eventString);
		    if (eventInterval!=null) { 
		    	// TODO: Check if collecting event date was before collector was 10 years old.
		       Interval lifeSpan = this.lookUpHarvardBotanist(collector);
		       if (lifeSpan!=null) { 
		    	   result = lifeSpan.overlaps(eventInterval);
		    	   lifeYears = Integer.toString(lifeSpan.getStart().getYear()) + "-" + Integer.toString(lifeSpan.getEnd().getYear()); 
		       } 
		       if (result==null) { 
		    	   lifeSpan = this.lookupEntomologist(collector);
		           if (lifeSpan!=null) { 
		    	      result = lifeSpan.overlaps(eventInterval);
		    	      lifeYears = Integer.toString(lifeSpan.getStart().getYear()) + "-" + Integer.toString(lifeSpan.getEnd().getYear()); 
		           } 
		       }
		       if (result!=null && result) { 
		    	   DateMidnight startDate = new DateMidnight(eventInterval.getStart()); 
		    	   if (eventInterval.getStart().isBefore(lifeSpan.getStart())) { 
		    		   // Propose truncation
		    		   setCurationStatus(CurationComment.CURATED);
                       addToComment("eventDate partially overlaps and starts before the life span of the collector, proposing truncation ");
                       correctionProposed = true;
                       correctEventDate = lifeSpan.getStart().plusYears(10).toString("yyyy-MM-dd") + "/" + eventInterval.getEnd().toString("yyyy-MM-dd");
		    	       startDate = new DateMidnight(lifeSpan.getStart().plusYears(10)); 
		    	   }
		    	   if (eventInterval.getEnd().isAfter(lifeSpan.getEnd())) { 
		    		   // Propose truncation
		    		   setCurationStatus(CurationComment.CURATED);
                       addToComment("eventDate partially overlaps and ends after the life span of the collector, proposing truncation ");
                       correctionProposed = true;
                       DateTime endDay = lifeSpan.getEnd();
                       if (lifeSpan.getEnd().getDayOfYear()==1 && lifeSpan.getEnd().getMonthOfYear()==1) {
                    	   // if january 1, probably unknown day, move to dec 31.
                          endDay = lifeSpan.getEnd().plusYears(1).minusDays(1);
                       }
                       correctEventDate = startDate.toString("yyyy-MM-dd") + "/" + endDay.toString("yyyy-MM-dd");
		    	   }
		       }
		    }
		} else { 
			DateMidnight eventDate = InternalDateValidationService.extractDate(eventString);
			if (eventDate!=null) { 
				Interval lifeSpan = this.lookUpHarvardBotanist(collector);
			    if (lifeSpan!=null) { 
			    	   result = lifeSpan.contains(eventDate);
			    } 
			    if (result==null) { 
			        lifeSpan = this.lookupEntomologist(collector);
			        if (lifeSpan!=null) { 
			    	      result = lifeSpan.contains(eventDate);
			        } 
			    }
			}
		}
		if (result!=null) { 
		   if (result) { 
			   if (!correctionProposed) { 
			       if (!getCurationStatus().equals(CurationComment.Filled_in)) { 
			    	   setCurationStatus(CurationComment.CORRECT);
			       } 
                   addToComment("eventDate falls inside the life span of the collector " + lifeYears);
                   logger.debug(getComment());
			   }
		   } else { 
			   setCurationStatus(CurationComment.UNABLE_CURATED);
              addToComment("eventDate " + eventString +" falls outside the life span of the collector " + collector + " " + lifeYears);
              logger.debug(getComment());
		   }
		} else { 
		   // no match or problem getting a match
		   // Since collector data is sparse, don't assert invalidity based on not being able to find a collector.
           addToComment("Unable to lookup a lifespan for the collector " + collector);
		}
		return result;
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
                ISODateTimeFormat.date().getParser()
                };
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
        
        // check to see if event date is a range.
        boolean isRange = false;
        if (eventDate!=null) { 
        String[] dateBits = eventDate.split("/");
        if (dateBits!=null && dateBits.length==2) { 
        	//probably a range.
        	try { 
        	   DateMidnight startDate = DateMidnight.parse(dateBits[0],formatter);
        	   DateMidnight endDate = DateMidnight.parse(dateBits[1],formatter);
        	   if (startDate.isAfter(endDate)) { 
                   addToComment("Event date ("+eventDate+") appears to be a range, but the start date is after the end date.");
                   setCurationStatus(CurationComment.UNABLE_CURATED);
                   return null;
        	   }
               addToComment("Event date ("+eventDate+") appears to be a range, treating the start date as the event date.");
        	   parsedEventDate = startDate;
        	   isRange = true;
        	} catch (Exception e) { 
                 addToComment("Event date ("+eventDate+") appears to be a range, but can't parse out the start and end dates.");
        	}
        }
        }
        
        if (parsedEventDate==null) { 
        	//get two eventDate first
        	try{
        		parsedEventDate = DateMidnight.parse(eventDate, formatter);
        	} catch(IllegalFieldValueException e){
        		//can't parse eventDate
        		//System.out.println("can't parse eventDate");
        		addToComment("Can't parse eventDate");
        		parsedEventDate=null;
        	} catch(NullPointerException e){
        		//System.out.println("eventDate = " + eventDate);
        		addToComment("eventDate is null?");
        	} catch(IllegalArgumentException e){
        		if(eventDate.length() == 0) { 
        			addToComment("eventDate is empty");
        		} else {
        			addToComment("not encoded format for date: " + eventDate);
        		}
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
            addToComment("can't construct eventDate from atomic fields: string casting error");
            constructedDate = null;
        }catch (IllegalFieldValueException e) {
            //can't construct eventDate
            //System.out.println("can't construct eventDate from atomic fields: parsing error");
            addToComment("can't construct eventDate from atomic fields: parsing error");
            constructedDate = null;
        }

        //second, compare in different cases
        if (parsedEventDate == null && constructedDate == null && !isRange ){
            setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
            addToComment("Can't get a valid eventDate from the record");
            return null;
        }else if (parsedEventDate != null && constructedDate == null){
            //System.out.println("parsedEventDate = " + parsedEventDate.toString());
            if (!parsedEventDate.toString(format).equals(eventDate) && !isRange(eventDate)) {
                setCurationStatus(CurationComment.CURATED);
                addToComment("eventDate:" + eventDate + " has been formatted to ISO format: " + parsedEventDate.toString(format) +".");
                correctEventDate=parsedEventDate.toString(format);
            }else{
            	if (isRange(eventDate)) { 
                   addToComment("eventDate is range in ISO format");
            	} else { 
                   addToComment("eventDate is in ISO format");
            	}
            }
            //todo: status will be overwritten if inconsistent, needs to be preserved?
        }else if (parsedEventDate == null && constructedDate != null){
            setCurationStatus(CurationComment.Filled_in);
            addToComment("EventDate is constructed from atomic fields");
            parsedEventDate = constructedDate;
            correctEventDate=parsedEventDate.toString(format);
        }else{
            //if two dates don't conform, use startDayOfYear to distinguish
            if(!parsedEventDate.isEqual(constructedDate)){
                if(startDayOfYear != null){
                    int startDayInt = Integer.parseInt(startDayOfYear);
                    if (constructedDate.dayOfYear().get() == startDayInt) {
                        parsedEventDate = constructedDate;
                        setCurationStatus(CurationComment.CURATED);
                        addToComment("found and solved internal inconsistency with constructed date");
                    }else if (parsedEventDate.dayOfYear().get() != startDayInt){
                        setCurationStatus(CurationComment.UNABLE_CURATED);
                        addToComment("Internal inconsistency: startDayOfYear:" + startDayInt + " and eventDate:" + parsedEventDate.toString(format) + " don't conform.");
                        return null;
                    }else if((parsedEventDate.dayOfYear().get() == startDayInt)) {
                        setCurationStatus(CurationComment.CORRECT);
                        addToComment("found and solved internal inconsistency with original date");
                    }
                }else{
                    setCurationStatus(CurationComment.UNABLE_CURATED);
                    addToComment("Internal inconsistency: EventDate:" + parsedEventDate.toString(format) + " doesn't conform to constructed date:" + constructedDate);
                    return null;
                }
            }else{
                setCurationStatus(CurationComment.CORRECT);
                addToComment("eventDate is consistent with atomic fields");
            }
        }

        //compare with other dates

        if(modified != null && !modified.isEmpty()){
            //System.out.println("parsedEventDate = " + parsedEventDate.toString());
            //System.out.println("modified = " + modified);
        	
            DateTimeParser[] modifiedParsers = {
                    DateTimeFormat.forPattern("yyyy/MM/dd").getParser(),
                    ISODateTimeFormat.date().getParser(),
                    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser(),
                    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS").getParser(),
                    ISODateTimeFormat.dateTimeParser().getParser() };
            formatter = new DateTimeFormatterBuilder().append( null, modifiedParsers ).toFormatter();      	

            try{
                // if (parsedEventDate.isAfter(DateMidnight.parse(modified.split(" ")[0], formatter)) ) {
                if (parsedEventDate.isAfter(DateMidnight.parse(modified, formatter)) ) {
                    setCurationStatus(CurationComment.UNABLE_CURATED);
                    addToComment("Internal inconsistent: EventDate:" + parsedEventDate.toString(format) + " occurs after modified date:" + DateMidnight.parse(modified.split(" ")[0], format) + ".");
                    return null;
                } else{
                    addToComment("eventDate is consistent with modified date");
                }

            }catch(IllegalFieldValueException e) {
                //can't format modified date
                addToComment("cannot parse modified date");
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
    @Deprecated
    public Boolean checkWithAuthorHarvard(DateMidnight eventDate, String collector){
    	Boolean result = null;
        addToServiceName("Harvard List of Botanists");
        String baseUrl = "http://kiki.huh.harvard.edu/databases/rdfgen.php?query=agent&name=";
        String url = baseUrl + collector.replace(" ", "%20"); //may need to change

        Model model = ModelFactory.createDefaultModel();
        logger.debug("url = " + url);
        model.read(url);
        //model.write(System.out);
        //use object to find subject
        if (model.isEmpty()) {
        	// no matches found.
            //insert space after a period
            collector = collector.replaceAll("\\.", ". ").replaceAll("  ", " ");
            // try again
    	    url = baseUrl + collector.replace(" ", "%20"); //may need to change
    	    logger.debug("url = " + url);
    	    model = ModelFactory.createDefaultModel();
    	    model.read(url);
    	    if (model.isEmpty()) {
                addToComment("Unable to find collector " + collector + " in Harvard list of botanists.");
    		    return null;
    	    }
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
            setCurationStatus(CurationComment.UNABLE_CURATED);
            addToComment("Internal inconsistent: eventDate:" + eventDate + " doesn't lie within the life span of collector:" + collector);
            logger.debug(getComment());
            // found a result and there is a mismatch = false;
            result =  false;
        } else if (birthDate == null || deathDate == null){
            //setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY;
            addToComment("Unable to get the Life span data of collector:" + collector);
            logger.debug(getComment());
            // did not find a result = null
            result = null;
        }else{
            setCurationStatus(CurationComment.CORRECT);
            addToComment("life span check is OK");
            logger.debug(getComment());
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
    @Deprecated
    public Boolean checkWithAuthorSolr(DateMidnight eventDate, String collector){
        addToServiceName("Filteredpush Entomologists List");
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
                    addToComment("Unable to get the Life span data of collector:" + collector);
                    if(useCache) eventDateCache.put(collector, new CacheValue().setComment(getComment()).setSource(getServiceName()).setStatus(getCurationStatus()));
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
                System.out.println(e.getMessage());
                System.out.println("params = " + params.toString());
                System.out.println("collector = " + collector);
                System.out.println("=====");
                logger.error(e.getMessage(),e);
            } catch (SolrServerException e) {
                System.out.println(e.getMessage());
                logger.error(e.getMessage(),e);
            }

            if(lifeSpan.size() == 0){
                logger.debug("no valid result: " + collector);
                addToComment("Unable to get the valid life span data of collector:" + collector);
                if(useCache) eventDateCache.put(collector, new CacheValue().setComment(getComment()).setSource(getServiceName()).setStatus(getCurationStatus()));
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
                    addToComment("eventDate "  + eventDate.getYear() + " lies within the life span (" + birth + "-" + death + ") of collector: " + collector + " (" + birth + " - " + death + ").");
                    logger.debug(getComment());
                    if(useCache) eventDateCache.put(collector, new CacheValue().setComment(getComment()).setSource(getServiceName()).setStatus(getCurationStatus()));
                    return true;

                } else{
                    addToComment("eventDate "  + eventDate.getYear() + " lies outside of the life span (" + birth + "-" + death + ") of collector: " + collector);
                    logger.debug(getComment());
                    setCurationStatus(CurationComment.UNABLE_CURATED);
                    if(useCache) eventDateCache.put(collector, new CacheValue().setComment(getComment()).setSource(getServiceName()).setStatus(getCurationStatus()));
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
     * Extract a joda date from an event date.
     * 
     * @param eventDate
     * @return
     */
    public static DateMidnight extractDate(String eventDate) {
    	DateMidnight result = null;
    	DateTimeParser[] parsers = { 
    			DateTimeFormat.forPattern("yyyy-MM").getParser(),
    			DateTimeFormat.forPattern("yyyy").getParser(),
    			ISODateTimeFormat.date().getParser() 
    	};
    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    		try { 
               result = DateMidnight.parse(eventDate, formatter);
               logger.debug(result);
    		} catch (Exception e) { 
    			// not a date
               logger.error(e.getMessage());
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
    
    /**
     * Look up birth and death dates for a botanist in the Harvard list of botanists.
     * Uses now as the end of the interval if a birth date is given but no death date is 
     * given.
     * 
     * @param collector botanist to look up.
     * @return null or an interval from either birth to death or birth to the present.
     */
    public Interval lookUpHarvardBotanist(String collector) { 
    	Interval result = null;
    	addToServiceName("Harvard List of Botanists");
    	String baseUrl = "http://kiki.huh.harvard.edu/databases/rdfgen.php?query=agent&name=";
    	String url = baseUrl + collector.replace(" ", "%20"); //may need to change

    	Model model = ModelFactory.createDefaultModel();
    	logger.debug("url = " + url);
    	try { 
    	    model.read(url);
    	} catch (JenaException e) {
    		if (e.getCause().getClass().equals(ConnectException.class)) {
    			// try again
    			try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					// continue
				}
    			try {
    		       model.read(url);
    			} catch (JenaException ex) { 
    				System.out.println(ex.getMessage());
    			}
    		}
    	}
    	logger.debug(model.listStatements().hasNext());
    	if (model.isEmpty()) {
    		// no matches found.
            //insert space after a period
            String collector2 = collector.replaceAll("\\.", ". ").replaceAll("  ", " ").trim();
            if (!collector2.equals(collector)) { 
               // try again
    	       url = baseUrl + collector2.replace(" ", "%20"); //may need to change
    	       logger.debug("url = " + url);
    	       model = ModelFactory.createDefaultModel();
    	       model.read(url);
            }
    	    if (model.isEmpty()) {
                addToComment("Unable to find collector " + collector + " in Harvard list of botanists.");
    		    return null;
    	    }
    	}
    	//use object to find subject
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

    		if (stmt.getSubject().equals(birthSubject) && stmt.getPredicate().toString().equals("http://purl.org/vocab/bio/0.1/date")){
    			birthDate = stmt.getObject().toString().replace("^^http://www.w3.org/2001/XMLSchema#date","");  //todo:manually select the number here
    			logger.debug("birthDate = " + birthDate);
    		}else if (stmt.getSubject().equals(deathSubject) && stmt.getPredicate().toString().equals("http://purl.org/vocab/bio/0.1/date")) {
    			deathDate = stmt.getObject().toString().replace("^^http://www.w3.org/2001/XMLSchema#date","");
    			logger.debug("deathDate = " + deathDate);
    		}
    	}
    	if (birthDate!=null) { 
    		DateMidnight endDate;
    		DateMidnight startDate = extractDate(birthDate);
    		if (deathDate!=null) { 
    			endDate = extractDate(deathDate);
    		} else { 
    			// If a birth date is given, but no death date, assume botanist is living.
    			endDate = new DateMidnight();
    		}
            addToComment("Found " + collector + "("+startDate +"-"+endDate+") in Harvard botanists list.");
    		result = new Interval(startDate, endDate);
    	}  else  {
            addToComment("Unable to get life span data of collector:" + collector);
    	}

    	return result;
    }
    
    public Interval lookupEntomologist(String collector) { 
    	Interval result = null;
    	 addToServiceName("FilteredPush Entomologists List");
         String url = "http://fp2.acis.ufl.edu:8983/solr/ento-bios/" ;
         String birthLabel = "birth";
         String deathLabel = "death";
         HashSet<HashMap<String, String>> lifeSpan = new HashSet<HashMap<String, String>>();
         collector = collector.replace("\"", "");

         //insert space after a period
         int index = collector.indexOf(".", 0);
         while (index != -1){
             if (index < collector.length()-1 && (!collector.substring(index + 1, index + 2).equals(" "))){
                 collector = collector.substring(0, index+1) + " " + collector.substring(index+1);
             }
             index = collector.indexOf(".", index+2);
         }

         if(collector.contains("[") || collector.contains("]") || collector.contains("(") || collector.contains(")") || collector.contains("&") || collector.contains(":")){
             logger.debug("excluded name:" + collector);
             addToComment("Skipping lookup for collector:" + collector);
         } else if(!collector.contains(" ")){
             //TODO: need to handle one component only
             logger.debug("one name:" + collector);
             addToComment("Skipping lookup for collector:" + collector);
         } else {
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
                 try { 
                     rsp = server.query( params );
                 } catch (SolrServerException ex) { 
                	 // pause and try again.
                	try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// continue
					}
                    rsp = server.query( params );
                 }

                 SolrDocumentList docs = rsp.getResults();
                 Iterator it = docs.iterator();

                 if(docs.size() == 0){
                     logger.debug("no result: " + collector);
                     addToComment("Unable to get the Life span data of collector:" + collector);
                     if(useCache) eventDateCache.put(collector, new CacheValue().setComment(getComment()).setSource(getServiceName()).setStatus(getCurationStatus()));
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
                 logger.error(e.getMessage(),e);
                 System.out.println("params = " + params.toString());
                 System.out.println("collector = " + collector);
                 System.out.println("=====");
             } catch (SolrServerException e) {
            	 System.out.println(e.getMessage());
                 logger.error(e.getMessage(),e);
             }

             if(lifeSpan.size() == 0){
                 logger.debug("no valid result: " + collector);
                 addToComment("Unable to get the valid life span data of collector:" + collector);
                 if(useCache) eventDateCache.put(collector, new CacheValue().setComment(getComment()).setSource(getServiceName()).setStatus(getCurationStatus()));
                 return null;
             }else{
                 logger.debug("has result: " + collector);
                 boolean liesIn = true;
                 int birth = 0;
                 int death = 0;
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
                 }
                 if (birth>0 && death>0) { 
                	 String startDate = Integer.toString(birth);
                	 String endDate = Integer.toString(death);
                	 if (death>birth) { 
                		 addToComment("  | Found " + collector + "("+startDate +"-"+endDate+") in SCAN entomologists list.");
                	     result = new Interval(InternalDateValidationService.extractDate(startDate), InternalDateValidationService.extractDate(endDate));
                	 } else { 
                		 logger.debug("Can't construct interval from " + startDate  +"-" + endDate );
                	 }
                 }
             }
         }   	
         return result;
    }
    
}
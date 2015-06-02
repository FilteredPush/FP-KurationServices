package org.filteredpush.kuration.services;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.filteredpush.kuration.interfaces.IExternalDateValidationService;
import org.filteredpush.kuration.util.*;
import org.joda.time.DateMidnight;
import org.joda.time.Days;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

//TODO: cache mechanism is not finished
//TODO: services have changed location
/**
 * Check a collecting event date by comparison to known birth/death dates and for
 * clustering with nearby (temporaly and spatialy) collecting event dates by the 
 * same collector.  External as in comparison to other collecting event records.
 * 
 * @author Tianhong Song
 *
 */
public class ExternalDateValidationService extends BaseCurationService implements IExternalDateValidationService {

    private boolean useCache;
	private File cacheFile = null;

    private String _mongodbHost = "fp1.acis.ufl.edu";
    private String _mongodbDB = "db";
    private String _mongodbCollection = "Occurrence";
    //private String _mongodbQuery = "{year:\"1898\"}";
    private DBCursor cursor = null;
    private int totalRecords = 0;
    private boolean useSolr = false;

    // the range of date interval for querying reference record
    // assume the record occurs 7 days after/before the eventDate, no point to use as reference
    private int referenceEventDateRange = 7;

    private final int temporalDistanceThreshold = 7; //in day
    private final int travelDistanceThreshold = 1000; //in km/day
    private String correctEventDate;
	
	private HashMap<String,Vector<String>> authoritativeFloweringTimeMap = null; 
	private static final String ColumnDelimiterInCacheFile = "\t";
	
	private final String serviceName = "Harvard List of Botanists";     

    public void validateDate(DateMidnight eventDate, String collector, String latitude, String longitude) {
        HashSet<HashMap<String, String>> resultSet;
        // can switch between querying mongoDB or Solr index
        if (useSolr){
            resultSet = querySolr(collector, eventDate);
        }
        else {
            resultSet = queryMongodb(collector, eventDate);
        }

        if (resultSet != null){
             checkForOutlier(eventDate, latitude, longitude, resultSet);
        }

    }

    public void setCacheFile(String file) throws CurationException {
        useCache = true;
        initializeCacheFile(file);
        importFromCache();
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
				//authoritativeFloweringTimeMap.put(taxon, getMonthVector(floweringTime));

				strLine = phenologyFileReader.readLine();
			}
		} catch (FileNotFoundException e) {
			throw new CurationException(getClass().getName()+" failed to find the phenology authoritative file "+cacheFile.toString()+" for "+e.getMessage());
		} catch (IOException e) {
			throw new CurationException(getClass().getName()+" failed to read the phenology authoritative file "+cacheFile.toString()+" for "+e.getMessage());
		}
	}


    //////////////////////////////////

    private DateMidnight parseEventDate(String eventDate){
        DateMidnight eventDateInReference = null;
        DateTimeFormatter format = ISODateTimeFormat.date();
        try{
            eventDateInReference = DateMidnight.parse(eventDate, format);
        } catch(IllegalFieldValueException e){
            //can't parse eventDate
            System.out.println("can't parse eventDate"+ eventDate + "in reference record");
            eventDateInReference=null;
        }
        return eventDateInReference;
    }

    private HashSet<HashMap<String, String>> queryMongodb (String collector, DateMidnight eventDate) {
        HashSet<HashMap<String, String>> resultSet = null;

        String mongodbQuery = "{recordBy:\"" + collector + "\"}";

        try {
            //System.out.println(" Host: " + _mongodbHost);
            MongoClient mongoClient = new MongoClient(_mongodbHost);
            DB db = mongoClient.getDB(_mongodbDB);
            DBCollection coll = db.getCollection(_mongodbCollection);

            totalRecords = 0;
            if (!mongodbQuery.isEmpty()) {
                //System.out.println(" With query: "+ _mongodbQuery);
                Object query = JSON.parse(mongodbQuery);
                cursor = coll.find((DBObject)query);
                totalRecords = cursor.count();
            } else {
                System.out.println("external validation Without query!");
            }

            while (cursor.hasNext()) {
                DBObject dbo = cursor.next();
                // first check whether the record has eventDate or not
                if (dbo.keySet().contains("eventDate")){
                    //second, check whether the reference record is close to the eventDate of the validating record
                    DateMidnight eventDateInReference = parseEventDate(dbo.get("eventDate").toString());
                    if (!eventDateInReference.equals(null) && Math.abs(Days.daysBetween(eventDate, eventDateInReference).getDays()) < temporalDistanceThreshold) {
                        HashMap<String, String> outMap = new SpecimenRecord();
                        outMap.put("eventDate", dbo.get("eventDate").toString());
                        outMap.put("decimalLatitude", dbo.get("decimalLatitude").toString());
                        outMap.put("decimalLongitude", dbo.get("decimalLongitude").toString());
                        resultSet.add(outMap);
                    }
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (cursor != null) cursor.close();
        return resultSet;
    }

    private HashSet<HashMap<String, String>> querySolr (String collector , DateMidnight eventDate) {
        HashSet<HashMap<String, String>> resultSet = null;
        // Old location for index.
        String url = "http://fp1.acis.ufl.edu:8983/solr/biologist" ;
        String dateLabel = "eventDate";
        String latitudeLabel = "decimalLatitude";
        String longitudeLabel = "decimalLongitude";

        try {
            SolrServer server = new HttpSolrServer(url);
            SolrQuery query = new SolrQuery();
            query.setQuery( "recordedBy:\"" + collector + "\"");
            query.setFields(dateLabel, latitudeLabel, longitudeLabel);  //for efficiency

            QueryResponse rsp = server.query( query );
            SolrDocumentList docs = rsp.getResults();
            Iterator it = docs.iterator();
            while (it.hasNext()){
                HashMap<String, String> outMap = new SpecimenRecord();
                SolrDocument doc = (SolrDocument)it.next();

                // first check whether the record has eventDate or not
                if (doc.keySet().contains(dateLabel)){
                    //second, check whether the reference record is close to the eventDate of the validating record
                    String date = doc.get(dateLabel).toString();
                    DateMidnight eventDateInReference = parseEventDate(doc.get(dateLabel).toString());
                    if (!eventDateInReference.equals(null) && Math.abs(Days.daysBetween(eventDate, eventDateInReference).getDays()) < temporalDistanceThreshold) {
                        outMap.put("eventDate", date);
                        outMap.put("decimalLatitude", doc.get(latitudeLabel).toString());
                        outMap.put("decimalLongitude", doc.get(longitudeLabel).toString());
                        resultSet.add(outMap);
                    }
                }
            }
        } catch (SolrServerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return resultSet;
    }

    private void checkForOutlier(DateMidnight eventDate, String latitude, String longitude, HashSet<HashMap<String, String>> referenceSet){
        boolean isOutlier = false;
        DateTimeFormatter format = ISODateTimeFormat.date();
        for (HashMap<String, String> map :referenceSet ) {
            format = ISODateTimeFormat.date();
            DateMidnight ReferenceEventDate = DateMidnight.parse(map.get("eventDate"), format);
            long refLatitude = Long.parseLong(map.get("decimalLatitude"));
            long refLongitude = Long.parseLong(map.get("decimalLongitude"));
            long dayDifference = Math.abs( (eventDate.getMillis() - ReferenceEventDate.getMillis())/86400000 );

            if(dayDifference < temporalDistanceThreshold){   //no point to calculate two eventDate far away from each other
                double travelDistancePerDay = GEOUtil.getDistanceKm(Long.parseLong(latitude), Long.parseLong(longitude), refLatitude, refLongitude)/dayDifference;
                if(travelDistancePerDay>=travelDistanceThreshold){
                    isOutlier = true;
                    break;
                }
            }
        }
        if(isOutlier){
            setCurationStatus(CurationComment.UNABLE_CURATED);
            addToComment("This record is a spatial outlier since the location is far away from locations of reference records within " + temporalDistanceThreshold + " days of " + eventDate.toString(format));
        }else{
            setCurationStatus(CurationComment.CORRECT);
            addToComment("This record is a spatial outlier since the location is far away from locations of reference records within " + temporalDistanceThreshold + " days of " + eventDate.toString(format));
            //todo: add other two status here
        }
    }

	
}
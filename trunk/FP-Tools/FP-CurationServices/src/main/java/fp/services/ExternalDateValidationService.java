package fp.services;

import com.mongodb.*;
import com.mongodb.util.JSON;
import fp.util.*;
import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.*;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

//todo: cache machanism is not finished
public class ExternalDateValidationService implements IExternalDateValidationService {

    private boolean useCache;


    public void validateDate(DateMidnight eventDate, String collector, String latitude, String longitude) {
        HashSet<HashMap<String, String>> resultSet = queryMongodb(collector);
        if (resultSet != null){
             checkForOutlier(eventDate, latitude, longitude, resultSet);
        }

    }

    public void setCacheFile(String file) throws CurrationException {
        useCache = true;
        initializeCacheFile(file);
        importFromCache();
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
				//authoritativeFloweringTimeMap.put(taxon, getMonthVector(floweringTime));

				strLine = phenologyFileReader.readLine();
			}
		} catch (FileNotFoundException e) {
			throw new CurrationException(getClass().getName()+" failed to find the phenology authoritative file "+cacheFile.toString()+" for "+e.getMessage());
		} catch (IOException e) {
			throw new CurrationException(getClass().getName()+" failed to read the phenology authoritative file "+cacheFile.toString()+" for "+e.getMessage());
		}
	}


    //////////////////////////////////
    private HashSet<HashMap<String, String>> queryMongodb (String collector) {
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

            do {
                DBObject dbo = cursor.next();
                HashMap<String, String> outMap = new SpecimenRecord();

                for (String key : dbo.keySet()) {
                    if (key.contains("eventDate")) outMap.put("eventDate", dbo.get(key).toString());
                    else{
                        //there is no point of putting coordinates in if eventDate is missing
                        if (outMap.keySet().contains("eventDate")){
                            if (key.contains("decimalLatitude")) outMap.put("decimalLatitude", dbo.get(key).toString());
                            else if (key.contains("decimalLongitude")) outMap.put("decimalLongitude", dbo.get(key).toString());
                        }
                    }
                }
                resultSet.add(outMap);
            } while (cursor.hasNext());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (cursor != null) cursor.close();
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
                double travelDistancePerDay = GEOUtil.getDistance(Long.parseLong(latitude), Long.parseLong(longitude), refLatitude, refLongitude)/dayDifference;
                if(travelDistancePerDay>=travelDistanceThreshold){
                    isOutlier = true;
                    break;
                }
            }
        }
        if(isOutlier){
            curationStatus = CurationComment.UNABLE_CURATED;
            comment = "This record is a spatial outlier since the location is far away from locations of reference records within " + temporalDistanceThreshold + " days of " + eventDate.toString(format);
        }else{
            curationStatus = CurationComment.CORRECT;
            comment = "This record is a spatial outlier since the location is far away from locations of reference records within " + temporalDistanceThreshold + " days of " + eventDate.toString(format);
            //todo: add other two status here
        }
    }

	private File cacheFile = null;

    private String _mongodbHost = "fp1.acis.ufl.edu";
    private String _mongodbDB = "db";
    private String _mongodbCollection = "Occurrence";
    //private String _mongodbQuery = "{year:\"1898\"}";
    private DBCursor cursor = null;
    private int totalRecords = 0;

    private final int temporalDistanceThreshold = 7; //in day
    private final int travelDistanceThreshold = 1000; //in km/day
	private CurationStatus curationStatus;
	private String comment = "";
    private String correctEventDate;
	
	private HashMap<String,Vector<String>> authoritativeFloweringTimeMap = null; 
	private static final String ColumnDelimiterInCacheFile = "\t";
	
	private final String serviceName = "Harvard List of Botanists";
}
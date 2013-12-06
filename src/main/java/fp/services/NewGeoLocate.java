package fp.services;

import com.vividsolutions.jts.awt.PolygonShape;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Point;
import fp.util.CurationComment;
import fp.util.CurationStatus;
import fp.util.CurrationException;
import fp.util.GEOUtil;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.filter.Filter;

import java.awt.*;
import java.awt.geom.Path2D;
import java.io.*;
import java.util.*;
import java.util.List;

public class NewGeoLocate implements IGeoRefValidationService {

    private boolean useCache;

    public void setCacheFile(String file) throws CurrationException {
		initializeCacheFile(file);
		importFromCache();
        this.useCache = true;
	}

	/*
	 * If latitude or longit0ude is null, it means such information is missing in the original records
	 *
	 * @see org.kepler.actor.SpecimenQC.IGeoRefValidationService#validateGeoRef(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void validateGeoRef(String country, String stateProvince, String county, String locality, String latitude, String longitude, double certainty){
		curationStatus = CurationComment.UNABLE_CURATED;
		correctedLatitude = -1;
		correctedLongitude = -1;
		comment = "";

		try {
			String key = constructCachedMapKey(country,stateProvince,county,locality);
			double foundLat;
			double foundLng;
			if(useCache && cachedCoordinates.containsKey(key)){
				String[] coordinates = cachedCoordinates.get(key).split(";");
				foundLat = Double.valueOf(coordinates[0]);
				foundLng = Double.valueOf(coordinates[1]);
			}else{
				Vector<Double> coordinatesInfo = queryGeoLocate(country, stateProvince, county, locality);

				if(coordinatesInfo == null || coordinatesInfo.size()<2){
					curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
					comment = "Can't find coordiantes by searching for locaility in GeoLocate service.";
					return;
				}else{
					foundLat = coordinatesInfo.get(0);
					foundLng = coordinatesInfo.get(1);
				}

				//keep the information which will be written into cache file later
				if(useCache){
					cachedCoordinates.put(key,String.valueOf(foundLat)+";"+String.valueOf(foundLng));
					newFoundCoordinates.add(country);
					newFoundCoordinates.add(stateProvince);
					newFoundCoordinates.add(county);
					newFoundCoordinates.add(locality);
					newFoundCoordinates.add(String.valueOf(foundLat));
					newFoundCoordinates.add(String.valueOf(foundLng));
				}
			}

			if(latitude == null || longitude == null){
				//The coordinates in the original records is missing
				curationStatus = CurationComment.Filled_in;
				correctedLatitude = foundLat;
				correctedLongitude = foundLng;
				comment = "Insert the coordinates by using cached data or "+getServiceName()+"service since the original coordinates are missing.";
			}else{
				//calculate the distance from the returned point and original point in the record
				//If the distance is smaller than a certainty, then use the original point --- GEOService, like GeoLocate can't parse detailed locality. In this case, the original point has higher confidence
				//Otherwise, use the point returned from GeoLocate
				double originalLat = Double.valueOf(latitude);
				double originalLng = Double.valueOf(longitude);
                //System.out.println("originalLng = " + originalLng);
                //System.out.println("originalLat = " + originalLat);

                //First, domain check, if wrong, switch
                if (originalLat > 90 || originalLat < -90) {
                    if (originalLng < 90 || originalLng > -90) {
                        double temp=originalLat;
                        originalLat = originalLng;
                        originalLng=temp;
                        curationStatus = CurationComment.CURATED;
                        comment = comment + "The original latitude is out of range. Transposing longitude and latitude. ";
                    }
                    else {
                        if (originalLng > 180 || originalLng < -180){
                            curationStatus = CurationComment.UNABLE_CURATED;
                            comment = "Both original latitude \""+ originalLat + "\" and longitude \"" + originalLng + "\" are out of range. ";
                            return;
                        }
                        else {
                            curationStatus = CurationComment.UNABLE_CURATED;
                            comment = "The original latitude \"" + originalLat + "\" is out of range. ";
                            return;
                        }
                    }
                }
                else{
                    if (originalLng > 180 || originalLng < -180){
                        curationStatus = CurationComment.UNABLE_CURATED;
                        comment = "The original longitude \"" + originalLng + "\" is out of range. ";
                        return;
                    }
                }


                //System.out.println("down to second");
                //Second, check whether it's on land

                boolean originalInPolygon = checkIfOnLand(originalLng, originalLat);

                //If not in polygon, try some sign changing/swapping
                if (!originalInPolygon){
                    //sign changing
                    originalLng = 0 - originalLng;
                    boolean swapInPolygon = checkIfOnLand(originalLng, originalLat);
                    if (!swapInPolygon){
                        originalLat = 0 - originalLat;
                        swapInPolygon = checkIfOnLand(originalLng, originalLat);
                    }
                    if (!swapInPolygon){
                        originalLng = 0 - originalLng;
                        swapInPolygon = checkIfOnLand(originalLng, originalLat);
                    }

                    //if it's still not in land, swap lat and lng and do the sign changing again
                    if (!swapInPolygon && (originalLat < 90 && originalLat > -90) && (originalLat < 90 && originalLat > -90) ){
                        double temp2=originalLat;
                        originalLat = originalLng;
                        originalLng=temp2;

                        originalLng = 0 - originalLng;
                        swapInPolygon = checkIfOnLand(originalLng, originalLat);
                        if (!swapInPolygon){
                            originalLat = 0 - originalLat;
                            swapInPolygon = checkIfOnLand(originalLng, originalLat);
                        }
                        if (!swapInPolygon){
                            originalLng = 0 - originalLng;
                            swapInPolygon = checkIfOnLand(originalLng, originalLat);
                        }
                    }

                    //check the result
                    if (swapInPolygon){
                        curationStatus = CurationComment.CURATED;
                        comment = comment + "sign changed coordinates are on the Earth's surface. ";
                        serviceName = serviceName + "Land data from Natural Earth";
                    }
                    else{
                        curationStatus = CurationComment.UNABLE_CURATED;
                        comment = "Can't transpose/sign change coordinates to place them on the Earth's surface.";
                        return;
                    }
                }

                //System.out.println("down to third");
                //Third, check whether it's in the country
                HashMap<String, Set<Path2D>> boundaries;
                try
                {
                    // FileInputStream fileIn = new FileInputStream("/home/tianhong/Downloads/political/country_boundary.ser");
                    //FileInputStream fileIn = new FileInputStream("/etc/filteredpush/descriptors/country_boundary.ser");
                    FileInputStream fileIn = new FileInputStream("/Users/cobalt/Projects/FPServices/country_boundary.ser");
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    boundaries = (HashMap) in.readObject();
                    in.close();
                    fileIn.close();
                    //System.out.println("read boundary data");
                }catch(IOException i)
                {
                    i.printStackTrace();
                    return;
                }catch(ClassNotFoundException c)
                {
                    System.out.println("boundaries data not found");
                    c.printStackTrace();
                    return;
                }

                //standardize country names
                //country = countryNormalization(country);

                if (country.equals("USA")){
                    country = "UNITED STATES";
                }else if (country.equals("United States of America"))    {
                    country = "UNITED STATES";
                }
                else {
                    country = country.toUpperCase();
                    //System.out.println("not in !##"+country+"##");
                }



                Set<Path2D> boundary = boundaries.get(country);
                boolean originalInBoundary = testInPolygon(boundary, originalLng, originalLat);
                //If not in polygon, try some swapping
                if (!originalInBoundary){
                    comment = comment + "Coordinates not inside country. ";
                    originalLng = 0 - originalLng;
                    boolean swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                    if (!swapInBoundary){
                        originalLat = 0 - originalLat;
                        swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                    }
                    if (!swapInBoundary){
                        originalLng = 0 - originalLng;
                        swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                    }

                    //if it's still not in country, swap lat and lng and do the sign changing again
                    if (!swapInBoundary && (originalLat < 90 && originalLat > -90) && (originalLat < 90 && originalLat > -90) ){
                        double temp3=originalLat;
                        originalLat = originalLng;
                        originalLng=temp3;

                        originalLng = 0 - originalLng;
                        swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                        if (!swapInBoundary){
                            originalLat = 0 - originalLat;
                            swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                        }
                        if (!swapInBoundary){
                            originalLng = 0 - originalLng;
                            swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                        }
                    }

                    if (swapInBoundary){
                        curationStatus = CurationComment.CURATED;
                        comment = comment + "Transposed/sign changed coordinates to place inside country.";
                        serviceName = serviceName + "Country boundary data from GeoCommunity";
                    }
                    else {
                        curationStatus = CurationComment.UNABLE_CURATED;
                        comment = comment + "Can't transpose/sign change coordinates to place inside country. ";
                        return;
                    }
                }

                //System.out.println("curationStatus = " + curationStatus);
                //System.out.println("comment = " + comment);

                //System.out.println("originalLng = " + originalLng);
                //System.out.println("originalLat = " + originalLat);
                //System.out.println("country = " + country);
                //System.out.println("foundLng = " + foundLng);
                //System.out.println("foundLat = " + foundLat);

                //finally, check whether it's close to GeoLocate referecne or not
                double distance = GEOUtil.getDistance(foundLat, foundLng, originalLat, originalLng);
                if(distance>Double.valueOf(certainty)){
                    //use the found coordinates
                    curationStatus = CurationComment.UNABLE_CURATED;
                    comment = comment+ "Coordinates are not near georeference of locality from geolocate with certainty: " + certainty;
                }else{
                    //use the original coordinates
                    if (curationStatus == CurationComment.CURATED){
                        comment = comment + "Transposed/sign changed coordinates are near georeference of locality from Geolocate.";
                        correctedLatitude = originalLat;
                        correctedLongitude = originalLng;
                    }
                    else {
                        curationStatus = CurationComment.CORRECT;
                        correctedLatitude = originalLat;
                        correctedLongitude = originalLng;
                        comment = comment + "Original coordinates are near georeference of locality from Geolocate.";
                    }
                }
			}
        } catch (CurrationException e) {
			curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
			comment = e.getMessage();
			return;
		} catch (CQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

//	public boolean isCoordinatesFound(){
//		return isCoordinatesFound;
//	}

	public double getCorrectedLatitude() {
		return correctedLatitude;
	}

	public double getCorrectedLongitude() {
		return correctedLongitude;
	}

	public String getComment(){
		return comment;
	}

	public CurationStatus getCurationStatus() {
		return curationStatus;
	}

	public void flushCacheFile() throws CurrationException {
		if(cacheFile == null){
			return;
		}

		try {
			//output the newly found coordinates into the cached file
			if(newFoundCoordinates.size()>0){
				BufferedWriter writer  = new BufferedWriter(new FileWriter(cacheFile,true));
				for(int i=0;i<newFoundCoordinates.size();i=i+6){
					String strLine = "";
					for(int j=i;j<i+6;j++){
						strLine = strLine + "\t" + newFoundCoordinates.get(j);
					}
					strLine = strLine.trim();
					writer.write(strLine+"\n");
				}
				writer.close();
			}
		} catch (IOException e) {
			throw new CurrationException(getClass().getName()+" failed to write newly found coordinates into cache file "+cacheFile.toString()+" since "+e.getMessage());
		}
	}

    @Override
    public List<List> getLog() {
        return null;
    }

    @Override
    public void setUseCache(boolean use) {
        this.useCache = use;
        cachedCoordinates = new HashMap<String,String>();
        newFoundCoordinates = new Vector<String>();
    }

    public String getServiceName(){
		return serviceName;
	}

	private void initializeCacheFile(String fileStr) throws CurrationException{
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
		cachedCoordinates = new HashMap<String,String>();
		newFoundCoordinates = new Vector<String>();

		//read
		try {
			BufferedReader cachedFileReader = new BufferedReader(new FileReader(cacheFile));
			String strLine = cachedFileReader.readLine();
			while(strLine!=null){
				String[] info = strLine.split(ColumnDelimiterInCacheFile);
				if(info.length != 6){
					throw new CurrationException(getClass().getName()+" failed to import data from cached file since some information is missing at: "+strLine);
				}

				String country = info[0];
				String state = info[1];
				String county = info[2];
				String locality = info[3];
				String lat = info[4];
				String lng = info[5];

				String key = constructCachedMapKey(country,state,county,locality);
				String coordinate = lat+";"+lng;

				cachedCoordinates.put(key, coordinate);

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

	private String constructCachedMapKey(String country, String state, String county, String locality){
		return country+" "+state+" "+county+" "+locality;
	}

    /*private String countryNormalization(String country){
        AbstractDataProcessor countryProcessor = new CountryProcessor();
        MockOccurrenceModel mockRawModel = new MockOccurrenceModel();
        MockOccurrenceModel mockModel = new MockOccurrenceModel();

        mockRawModel.setCountry(country);
        ProcessingResult pr = new ProcessingResult();
        countryProcessor.processBean(mockRawModel, mockModel, null, pr);
        return mockModel.getCountry();
    }
    */

    private boolean testInPolygon (Set<Path2D> polygonSet, double Xvalue, double Yvalue){
        //System.out.println("Xvalue = " + Xvalue);
        //System.out.println("Yvalue = " + Yvalue);
        Iterator it = polygonSet.iterator();
        while(it.hasNext()){
            Path2D poly=(Path2D)it.next();
            if (poly.contains(Xvalue, Yvalue)) {
                //System.out.println("Found in polygon");
                return true;
            }
        }
        return false;
    }

    private boolean checkIfOnLand(double longitude, double latitude) throws IOException, CQLException {

        //File file = new File("/etc/filteredpush/descriptors/ne_10m_land.shp");
        File file = new File("/Users/cobalt/Projects/FPServices/ne_10m_land.shp");

        Map map = new HashMap();
        map.put( "url", file.toURL() );
        DataStore dataStore = DataStoreFinder.getDataStore(map);
        String typeName = dataStore.getTypeNames()[0];

        FeatureSource source = dataStore.getFeatureSource(typeName);
        //Filter filter = CQL.toFilter("BBOX(THE_GEOM, 10,20,30,40)");
        //FeatureCollection collection = source.getFeatures(filter);
        FeatureCollection collection = source.getFeatures();
        FeatureIterator iterator = collection.features();
        boolean test = false;
        try {
            while( iterator.hasNext() ){
                Feature feature = iterator.next();
                GeometryAttribute p1 = (GeometryAttribute)feature.getProperty("the_geom");
                Object o = p1.getValue();
                MultiPolygon m = (MultiPolygon) o;
                test = m.contains(m.getFactory().createPoint(new Coordinate(longitude,latitude)));
                System.out.println(test);
            }
        }
        finally {
           iterator.close();
        }
        return test;
    }

	private Vector<Double> queryGeoLocate(String country, String stateProvience, String county, String locality) throws CurrationException{
		org.apache.http.client.HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
        httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);

        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("Country", country));
        parameters.add(new BasicNameValuePair("State", stateProvience));
        parameters.add(new BasicNameValuePair("County", county));
        parameters.add(new BasicNameValuePair("LocalityString", locality));
        parameters.add(new BasicNameValuePair("FindWaterbody", "False"));
        parameters.add(new BasicNameValuePair("HwyX", "False"));

        //System.out.println("*************country = " + country + stateProvience + county + locality);

        try {
            HttpResponse resp;
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));

            resp = httpclient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() != 200) {
				throw new CurrationException("GeoLocateService failed to send request to Geolocate for "+resp.getStatusLine().getStatusCode());
			}
            InputStream reponseStream = resp.getEntity().getContent();

			//parse the response
		    SAXReader reader = new SAXReader();

		    HashMap<String,String> map = new HashMap<String,String>();
		    map.put( "geo", defaultNameSpace);
		    reader.getDocumentFactory().setXPathNamespaceURIs(map);

		    Document document = reader.read(reponseStream);
		    Node latitudeNode = document.selectSingleNode("/geo:Georef_Result_Set/geo:ResultSet[1]/geo:WGS84Coordinate/geo:Latitude");
		    Node longitudeNode = document.selectSingleNode("/geo:Georef_Result_Set/geo:ResultSet[1]/geo:WGS84Coordinate/geo:Longitude");

		    if(latitudeNode == null || longitudeNode == null){
		    	//can't find the coordinates in the first result set which has the highest confidence
		    	return null;
		    }

		    Vector<Double> coordinatesInfo = new Vector<Double>();
		    coordinatesInfo.add(Double.valueOf(latitudeNode.getText()));
		    coordinatesInfo.add(Double.valueOf(longitudeNode.getText()));
		    return coordinatesInfo;
		} catch (IOException e) {
			throw new CurrationException("GeoLocateService failed to access GeoLocate service for "+e.getMessage());
		} catch (DocumentException e) {
			throw new CurrationException("GeoLocateService failed to get the coordinates information by parsing the response from GeoLocate service at: "+url+" for: "+e.getMessage());
		}
	}

    public static void main(String args[]) {
        new NewGeoLocate().validateGeoRef("USA","CA","Yolo","Davis","41.0","-77.0",200.0);
    }

	private File cacheFile = null;

	private CurationStatus curationStatus;
	private double correctedLatitude;
	private double correctedLongitude;
	private String comment = "";
//	private boolean isCoordinatesFound;


	private HashMap<String, String> cachedCoordinates;
	private Vector<String> newFoundCoordinates;
	private static final String ColumnDelimiterInCacheFile = "\t";

	private String serviceName = "GEOLocate";

	private final String url = "http://www.museum.tulane.edu/webservices/geolocatesvc/geolocatesvc.asmx/Georef2";
	private final String defaultNameSpace = "http://www.museum.tulane.edu/webservices/";

}


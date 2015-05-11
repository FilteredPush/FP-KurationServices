package org.filteredpush.kuration.services;

import org.apache.http.client.ClientProtocolException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.filteredpush.kuration.interfaces.IGeoRefValidationService;
import org.filteredpush.kuration.util.*;
import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader;
import org.nocrala.tools.gis.data.esri.shapefile.ValidationPreferences;
import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;
import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.PointData;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonShape;

import java.awt.geom.Path2D;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class GeoLocate3 implements IGeoRefValidationService {

    private boolean useCache = true;
    private Cache cache;
	
	/*
	 * If latitude or longitude is null, it means such information is missing in the original records
	 * 
	 * @see org.kepler.actor.SpecimenQC.IGeoRefValidationService#validateGeoRef(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void validateGeoRef(String country, String stateProvince, String county, String locality, String latitude, String longitude, double certainty){
		curationStatus = CurationComment.UNABLE_CURATED;
		correctedLatitude = -1;
		correctedLongitude = -1;
		comment = "";
        serviceName = "decimalLatitude:" + latitude + "#decimalLongitude:" + longitude + "#";
        log = new LinkedList<List>();

        //first search for reference coordinates
        double foundLat;
        double foundLng;
        String key = country+" "+stateProvince+" "+county+" "+locality;
        if (useCache && coordinatesCache.containsKey(key)){
            GeoRefCacheValue cachedValue = (GeoRefCacheValue) coordinatesCache.get(key);
            foundLat = cachedValue.getLat();
            foundLng = cachedValue.getLng();
            //System.out.println("geocount = " + count++);
            //System.out.println("key = " + key);
        } else {

            Vector<Double> coordinatesInfo = null;
            try {
                coordinatesInfo = queryGeoLocate(country, stateProvince, county, locality);
            } catch (CurationException e) {
                curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
                comment = comment + " | " + e.getMessage();
                return;
            }

            if(coordinatesInfo == null || coordinatesInfo.size()<2){
                //todo: very ad-hoc way for now, need to handle all the combinations of the two missings
                if(latitude == null || longitude == null){
                    comment = comment + " | The original coordinates are missing.";
                }
                curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
                comment = " | Can't find coordinates by searching for locality in GeoLocate service.";
                return;
            }else{
                foundLat = coordinatesInfo.get(0);
                foundLng = coordinatesInfo.get(1);
            }

            if(useCache) addNewToCache(foundLat, foundLng, country, stateProvince, county, locality);
        }
				

        //start validation
        if(latitude == null || longitude == null){
            //The coordinates in the original records is missing
            curationStatus = CurationComment.CURATED;
            correctedLatitude = foundLat;
            correctedLongitude = foundLng;
            comment = comment + " | Insert the coordinates by using cached data or "+getServiceName()+"service since the original coordinates are missing.";
        }else {
            //calculate the distance from the returned point and original point in the record
            //If the distance is smaller than a certainty, then use the original point --- GEOService, like GeoLocate can't parse detailed locality. In this case, the original point has higher confidence
            //Otherwise, use the point returned from GeoLocate

            double originalLat = Double.valueOf(latitude);
            double originalLng = Double.valueOf(longitude);

            //zero, check if it's close to GeoLocate referecne
            double distance = GEOUtil.getDistance(foundLat, foundLng, originalLat, originalLng);
            if (distance < Double.valueOf(certainty)) {
                curationStatus = CurationComment.CORRECT;
                correctedLatitude = originalLat;
                correctedLongitude = originalLng;
                comment = comment + " | Original coordinates are near georeference of locality from Geolocate with certainty: " + certainty;
                return;
            }

            //First, domain check, if wrong, switch
            if (originalLat > 90 || originalLat < -90) {
                if (originalLng < 90 || originalLng > -90) {
                    double temp = originalLat;
                    originalLat = originalLng;
                    originalLng = temp;
                    curationStatus = CurationComment.CURATED;
                    comment = comment + " | The original latitude is out of range. Transposing longitude and latitude. ";
                } else {
                    if (originalLng > 180 || originalLng < -180) {
                        curationStatus = CurationComment.UNABLE_CURATED;
                        comment = comment + " | Both original latitude \"" + originalLat + "\" and longitude \"" + originalLng + "\" are out of range. ";
                        return;
                    } else {
                        curationStatus = CurationComment.UNABLE_CURATED;
                        comment = comment + " | The original latitude \"" + originalLat + "\" is out of range. ";
                        return;
                    }
                }
            } else {
                if (originalLng > 180 || originalLng < -180) {
                    curationStatus = CurationComment.UNABLE_CURATED;
                    comment = " | The original longitude \"" + originalLng + "\" is out of range. ";
                    return;
                }
            }


            //System.out.println("down to second");
            //Second, check whether it's on land
            Set<Path2D> setPolygon = null;
            try {
                setPolygon = ReadLandData();
                //System.out.println("read data");
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InvalidShapeFileException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            boolean originalInPolygon = testInPolygon(setPolygon, originalLng, originalLat);
            //If not in polygon, try some sign changing/swapping
            if (!originalInPolygon) {
                //sign changing
                originalLng = 0 - originalLng;
                boolean swapInPolygon = testInPolygon(setPolygon, originalLng, originalLat);
                if (!swapInPolygon) {
                    originalLat = 0 - originalLat;
                    swapInPolygon = testInPolygon(setPolygon, originalLng, originalLat);
                }
                if (!swapInPolygon) {
                    originalLng = 0 - originalLng;
                    swapInPolygon = testInPolygon(setPolygon, originalLng, originalLat);
                }

                //if it's still not in land, swap lat and lng and do the sign changing again
                if (!swapInPolygon && (originalLat < 90 && originalLat > -90) && (originalLat < 90 && originalLat > -90)) {
                    double temp2 = originalLat;
                    originalLat = originalLng;
                    originalLng = temp2;

                    originalLng = 0 - originalLng;
                    swapInPolygon = testInPolygon(setPolygon, originalLng, originalLat);
                    if (!swapInPolygon) {
                        originalLat = 0 - originalLat;
                        swapInPolygon = testInPolygon(setPolygon, originalLng, originalLat);
                    }
                    if (!swapInPolygon) {
                        originalLng = 0 - originalLng;
                        swapInPolygon = testInPolygon(setPolygon, originalLng, originalLat);
                    }
                }

                //check the result
                if (swapInPolygon) {
                    curationStatus = CurationComment.CURATED;
                    comment = comment + " | sign changed coordinates are on the Earth's surface. ";
                    serviceName = serviceName + " | Land data from Natural Earth";
                } else {
                    curationStatus = CurationComment.UNABLE_CURATED;
                    comment = " | Can't transpose/sign change coordinates to place them on the Earth's surface.";
                    return;
                }
            }

            //System.out.println("down to third");
            //Third, check whether it's in the country
            HashMap<String, Set<Path2D>> boundaries = null;
            try {

                InputStream fileIn = GeoLocate3.class.getResourceAsStream("/org.filteredpush.kuration.services/country_boundary.ser");
                //FileInputStream fileIn = new FileInputStream("/etc/filteredpush/descriptors/country_boundary.ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                boundaries = (HashMap) in.readObject();
                in.close();
                fileIn.close();
                //System.out.println("read boundary data");
            } catch (IOException i) {
                i.printStackTrace();
            } catch (ClassNotFoundException c) {
                System.out.println("boundaries data not found");
                c.printStackTrace();
            }

            //standardize country names
            //country = countryNormalization(country);
            if (country != null && boundaries != null) {
                serviceName = serviceName + " | Country boundary data from GeoCommunity";
                if (country.toUpperCase().equals("USA")) {
                    country = "UNITED STATES";
                } else if (country.equals("United States of America")) {
                    country = "UNITED STATES";
                } else {
                    country = country.toUpperCase();
                    //System.out.println("not in !##"+country+"##");
                }

                Set<Path2D> boundary = boundaries.get(country);
                if (boundary == null) {
                    curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
                    comment = " | Can't find country: " + country + " in country name list";

                } else {
                    boolean originalInBoundary = testInPolygon(boundary, originalLng, originalLat);
                    //If not in polygon, try some swapping
                    if (!originalInBoundary) {
                        comment = comment + " | Coordinates not inside country. ";
                        originalLng = 0 - originalLng;
                        boolean swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                        if (!swapInBoundary) {
                            originalLat = 0 - originalLat;
                            swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                        }
                        if (!swapInBoundary) {
                            originalLng = 0 - originalLng;
                            swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                        }

                        //if it's still not in country, swap lat and lng and do the sign changing again
                        if (!swapInBoundary && (originalLat < 90 && originalLat > -90) && (originalLat < 90 && originalLat > -90)) {
                            double temp3 = originalLat;
                            originalLat = originalLng;
                            originalLng = temp3;

                            originalLng = 0 - originalLng;
                            swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                            if (!swapInBoundary) {
                                originalLat = 0 - originalLat;
                                swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                            }
                            if (!swapInBoundary) {
                                originalLng = 0 - originalLng;
                                swapInBoundary = testInPolygon(boundary, originalLng, originalLat);
                            }
                        }

                        if (swapInBoundary) {
                            curationStatus = CurationComment.CURATED;
                            comment = comment + " | transposed/sign changed coordinates to place inside country.";
                        } else {
                            curationStatus = CurationComment.UNABLE_CURATED;
                            comment = comment + " | Can't transpose/sign change coordinates to place inside country. ";
                            return;
                        }
                    }
                }
            } else {
                comment = comment + " | country name is empty";
            }
            //System.out.println("curationStatus = " + curationStatus);
            //System.out.println("comment = " + comment);

            //System.out.println("originalLng = " + originalLng);
            //System.out.println("originalLat = " + originalLat);
            //System.out.println("country = " + country);
            //System.out.println("foundLng = " + foundLng);
            //System.out.println("foundLat = " + foundLat);


            distance = GEOUtil.getDistance(foundLat, foundLng, originalLat, originalLng);
            if (distance > Double.valueOf(certainty)) {
                //use the found coordinates
                curationStatus = CurationComment.UNABLE_CURATED;
                comment = comment + " | Coordinates are not near georeference of locality from geolocate with certainty: " + certainty;
            } else {
                //use the original coordinates
                if (curationStatus == CurationComment.CURATED) {
                    comment = comment + " | Transposed/sign changed coordinates are near georeference of locality from Geolocate.";
                    correctedLatitude = originalLat;
                    correctedLongitude = originalLng;
                } else {
                    System.out.println("wrong status in GeoLocate3: no change but near");
                    System.out.println("debugging = " + serviceName);
                }


            }

        }
	}


    public void addNewToCache(double Lat, double Lng, String country, String stateProvince, String county, String locality) {
        String key = constructCachedMapKey(country, stateProvince, county, locality);
        if(!coordinatesCache.containsKey(key)){
            CacheValue newValue = new GeoRefCacheValue(Lat, Lng);
            coordinatesCache.put(key, newValue);
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

    @Override
    public List<List> getLog() {
        return log;
    }

    private String constructCachedMapKey(String country, String state, String county, String locality){
        return country+" "+state+" "+county+" "+locality;
    }


	public void flushCacheFile() throws CurationException {
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
			throw new CurationException(getClass().getName()+" failed to write newly found coordinates into cache file "+cacheFile.toString()+" since "+e.getMessage());
		}		
	}

    public String getServiceName(){
        return serviceName;
    }

    public void setUseCache(boolean use) {
        //old interface

    }

    public void setCacheFile(String file) {

    }
    /*  switch off old cache machanism based on files

    public void setUseCache(boolean use) {
        this.useCache = use;
        cachedCoordinates = new HashMap<String,String>();
        newFoundCoordinates = new Vector<String>();
        if (use) {
            cache = new GeoRefDBCache();
        }
    }

    public void setCacheFile(String file) throws CurationException {
		initializeCacheFile(file);
		importFromCache();
        this.useCache = true;
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
	
	private void importFromCache() throws CurationException{
		cachedCoordinates = new HashMap<String,String>();
		newFoundCoordinates = new Vector<String>();
		
		//read
		try {
			BufferedReader cachedFileReader = new BufferedReader(new FileReader(cacheFile));
			String strLine = cachedFileReader.readLine();
			while(strLine!=null){
				String[] info = strLine.split(ColumnDelimiterInCacheFile);
				if(info.length != 6){
					throw new CurationException(getClass().getName()+" failed to import data from cached file since some information is missing at: "+strLine);
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
			throw new CurationException(getClass().getName()+" failed to import data from cached file for "+e.getMessage());
		} catch (IOException e) {
			throw new CurationException(getClass().getName()+" failed to import data from cached file for "+e.getMessage());
		}
	}
	


    private double [] searchCache(String country, String stateProvince, String county,String locality) {
        String key = country + " " + stateProvince + " " + county + " " + locality;
        double foundLat;
        double foundLng;
        if (cachedCoordinates.containsKey(key)) {
            String[] coordinates = cachedCoordinates.get(key).split(";");
            foundLat = Double.valueOf(coordinates[0]);
            foundLng = Double.valueOf(coordinates[1]);
            return new double[]{foundLat, foundLng};
        } else return null;
    }


    private void addToCache(String key, double latitude, double longitude, String country, String stateProvince, String county,String locality){
        //keep the information which will be written into cache file later
        cachedCoordinates.put(key,String.valueOf(latitude)+";"+String.valueOf(longitude));
        newFoundCoordinates.add(country);
        newFoundCoordinates.add(stateProvince);
        newFoundCoordinates.add(county);
        newFoundCoordinates.add(locality);
        newFoundCoordinates.add(String.valueOf(latitude));
        newFoundCoordinates.add(String.valueOf(longitude));
    }
    */

    private boolean testInPolygon (Set<Path2D> polygonSet, double Xvalue, double Yvalue){
        //System.out.println("Xvalue = " + Xvalue);
        //System.out.println("Yvalue = " + Yvalue);
        Boolean foundInPolygon = false;
        Iterator it = polygonSet.iterator();
        while(it.hasNext()){
            Path2D poly=(Path2D)it.next();
            if (poly.contains(Xvalue, Yvalue)) {
                //System.out.println("Found in polygon");
                foundInPolygon = true;
            }
        }
        return foundInPolygon;
    }

    private Set<Path2D> ReadLandData () throws IOException, InvalidShapeFileException {

        InputStream is = GeoLocate3.class.getResourceAsStream("/org.filteredpush.kuration.services/ne_10m_land.shp");
        //FileInputStream is = null;
        //is = new FileInputStream("/etc/filteredpush/descriptors/ne_10m_land.shp");

        ValidationPreferences prefs = new ValidationPreferences();
        prefs.setMaxNumberOfPointsPerShape(420000);
        ShapeFileReader reader = null;
        reader = new ShapeFileReader(is, prefs);

        Set<Path2D> polygonSet = new HashSet<Path2D>();

        AbstractShape shape;
        while ((shape = reader.next()) != null) {

            PolygonShape aPolygon = (PolygonShape) shape;

            //System.out.println("content: " + aPolygon.toString());
            //System.out.println("I read a Polygon with "
            //    + aPolygon.getNumberOfParts() + " parts and "
            //    + aPolygon.getNumberOfPoints() + " points. "
            //     + aPolygon.getShapeType());

            for (int i = 0; i < aPolygon.getNumberOfParts(); i++) {
                PointData[] points = aPolygon.getPointsOfPart(i);
                //System.out.println("- part " + i + " has " + points.length + " points");

                Path2D polygon = new Path2D.Double();
                for (int j = 0; j < points.length; j++) {
                    if (j==0) polygon.moveTo(points[j].getX(), points[j].getY());
                    else polygon.lineTo(points[j].getX(), points[j].getY());
                    //System.out.println("- point " + i + " has " + points[j].getX() + " and " + points[j].getY());
                }
                polygonSet.add(polygon);
            }
        }
        is.close();
        return polygonSet;
    }

	private Vector<Double> queryGeoLocate(String country, String stateProvince, String county, String locality) throws CurationException {

        serviceName += " | GeoLocate";
        Reader stream = null;
        Document document = null;
        long starttime = System.currentTimeMillis();

        if(country == null) comment = comment +  " | country is missing in the orignial record";
        if(stateProvince == null) comment = comment +  " | stateProvince is missing in the orignial record";
        if(county == null) comment = comment +  " | county is missing in the orignial record";
        if(locality == null) comment = comment +  " | locality is missing in the orignial record";

        List<String> skey = new ArrayList<String>(5);
        skey.add(country);
        skey.add(stateProvince);
        skey.add(county);
        skey.add(locality);
        if (useCache && cache != null && cache.lookup(skey) != null) {
            String x = cache.lookup(skey);
            stream = new StringReader(x);
        } else {
            try{
                //temp switch to plain url

                String urlString = url +  "country=" + country + "&state=" + stateProvince + "&county=" + county +
                         "&LocalityString=" + locality + "&FindWaterbody=False&HwyX=False";
                //URL url2 = new URL("http://www.museum.tulane.edu/webservices/geolocatesvc/geolocatesvc.asmx/Georef2?country=USA&state=california&county=yolo&LocalityString=%22I80%22&hwyx=false&FindWaterbody=false");
                urlString = urlString.replace(" ", "%20");
                //System.out.println("urlString = " + urlString);
                URL url2 = new URL(urlString);

                URLConnection connection = url2.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while (br.ready()) {
                    sb.append(br.readLine());
                }
                stream = new StringReader(sb.toString());
                //httpPost.releaseConnection();

                if (useCache && cache != null) {
                    skey.add(sb.toString());
                    cache.insert(skey);
                }
            } catch (ClientProtocolException e) {
                throw new CurationException("GeoLocateService failed to access GeoLocate service for A "+e.getMessage());
            } catch (UnsupportedEncodingException e) {
                throw new CurationException("GeoLocateService failed to access GeoLocate service for B "+e.getMessage());
            } catch (IOException e) {
                throw new CurationException("GeoLocateService failed to access GeoLocate service for C "+e.getMessage());
            }
        }

        SAXReader reader = new SAXReader();
        HashMap<String,String> map = new HashMap<String,String>();
        map.put( "geo", defaultNameSpace);
        reader.getDocumentFactory().setXPathNamespaceURIs(map);
        try {
            document = reader.read(stream);
        } catch (DocumentException e) {
            throw new CurationException("GeoLocateService failed to get the coordinates information by parsing the response from GeoLocate service at: "+url+" for: "+e.getMessage());
        }

        Node latitudeNode = document.selectSingleNode("/geo:Georef_Result_Set/geo:ResultSet[1]/geo:WGS84Coordinate/geo:Latitude");
        Node longitudeNode = document.selectSingleNode("/geo:Georef_Result_Set/geo:ResultSet[1]/geo:WGS84Coordinate/geo:Longitude");
		    
        if(latitudeNode == null || longitudeNode == null){
            //can't find the coordinates in the first result set which has the highest confidence
            List l = new LinkedList();
            l.add(this.getClass().getSimpleName());
            l.add(starttime);
            l.add(System.currentTimeMillis());
            l.add("POST");
            log.add(l);
            return null;
        }

        Vector<Double> coordinatesInfo = new Vector<Double>();
        coordinatesInfo.add(Double.valueOf(latitudeNode.getText()));
        coordinatesInfo.add(Double.valueOf(longitudeNode.getText()));
        List l = new LinkedList();
        l.add(this.getClass().getSimpleName());
        l.add(starttime);
        l.add(System.currentTimeMillis());
        l.add("POST");
        log.add(l);
        return coordinatesInfo;
	}

	
	private File cacheFile = null;

	private CurationStatus curationStatus;
	private double correctedLatitude;
	private double correctedLongitude;
	private String comment = "";	
//	private boolean isCoordinatesFound;
    private List<List> log = new LinkedList<List>();

    static int count = 0;
	private static HashMap<String, CacheValue> coordinatesCache = new HashMap<String, CacheValue>();
	private Vector<String> newFoundCoordinates;
	private static final String ColumnDelimiterInCacheFile = "\t";
	
	private String serviceName = "";
	
	private final String url = "http://www.museum.tulane.edu/webservices/geolocatesvc/geolocatesvc.asmx/Georef2?";
    //private final String url = "http://lore.genomecenter.ucdavis.edu/cache/geolocate.php";
	private final String defaultNameSpace = "http://www.museum.tulane.edu/webservices/";
}
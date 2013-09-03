package fp.services;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;

import fp.util.GEOUtil;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.params.CoreConnectionPNames;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * Created with IntelliJ IDEA.
 * User: cobalt
 * Date: 26.04.2013
 * Time: 15:02
 * To change this template use File | Settings | File Templates.
 */
public class GeoLocate {

    class Coordinates {
        Double longitude;
        Double latitude;

        Coordinates(Double longitude, Double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }

    static HashMap<String,Coordinates> cache;

    public static String hashString(String country, String stateProvince, String county, String locality) {
        StringBuilder sb = new StringBuilder();
        sb.append(country);
        sb.append("#");
        sb.append(stateProvince);
        sb.append("#");
        sb.append(county);
        sb.append("#");
        sb.append(locality);
        return sb.toString();
    }

    public GeoLocate() {
        if (cache == null) {
            cache = new HashMap<String, Coordinates>();
        }
    }

    /*
     * If latitude or longitude is null, it means such information is missing in the original records
     *
     * @see org.kepler.actor.SpecimenQC.IGeoRefValidationService#validateGeoRef(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void validateGeoRef(String country, String stateProvince, String county, String locality, String latitude, String longitude, double certainty) {
        curationStatus = false;
        isCoordinatesFound = false;
        correctedLatitude = -1;
        correctedLongitude = -1;
        comment = "";
        isTimedOut = false;


        Double foundLat = null;
        Double foundLng = null;

        if (country == null || stateProvince == null || county == null || locality == null) {
            curationStatus = false;
            comment = "Not all required fields present to use GeoLocate service.";
            return;
        }


        String hs = hashString(country,stateProvince,county,locality);
        Coordinates coordinatesInfo = cache.get(hs);
        if (coordinatesInfo == null) {
            try {
                coordinatesInfo = queryGeoLocate(country, stateProvince, county, locality);
            } catch (Exception e) {
                curationStatus = false;
                comment = "Unable to determine coordinates for locality using GeoLocate service.";
                return;
            }
            if (coordinatesInfo == null) {
                curationStatus = false;
                comment = "Unable to determine coordinates for locality using GeoLocate service.";
                return;
            } else {
                cache.put(hs,coordinatesInfo);
                foundLat = coordinatesInfo.latitude;
                foundLng = coordinatesInfo.longitude;
            }
        } else {
            foundLat = coordinatesInfo.latitude;
            foundLng = coordinatesInfo.longitude;
            //System.out.println("HIT !!!!");
        }


        isCoordinatesFound = true;

        if (latitude == null || longitude == null) {
            //The coordinates in the original records is missing
            curationStatus = true;
            correctedLatitude = foundLat;
            correctedLongitude = foundLng;
            comment = "Added missing coordinates by using " + getServiceName() + "service.";
        } else {
            //calculate the distance from the returned point and original point in the record
            //If the distance is smaller than a certainty, then use the original point --- GEOService, like GeoLocate can't parse detailed locality. In this case, the original point has higher confidence
            //Otherwise, use the point returned from GeoLocate
            double originalLat = Double.valueOf(latitude);
            double originalLng = Double.valueOf(longitude);
            double distance = GEOUtil.getDistance(foundLat, foundLng, originalLat, originalLng);
            if (distance > Double.valueOf(certainty)) {
                //use the found coordinates
                curationStatus = true;
                correctedLatitude = foundLat;
                correctedLongitude = foundLng;
                comment = "Updated coordinates using " + getServiceName() + "service since they differ from the specified locality.";
            } else {
                //use the original coordinates
                curationStatus = true;
                correctedLatitude = originalLat;
                correctedLongitude = originalLng;
                comment = "Coordinates are correct according to GeoLocate service.";
            }
        }
    }

    public boolean isCoordinatesFound(){
    	return isCoordinatesFound;
    }

    public double getCorrectedLatitude() {
        return correctedLatitude;
    }

    public double getCorrectedLongitude() {
        return correctedLongitude;
    }

    public String getComment() {
        return comment;
    }

    public boolean getCurationStatus() {
        return curationStatus;
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isTimedOut() {
        return isTimedOut;
    }

    public String getQueryString() {
        return queryString;
    }

    private Coordinates queryGeoLocate(String country, String stateProvience, String county, String locality) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
        httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);
        try {
            URIBuilder builder = new URIBuilder();
            builder.setScheme("http").setHost(urlhost).setPath(urlpath)
                    .setParameter("Country", country)
                    .setParameter("State", stateProvience)
                    .setParameter("County", county)
                    .setParameter("LocalityString", locality)
                    .setParameter("FindWaterbody", "False")
                    .setParameter("HwyX", "False");
            URI uri = builder.build();
            queryString = uri.toString();
            HttpGet httpGet = new HttpGet(uri);

            try {
                int retry = 0;
                boolean done = false;
                HttpResponse resp = null;
                do {
                    try {
                        resp = httpclient.execute(httpGet);
                        if (resp.getStatusLine().getStatusCode() != 200) {
                            System.out.println(httpGet.getURI().toString());
                            throw new Exception("GeoLocateService failed to send request to Geolocate for " + resp.getStatusLine().getStatusCode());
                        }
                        done = true;
                    } catch (SocketTimeoutException ste) {
                        if (retry++ >= 0) {
                            isTimedOut = true;
                            throw ste;
                        }
                        //System.out.println("Retry after 20 sec!!!");
                        Thread.sleep(5000);
                    }
                } while (!done);

                InputStream reponseStream = resp.getEntity().getContent();

                //parse the response
                SAXReader reader = new SAXReader();

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("geo", defaultNameSpace);
                reader.getDocumentFactory().setXPathNamespaceURIs(map);

                Document document = reader.read(reponseStream);
                Node latitudeNode = document.selectSingleNode("/geo:Georef_Result_Set/geo:ResultSet[1]/geo:WGS84Coordinate/geo:Latitude");
                Node longitudeNode = document.selectSingleNode("/geo:Georef_Result_Set/geo:ResultSet[1]/geo:WGS84Coordinate/geo:Longitude");

                if (latitudeNode == null || longitudeNode == null) {
                    //can't find the coordinates in the first result set which has the highest confidence
                    return null;
                }

                Coordinates coordinatesInfo = new Coordinates(Double.valueOf(longitudeNode.getText()),Double.valueOf(latitudeNode.getText()));
                return coordinatesInfo;
            } catch (HttpException e) {
                throw new Exception("GeoLocateService failed to access GeoLocate service for " + e.getMessage());
            } catch (IOException e) {
                throw new Exception("GeoLocateService timed out when accessing GeoLocate service: " + e.getMessage());
            } catch (DocumentException e) {
                throw new Exception("GeoLocateService failed to get the coordinates information by parsing the response from GeoLocate service at: " + uri + " for: " + e.getMessage());
            }
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

    public void shutdown() {

    }

    private boolean curationStatus;
    private double correctedLatitude;
    private double correctedLongitude;
    private String comment = "";
    private boolean isCoordinatesFound;
    private boolean isTimedOut;
    private String queryString;

    private Vector<String> newFoundCoordinates;

    private final String serviceName = "GEOLocate";

    private final String urlhost = "www.museum.tulane.edu";
    private final String urlpath = "/webservices/geolocatesvc/geolocatesvc.asmx/Georef2";
    private final String defaultNameSpace = "http://www.museum.tulane.edu/webservices/";
}


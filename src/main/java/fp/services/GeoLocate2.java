package fp.services;

import fp.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.*;

public class GeoLocate2 implements IGeoRefValidationService {

    private boolean useCache;
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
        log = new LinkedList<List>();

		try {
			String key = constructCachedMapKey(country,stateProvince,county,locality);
			double foundLat;
			double foundLng;
			if (useCache && cachedCoordinates.containsKey(key)){
				String[] coordinates = cachedCoordinates.get(key).split(";");
				foundLat = Double.valueOf(coordinates[0]);
				foundLng = Double.valueOf(coordinates[1]);
            } else {
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
				curationStatus = CurationComment.CURATED;				
				correctedLatitude = foundLat;
				correctedLongitude = foundLng;
				comment = "Insert the coordinates by using cached data or "+getServiceName()+"service since the original coordinates are missing.";				
			}else{
				//calculate the distance from the returned point and original point in the record
				//If the distance is smaller than a certainty, then use the original point --- GEOService, like GeoLocate can't parse detailed locality. In this case, the original point has higher confidence
				//Otherwise, use the point returned from GeoLocate
				double originalLat = Double.valueOf(latitude);
				double originalLng = Double.valueOf(longitude);
				double distance = GEOUtil.getDistance(foundLat, foundLng, originalLat, originalLng);
				if(distance>Double.valueOf(certainty)){
					//use the found coordinates
					curationStatus = CurationComment.CURATED;				
					correctedLatitude = foundLat;
					correctedLongitude = foundLng;
					comment = "Update the coordinates by using cached data or "+getServiceName()+"service since the original coordinates are not consistent to the specified localities.";
				}else{
					//use the original coordinates
					curationStatus = CurationComment.CORRECT;
					correctedLatitude = originalLat;
					correctedLongitude = originalLng;				
					comment = "The coordinates is correct by checking with GeoLocate Service.";
				}				
			}					
		} catch (CurrationException e) {
			curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
			comment = e.getMessage();
			return;
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
        return log;
    }

    public void setUseCache(boolean use) {
        this.useCache = use;
        cachedCoordinates = new HashMap<String,String>();
        newFoundCoordinates = new Vector<String>();
        if (use) {
            cache = new GeoRefDBCache();
        }
    }

    public void setCacheFile(String file) throws CurrationException {
		initializeCacheFile(file);
		importFromCache();
        this.useCache = true;
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

	private Vector<Double> queryGeoLocate(String country, String stateProvince, String county, String locality) throws CurrationException {

        Reader stream = null;
        Document document = null;
        long starttime = System.currentTimeMillis();

        List<String> skey = new ArrayList<String>(5);
        skey.add(country);
        skey.add(stateProvince);
        skey.add(county);
        skey.add(locality);
        if (useCache && cache != null && cache.lookup(skey) != null) {
            String x = cache.lookup(skey);
            stream = new StringReader(x);
        } else {
            org.apache.http.client.HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
            httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,30000);

            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("Country", country));
            parameters.add(new BasicNameValuePair("State", stateProvince));
            parameters.add(new BasicNameValuePair("County", county));
            parameters.add(new BasicNameValuePair("LocalityString", locality));
            parameters.add(new BasicNameValuePair("FindWaterbody", "False"));
            parameters.add(new BasicNameValuePair("HwyX", "False"));

            try {
                HttpResponse resp;
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(parameters));

                resp = httpclient.execute(httpPost);
                if (resp.getStatusLine().getStatusCode() != 200) {
                    throw new CurrationException("GeoLocateService failed to send request to Geolocate for "+resp.getStatusLine().getStatusCode());
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
                StringBuilder sb = new StringBuilder();
                while (br.ready()) {
                    sb.append(br.readLine());
                }
                stream = new StringReader(sb.toString());
                httpPost.releaseConnection();

                if (useCache && cache != null) {
                    skey.add(sb.toString());
                    cache.insert(skey);
                }
            } catch (ClientProtocolException e) {
                throw new CurrationException("GeoLocateService failed to access GeoLocate service for "+e.getMessage());
            } catch (UnsupportedEncodingException e) {
                throw new CurrationException("GeoLocateService failed to access GeoLocate service for "+e.getMessage());
            } catch (IOException e) {
                throw new CurrationException("GeoLocateService failed to access GeoLocate service for "+e.getMessage());
            }
        }

        SAXReader reader = new SAXReader();
        HashMap<String,String> map = new HashMap<String,String>();
        map.put( "geo", defaultNameSpace);
        reader.getDocumentFactory().setXPathNamespaceURIs(map);
        try {
            document = reader.read(stream);
        } catch (DocumentException e) {
            throw new CurrationException("GeoLocateService failed to get the coordinates information by parsing the response from GeoLocate service at: "+url+" for: "+e.getMessage());
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
	
	private HashMap<String, String> cachedCoordinates;
	private Vector<String> newFoundCoordinates;
	private static final String ColumnDelimiterInCacheFile = "\t";
	
	private final String serviceName = "GEOLocate";
	
	private final String url = "http://www.museum.tulane.edu/webservices/geolocatesvc/geolocatesvc.asmx/Georef2";
    //private final String url = "http://lore.genomecenter.ucdavis.edu/cache/geolocate.php";
	private final String defaultNameSpace = "http://www.museum.tulane.edu/webservices/";
}
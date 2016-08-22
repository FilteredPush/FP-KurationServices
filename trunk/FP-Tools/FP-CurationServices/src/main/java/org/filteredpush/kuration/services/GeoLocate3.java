package org.filteredpush.kuration.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.filteredpush.kuration.interfaces.IGeoRefValidationService;
import org.filteredpush.kuration.util.Cache;
import org.filteredpush.kuration.util.CacheValue;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationException;
import org.filteredpush.kuration.util.GEOUtil;
import org.filteredpush.kuration.util.GeoRefCacheValue;
import org.filteredpush.kuration.util.GeolocationAlternative;
import org.filteredpush.kuration.util.GeolocationResult;
import org.kurator.akka.data.CurationStep;
import org.nocrala.tools.gis.data.esri.shapefile.ShapeFileReader;
import org.nocrala.tools.gis.data.esri.shapefile.ValidationPreferences;
import org.nocrala.tools.gis.data.esri.shapefile.exception.InvalidShapeFileException;
import org.nocrala.tools.gis.data.esri.shapefile.shape.AbstractShape;
import org.nocrala.tools.gis.data.esri.shapefile.shape.PointData;
import org.nocrala.tools.gis.data.esri.shapefile.shape.shapes.PolygonShape;

import edu.tulane.museum.www.webservices.GeolocatesvcSoapProxy;
import edu.tulane.museum.www.webservices.Georef_Result;
import edu.tulane.museum.www.webservices.Georef_Result_Set;

import java.awt.geom.Path2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class GeoLocate3 extends BaseCurationService implements IGeoRefValidationService {
	
	private static final Log logger = LogFactory.getLog(GeoLocate3.class);

    private boolean useCache = true;
    private Cache cache;
    
	private File cacheFile = null;

	private double correctedLatitude;
	private double correctedLongitude;
    private List<List> log = new LinkedList<List>();

    static int count = 0;
	private static HashMap<String, CacheValue> coordinatesCache = new HashMap<String, CacheValue>();
	private Vector<String> newFoundCoordinates;
	private static final String ColumnDelimiterInCacheFile = "\t";
	
	private final String url = "http://www.museum.tulane.edu/webservices/geolocatesvc/geolocatesvc.asmx/Georef2?";
    //private final String url = "http://lore.genomecenter.ucdavis.edu/cache/geolocate.php";
	private final String defaultNameSpace = "http://www.museum.tulane.edu/webservices/";    
	
	/*
	 * If latitude or longitude is null, it means such information is missing in the original records
	 * 
	 * @see org.kepler.actor.SpecimenQC.IGeoRefValidationService#validateGeoRef(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void validateGeoRef(String country, String stateProvince, String county, String waterBody, String verbatimDepth, String locality, String latitude, String longitude, double thresholdDistanceKm){
		logger.debug("Geolocate3.validateGeoref("+country+","+stateProvince+","+county+","+locality+")");
		
		HashMap<String, String> initialValues = new HashMap<String, String>();
		initialValues.put("decimalLatitude", latitude);
		initialValues.put("decimalLongitude", longitude);
		initBase(new CurationStep("Validate Georeference: check dwc:decimalLatitude and dwc:decimalLongitude against the textual locality data. ", initialValues));
		setCurationStatus(CurationComment.UNABLE_CURATED);
		correctedLatitude = -1;
		correctedLongitude = -1;
		
		// overloaded for extraction into "WAS" values by MongoSummaryWriter
        addToServiceName("decimalLatitude:" + latitude + "#decimalLongitude:" + longitude + "#");
        this.addInputValue("decimalLatitude", latitude);
        this.addInputValue("decimalLongitude", longitude);
        log = new LinkedList<List>();
        
        List<GeolocationResult> potentialMatches = null;

        //first search for reference coordinates
        String key = country+" "+stateProvince+" "+county+" "+locality;
        if (useCache && coordinatesCache.containsKey(key)){
            GeoRefCacheValue cachedValue = (GeoRefCacheValue) coordinatesCache.get(key);
            GeolocationResult fromCache = new GeolocationResult(cachedValue.getLat(), cachedValue.getLng(),0,0,"");
            potentialMatches = new ArrayList<GeolocationResult>();
            potentialMatches.add(fromCache);
		    logger.debug("Geolocate3.validateGeoref found in cache " + fromCache.getLatitude() + " " + fromCache.getLongitude());
            //System.out.println("geocount = " + count++);
            //System.out.println("key = " + key);
        } else {
        	// Look up locality in Tulane's GeoLocate service
            try {
        	    potentialMatches = queryGeoLocateMulti(country, stateProvince, county, locality, latitude, longitude);
            } catch (CurationException e) {
                setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
                addToComment(e.getMessage());
            }

            if(potentialMatches == null || potentialMatches.size()==0){
                setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
                addToComment("GeoLocate service can't find coordinates of Locality. ");
            }
        }

        // start validation
        
        // Make strings null if they don't contain valid lat/long values
        if (latitude!=null && latitude.trim().length()==0) { latitude = null; } 
        if (longitude!=null && longitude.trim().length()==0) { longitude = null; } 
        if (latitude!=null) { 
           try { Double.valueOf(latitude); } catch (NumberFormatException e) { latitude = null; }
        }
        if (longitude!=null) { 
           try { Double.valueOf(longitude); } catch (NumberFormatException e) { longitude = null; }
        }
        
        // Try to fill in missing values 
        if(latitude == null || longitude == null){
        	if (potentialMatches.size()>0 && potentialMatches.get(0).getConfidence()>80 ) { 
        		if (latitude!=null && longitude==null) { 
        			// Try to fill in the longitude
        			if (GeolocationResult.isLocationNearAResult(Double.valueOf(latitude), potentialMatches.get(0).getLongitude(), potentialMatches, (int)Math.round(thresholdDistanceKm * 1000))) {
        				// if latitude plus longitude from best match is near a match, propose the longitude from the best match.
        			    setCurationStatus(CurationComment.FILLED_IN);
        				correctedLongitude = potentialMatches.get(0).getLongitude();
        				// TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc.
        				addToComment("Added a longitude from "+getServiceName()+" as longitude was missing and geolocate had a confident match near the original line of latitude. ");
        			}
        		}
        		if (latitude==null && longitude!=null) { 
        			// Try to fill in the latitude
        			if (GeolocationResult.isLocationNearAResult(potentialMatches.get(0).getLatitude(), Double.valueOf(longitude), potentialMatches, (int)Math.round(thresholdDistanceKm * 1000))) {
        				// if latitude plus longitude from best match is near a match, propose the longitude from the best match.
        			    setCurationStatus(CurationComment.FILLED_IN);
        				correctedLatitude = potentialMatches.get(0).getLatitude();
        				// TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc.
        				addToComment("Added a latitude from "+getServiceName()+" as latitude was missing and geolocate had a confident match near the original line of longitude. ");
        			}
        		}
        		//Both coordinates in the original record are missing
        		if (latitude==null && longitude ==null) { 
        			setCurationStatus(CurationComment.FILLED_IN);
        			correctedLatitude = potentialMatches.get(0).getLatitude();
        			correctedLongitude = potentialMatches.get(0).getLongitude();
        			// TODO: If we do this, then we need to add the datum, georeference source, georeference method, etc.
        			addToComment("Added a georeference using cached data or "+getServiceName()+"service since the original coordinates are missing and geolocate had a confident match. ");
        		}
        	} else { 
        		setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
        		addToComment("No latitude and/or longitude provided, and geolocate didn't return a good match.");
        	}
        } else {
            //calculate the distance from the returned point and original point in the record
            //If the distance is smaller than a certainty, then use the original point --- GEOService, like GeoLocate can't parse detailed locality. In this case, the original point has higher confidence
            //Otherwise, use the point returned from GeoLocate
        	addToComment("Latitute and longitude are both present.");

            double originalLat = Double.valueOf(latitude);
            double originalLong = Double.valueOf(longitude);
            double rawLat = originalLat;
            double rawLong = originalLong;
            
            // Construct a list of alternatives
            List<GeolocationAlternative> alternatives = GeolocationAlternative.constructListOfAlternatives(originalLat, originalLong); 

            boolean flagError = false;
            boolean foundGoodMatch = false;
            
            // Check for possible error conditions
            
            // (1) Latitude and longitude out of range
            if (Math.abs(originalLat)>90) { 
                addToComment("The original latitude is out of range.");
                flagError = true;
            }
            if (Math.abs(originalLong)>180) { 
                addToComment("The original longitude is out of range.");
                flagError = true;
            }
            if (!flagError) { 
        	    addToComment("Latitute is within +/-90 and longitude is within +/-180.");
            }

            // Check country and stateProvince
            if (country != null && country.length()>0) {
            	//standardize country names
            	if (country.toUpperCase().equals("USA")) {
            		country = "United States";
            	} else if (country.toUpperCase().equals("U.S.A.")) {
            		country = "United States";
            	} else if (country.toLowerCase().equals("united states of america")) {
            		country = "United States";
            	} else {
            		country = country.toUpperCase();
            		//System.out.println("not in !##"+country+"##");
            	} 

            	// (2) Locality not inside country?
            	if (GEOUtil.isCountryKnown(country)) {  
            		if (GEOUtil.isPointInCountry(country, originalLat, originalLong)) {  
            			addToComment("Original coordinate is inside country ("+country+").");
            			addToServiceName("Country boundary data from Natural Earth");
            		} else { 
            			addToComment("Original coordinate is not inside country ("+country+").");
            			addToServiceName("Country boundary data from Natural Earth");
            			flagError = true;
            		}
            	} else { 
            		addToComment("Can't find country: " + country + " in country name list");
            	}

            	if (stateProvince!=null && stateProvince.length()>0) { 
            		// (3) Locality not inside primary division?
            		if (GEOUtil.isPrimaryKnown(country, stateProvince)) { 
            			if (GEOUtil.isPointInPrimary(country, stateProvince, originalLat, originalLong)) {
            				addToComment("Original coordinate is inside primary division ("+stateProvince+").");
            				addToServiceName("State/province boundary data from Natural Earth");
            			} else { 
            				addToComment("Original coordinate is not inside primary division ("+stateProvince+").");
            				addToServiceName("State/province boundary data from Natural Earth");
            				flagError = true;
            			} 
            		} else { 
            			addToComment("Can't find state/province: " + stateProvince + " in primaryDivision name list");
            		}
            	}
            }

            // (4) Is locality marine? 
            Set<Path2D> setPolygon = null;
            try {
            	setPolygon = ReadLandData();
            	//System.out.println("read data");
            } catch (IOException e) {
            	logger.error(e.getMessage());
            } catch (InvalidShapeFileException e) {
            	logger.error(e.getMessage());
            }
            boolean isMarine = false;
            if ((country==null||country.length()==0) && (stateProvince==null||stateProvince.length()==0) && (county==null||county.length()==0)) {
            	addToComment("No country, state/province, or county provided, guessing that this is a marine locality. ");
            	// no country provided, assume locality is marine
            	isMarine = true;
            } else { 
            	if (waterBody!=null && waterBody.trim().length()>0) { 
            		if (waterBody.matches("(Indian|Pacific|Arctic|Atlantic|Ocean|Sea|Carribean|Mediteranian)")) { 
            			isMarine = true;
            	        addToComment("A water body name that appears to be an ocean or a sea was provided, guessing that this is a marine locality. ");
            		} else { 
            	        addToComment("A country, state/province, or county was provided with a water body that doesn't appear to be an ocean or a sea, guessing that this is a non-marine locality. ");
            		}
            	} else { 
            	    addToComment("A country, state/province, or county was provided but no water body, guessing that this is a non-marine locality. ");
            	}
            }
            if (!GEOUtil.isInPolygon(setPolygon, originalLong, originalLat, isMarine)) {
            	if (isMarine) { 
            		addToComment("Coordinate is on land for a supposedly marine locality.");
            		flagError = true;
            	} else { 
            		addToComment("Coordinate is not on land for a supposedly non-marine locality.");
            		double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus zone.
            		if (GEOUtil.isPointNearCountry(country, originalLat, originalLong, thresholdDistanceKmFromLand)) { 
            			addToComment("Coordinate is within 24 nautical miles of country boundary, could be a nearshore marine locality.");
            		} else { 
            			addToComment("Coordinate is further than 24 nautical miles of country boundary, country in error or marine within EEZ.");
            			flagError = true;
            		}
            	}
            }            

            // (5) Geolocate returned some result, is original locality near that result?
            if (potentialMatches!=null && potentialMatches.size()>0) { 
            	if (GeolocationResult.isLocationNearAResult(originalLat, originalLong, potentialMatches, (int)Math.round(thresholdDistanceKm * 1000))) {
            		setCurationStatus(CurationComment.CORRECT);
            		correctedLatitude = originalLat;
            		correctedLongitude = originalLong;
            		addToComment("Original coordinates are near (within georeference error radius or " +  thresholdDistanceKm + " km) the georeference for the locality text from the Geolocate service.  Accepting the original coordinates. ");
            		flagError = false;
            		foundGoodMatch = true;
            	} else {
            		addToComment("Original coordinates are not near (within georeference error radius or " +  thresholdDistanceKm + " km) the georeference for the locality text from the Geolocate service. ");
            		flagError = true;
            	}
            }


            // Some error condition was found, see if any transposition returns a plausible locality
            boolean matchFound = false;
            if (flagError) {
            	Iterator<GeolocationAlternative> i = alternatives.iterator();
            	while (i.hasNext() && !matchFound) { 
            		GeolocationAlternative alt = i.next();
            		if (potentialMatches !=null && potentialMatches.size()>0) { 
            			if (GeolocationResult.isLocationNearAResult(alt.getLatitude(), alt.getLongitude(), potentialMatches, (int)Math.round(thresholdDistanceKm * 1000))) {
            				setCurationStatus(CurationComment.CURATED);
            				correctedLatitude = alt.getLatitude();
            				correctedLongitude = alt.getLongitude();
            				addToComment("Modified coordinates ("+alt.getAlternative()+") are near (within georeference error radius or " +  thresholdDistanceKm + " km) the georeference for the locality text from the Geolocate service.  Accepting the " + alt.getAlternative() + " coordinates. ");
            				matchFound = true;
            			}            	
            		} else {
            			if (isMarine) {
            				if (country!=null && GEOUtil.isCountryKnown(country)) { 
            					double thresholdDistanceKmFromLand = 44.448d;  // 24 nautical miles, territorial waters plus contigouus zone.
            					if (GEOUtil.isPointNearCountry(country, originalLat, originalLong, thresholdDistanceKmFromLand)) { 
            						addToComment("Modified coordinate (" + alt.getAlternative() + ") is within 24 nautical miles of country boundary.");
            						correctedLatitude = alt.getLatitude();
            						correctedLongitude = alt.getLongitude();
            						matchFound = true;
            					}
            				}
            			} else { 
            				if (GEOUtil.isCountryKnown(country) && 
            						GEOUtil.isPointInCountry(country, alt.getLatitude(), alt.getLongitude())) { 
            					addToComment("Modified coordinate ("+alt.getAlternative()+") is inside country ("+country+").");
            					if (GEOUtil.isPrimaryKnown(country, stateProvince) && 
            							GEOUtil.isPointInPrimary(country, stateProvince, originalLat, originalLong)) { 
            						setCurationStatus(CurationComment.CURATED);
            						addToComment("Modified coordinate ("+alt.getAlternative()+") is inside stateProvince ("+stateProvince+").");
            						correctedLatitude = alt.getLatitude();
            						correctedLongitude = alt.getLongitude();
            						matchFound = true;
            					}
            				}
            			}
            		}
            	}
            }
            
            if (flagError) { 
            	if (matchFound) { 
            		setCurationStatus(CurationComment.CURATED);
                    if(useCache) { 
                    	addNewToCache(correctedLatitude, correctedLongitude, country, stateProvince, county, locality); 
                    }
            	} else { 
            		if (country!=null && GEOUtil.isCountryKnown(country)) {
            			if (isMarine) { 
            				 addToComment("No transformation of the coordinates is near the provided country.");
            			} else {
            				if (stateProvince!=null && GEOUtil.isPrimaryKnown(country, stateProvince)) { 
            				     addToComment("No transformation of the coordinates is inside the provided country and state/province.");
            				}
            			}
            		}
            		setCurationStatus(CurationComment.UNABLE_CURATED);
            	}
            } else { 
            	if (foundGoodMatch) { 
            		setCurationStatus(CurationComment.CORRECT);
                    if(useCache) { 
                    	addNewToCache(originalLat, originalLong, country, stateProvince, county, locality); 
                    }
            	} else { 
            		setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
            	}
            }

            //System.out.println("setCurationStatus(" + curationStatus);
            //System.out.println("comment = " + comment);

            //System.out.println("originalLng = " + originalLng);
            //System.out.println("originalLat = " + originalLat);
            //System.out.println("country = " + country);
            //System.out.println("foundLng = " + foundLng);
            //System.out.println("foundLat = " + foundLat);

        }
		logger.debug("Geolocate3.validateGeoref done " + getCurationStatus());
	}


    public void addNewToCache(double Lat, double Lng, String country, String stateProvince, String county, String locality) {
        String key = constructCachedMapKey(country, stateProvince, county, locality);
        if(!coordinatesCache.containsKey(key)){
            CacheValue newValue = new GeoRefCacheValue(Lat, Lng);
            coordinatesCache.put(key, newValue);
            logger.debug("adding georeference to cache " + key + " " + ((GeoRefCacheValue)newValue).getLat() + " "+ ((GeoRefCacheValue)newValue).getLat());
        }

    }
	
	public double getCorrectedLatitude() {
		return correctedLatitude;
	}

	public double getCorrectedLongitude() {
		return correctedLongitude;
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

    public Set<Path2D> ReadLandData() throws IOException, InvalidShapeFileException {

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
       

    /**
     * Given country, stateProvince, county/Shire, and locality strings, return all matches found by geolocate for
     * that location.
     * 
     * @param country
     * @param stateProvince
     * @param county
     * @param locality
     * @param latitude for distance comparison in log
     * @param longitude for distance comparison in log
     * @return
     * @throws CurationException
     */
	private List<GeolocationResult> queryGeoLocateMulti(String country, String stateProvince, String county, String locality, String latitude, String longitude) throws CurationException {
        addToServiceName("GeoLocate");
        long starttime = System.currentTimeMillis();
        List<GeolocationResult> result = new ArrayList<GeolocationResult>();
        
        GeolocatesvcSoapProxy geolocateService = new GeolocatesvcSoapProxy();
        
        // Test page for georef2 at: http://www.museum.tulane.edu/webservices/geolocatesvcv2/geolocatesvc.asmx?op=Georef2
        
        boolean hwyX = false;   // look for road/river crossing
        if (locality!=null && locality.toLowerCase().matches("bridge")) { 
        	hwyX = true;
        }
        boolean findWaterbody = false;  // find waterbodies
        if (locality!=null && locality.toLowerCase().matches("(lake|pond|sea|ocean)")) { 
        	findWaterbody = true;
        }
        boolean restrictToLowestAdm = true;  
        boolean doUncert = true;  // include uncertainty radius in results
        boolean doPoly = false;   // include error polygon in results
        boolean displacePoly = false;  // displace error polygon in results
        boolean polyAsLinkID = false;
        int languageKey = 0;  // 0=english; 1=spanish
        
        Georef_Result_Set results;
		try {
			results = geolocateService.georef2(country, stateProvince, county, locality, hwyX, findWaterbody, restrictToLowestAdm, doUncert, doPoly, displacePoly, polyAsLinkID, languageKey);
            int numResults = results.getNumResults();
            this.addToComment(" found " + numResults + " possible georeferences with Geolocate engine:" + results.getEngineVersion());
            for (int i=0; i<numResults; i++) { 
            	Georef_Result res = results.getResultSet(i);
            	try {
            	   double lat2 = Double.parseDouble(latitude);
            	   double lon2 = Double.parseDouble(longitude);
              	   long distance = GEOUtil.calcDistanceHaversineMeters(res.getWGS84Coordinate().getLatitude(), res.getWGS84Coordinate().getLongitude(), lat2, lon2)/100;
            	   addToComment(res.getParsePattern() + " score:" + res.getScore() + " "+ res.getWGS84Coordinate().getLatitude() + " " + res.getWGS84Coordinate().getLongitude() + " km:" + distance);
            	} catch (NumberFormatException e) {             	
            	   addToComment(res.getParsePattern() + " score:" + res.getScore() + " "+ res.getWGS84Coordinate().getLatitude() + " " + res.getWGS84Coordinate().getLongitude());
            	}
            }
            result = GeolocationResult.constructFromGeolocateResultSet(results);
		} catch (RemoteException e) {
			logger.debug(e.getMessage());
			addToComment(e.getMessage());
		}
        
        List l = new LinkedList();
        l.add(this.getClass().getSimpleName());
        l.add(starttime);
        l.add(System.currentTimeMillis());
        l.add("POST");
        log.add(l);		
		return result;
	}
	
    /**
     * Run a locality string, country, state/province, county/parish/shire against GeoLocate, return the
     * latitude and longitude of the single best match.
     * 
     * @param country in which the locality is contained
     * @param stateProvince in which the locality is contained
     * @param county in which the locality is contained
     * @param locality string for georeferencing by GeoLocate
     * 
     * @return a vector of doubles where result[0] is the latitude and result[1] is the longitude for the 
     * single best match found by the GeoLocate web service.
     * 
     * @throws CurationException
     */
	@Deprecated 
	private Vector<Double> queryGeoLocateBest(String country, String stateProvince, String county, String locality) throws CurationException {
        addToServiceName("GeoLocate");
        long starttime = System.currentTimeMillis();
        
        Document document = getXmlFromGeolocate(country, stateProvince, county, locality);

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

	@Deprecated 
	private Document getXmlFromGeolocate(String country, String stateProvince, String county, String locality) throws CurationException { 
        Reader stream = null;
        Document document = null;

        StringBuilder loc = new StringBuilder();
        if(country == null) {  
        	addToComment("country is missing in the orignial record"); 
        } else {
        	loc.append(country);
        } 
        if(stateProvince == null) { 
        	addToComment("stateProvince is missing in the orignial record"); 
        } else { 
        	loc.append(", ").append(stateProvince);
        }
        if(county == null) { 
        	addToComment("county is missing in the orignial record"); 
        } else {
        	loc.append(", ").append(stateProvince);
        }
        if(locality == null) { 
        	addToComment("locality is missing in the orignial record, using ("+ loc.toString() +")");
        	locality = loc.toString();
        }

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
                throw new CurationException("GeoLocate3 failed to access GeoLocate service for A "+e.getMessage());
            } catch (UnsupportedEncodingException e) {
                throw new CurationException("GeoLocate3 failed to access GeoLocate service for B "+e.getMessage());
            } catch (IOException e) {
                throw new CurationException("GeoLocate3 failed to access GeoLocate service for C "+e.getMessage());
            }
        }

        SAXReader reader = new SAXReader();
        HashMap<String,String> map = new HashMap<String,String>();
        map.put( "geo", defaultNameSpace);
        reader.getDocumentFactory().setXPathNamespaceURIs(map);
        try {
            document = reader.read(stream);
        } catch (DocumentException e) {
            throw new CurationException("GeoLocate3 failed to get the coordinates information by parsing the response from GeoLocate service at: "+url+" for: "+e.getMessage());
        }
        
        return document;
	}
	
}

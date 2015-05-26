package org.filteredpush.kuration.util;

public class GEOUtil {
	/**
	 * Equatorial radius of the Earth in kilometers (GRS80).
	 */
	private static double EARTH_EQUATORIAL_RADIUS_KM = 6378.138;
	
	// GRS80 value for the equatorial radius of the Earth = 6,378,138.0 meters.
	// IERS value for the equatorial radius of the Earth =  6,378,136.3 meters.
	// Wikipedia/Australian Geodetic Datum mean equatorial radius = 6,378,160.0 meters.
	// CRC Mean radius of the Earth: 6370949.0 meters
	
	/**
	 * Mean radius of the Earth in meters (CRC).
	 */
	private static double EARTH_MEAN_RADIUS_METERS = 6370949.0d;  // Mean radius, from CRC
	
	/**
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static double getDistanceKm(double lat1, double lng1, double lat2, double lng2)
	{
	   double radLat1 = Math.toRadians(lat1);
	   double radLat2 = Math.toRadians(lat2);
	   double a = radLat1 - radLat2;
	   double b = Math.toRadians(lng1) - Math.toRadians(lng2);

	   double s = 2d * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2d),2d) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2d),2d)));
	   s = s * EARTH_EQUATORIAL_RADIUS_KM;
	   s = Math.round(s * 10000) / 10000;
	   return s;
	}

	/**
	 * Calculate distance in meters between two points on the Earth's surface using the Haversine formula,
	 * which maintains accuracy even when points are a short distance apart.
	 * 
	 * @param lat1 latitude of the first point
	 * @param lon1 longitude of the first point
	 * @param lat2 latitude of the second point
	 * @param lon2 longitude of the second point
	 * 
	 * @return great circle distance between the two points in meters.
	 */
	public static long calcDistanceHaversineMeters(double lat1, double lon1, double lat2, double lon2) {

		double lat1r = Math.toRadians(lat1);
		double lat2r = Math.toRadians(lat2);
		double long1r = Math.toRadians(lon1);
		double long2r = Math.toRadians(lon2);
	    double deltaLat = lat2r - lat1r;
	    double deltaLon = long2r - long1r;
	    double a = Math.pow((Math.sin(deltaLat/2.0d)), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow((Math.sin(deltaLon/2.0d)), 2);
	    double c = 2.0d * Math.atan2(Math.sqrt(a), Math.sqrt(1.0d-a));
	    long distance = Math.round(EARTH_MEAN_RADIUS_METERS * c);

	    return distance;
	}
	
}


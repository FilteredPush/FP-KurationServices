/**
 * 
 */
package org.filteredpush.kuration.util.test;

import static org.junit.Assert.*;

import org.filteredpush.kuration.util.GEOUtil;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class GeoUtiltsTest {

	/**
	 * Test method for {@link org.filteredpush.kuration.util.GEOUtil#getDistanceKm(double, double, double, double)}.
	 */
	@Test
	public void testGetDistance() {
		// one degree latitude at the equator is 111 km.
		assertEquals(111, GEOUtil.getDistanceKm(0, 0, 0, 1), 1d);
		
	
	}

	/**
	 * Test method for {@link org.filteredpush.kuration.util.GEOUtil#calcDistanceHaversineMeters(double, double, double, double)}.
	 */
	@Test
	public void testCalcDistanceHaversineMeters() {
		// one degree latitude at the equator is 111194 meters  
		assertEquals(111194, GEOUtil.calcDistanceHaversineMeters(0, 0, 0, 1));
		
		// each degree of longitude is a constant 111194 meters
	    assertEquals(111194, GEOUtil.calcDistanceHaversineMeters(0, 0, 1, 0));
	    for (int d=-90; d<90; d++) {
	          assertEquals(111194, GEOUtil.calcDistanceHaversineMeters(d, 0, d+1, 0));
	    }		
	    
	    // Test Case from: http://rosettacode.org/wiki/Haversine_formula#Java
	    // Distance between  (36.12, -86.67) and (33.94, -118.40) is 2887260 m
	    // Distance depends on the choice of EARTH_MEAN_RADIUS  
	    // For earth radius based on surface area 6371.0 km distance is 2886.44444283798329974715782394574671655 km;
	    // For radius based on average circumference 6372.8 km distance is 2887.25995060711033944886005029688505340 km;
	    // We are using the CRC value EARTH_MEAN_RADIUS_METERS 6370949 (radius based on surface area)
	    // so our result is slightly different from either of these.
	    assertEquals(2886421,GEOUtil.calcDistanceHaversineMeters(36.120, -86.670, 33.940, -118.400));
		
	}

}

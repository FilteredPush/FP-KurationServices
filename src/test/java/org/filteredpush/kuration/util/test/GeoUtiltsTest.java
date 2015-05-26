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
		
	}

}

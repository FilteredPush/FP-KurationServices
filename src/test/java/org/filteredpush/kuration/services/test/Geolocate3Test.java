/**
 * 
 */
package org.filteredpush.kuration.services.test;

import static org.junit.Assert.*;

import org.filteredpush.kuration.services.GeoLocate3;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class Geolocate3Test {

	GeoLocate3 geolocate3 = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	    geolocate3 = new GeoLocate3();
	}

	/**
	 * Test method for {@link org.filteredpush.kuration.services.GeoLocate3#validateGeoRef(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, double)}.
	 */
	@Test
	public void testValidateGeoRef() {
		geolocate3.validateGeoRef("United States", "Alaska", "", "Barrow", "71.295556", "-156.766389", 20d);
		System.out.println(geolocate3.getComment());
		assertEquals("Valid",geolocate3.getCurationStatus().toString().trim());
		// Transposed
		geolocate3.validateGeoRef("United States", "Alaska", "", "Barrow", "-156.766389", "71.295556", 20d);
		System.out.println(geolocate3.getComment());
		assertEquals("Curated",geolocate3.getCurationStatus().toString().trim());
		
	}

}

package org.filteredpush.kuration.services.test;

import org.filteredpush.kuration.interfaces.IGeoRefValidationService;
import org.filteredpush.kuration.services.GeoLocate3;
import org.filteredpush.kuration.util.CurationComment;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by tianhong on 10/22/14.
 */
public class GeoRefServiceTest {
    private IGeoRefValidationService geoRefValidationServiceService = new GeoLocate3();
    //String serviceClassQN = "org.filteredpush.kuration.services.COLService";
    //scientificNameService = (INewScientificNameValidationService)Class.forName(serviceClassQN).newInstance();

    @Test
    public void validTest() {
        geoRefValidationServiceService.validateGeoRef("USA", "Arizona", "", "", "", "1 mi. S of Portal", "31.899097", "-109.14083", 200.0);
        //assertTrue(scientificNameService.getCorrectedScientificName().equals("Eucerceris canaliculata"));
        assertTrue(geoRefValidationServiceService.getCurationStatus().equals(CurationComment.CORRECT));
    }
    @Test
    public void signChangeTest(){
        //geoRefValidationServiceService.validateGeoRef("USA", "Arizona", "", "0.25 mi. N of Pigeon Springs", "33.714026", "111.337804", 200.0);
        geoRefValidationServiceService.validateGeoRef("USA", "Arizona", "", "", "", "1 mi. S of Portal", "31.899097", "109.14083", 200.0);
        assertEquals(-109.14083, geoRefValidationServiceService.getCorrectedLongitude(), .0001d);
        assertEquals(31.899097, geoRefValidationServiceService.getCorrectedLatitude(), .0001d);
        assertTrue(geoRefValidationServiceService.getCurationStatus().equals(CurationComment.CURATED));
    }

    @Test
    public void unableTest(){
        //geoRefValidationServiceService.validateGeoRef("USA", "Arizona", "", "2.2 mi. S of Sulphide del Rey", "83.275381", "-10.862407", 200.0);  //no result in geolocate
        geoRefValidationServiceService.validateGeoRef("USA", "Arizona", "", "", "", "1 mi. S of Portal", "81.899097", "10.14083", 200.0);
        assertTrue(geoRefValidationServiceService.getCurationStatus().equals(CurationComment.UNABLE_CURATED));
    }

    @Test
    public void nullTest(){
        geoRefValidationServiceService.validateGeoRef("USA", "Arizona", "", "", "", "2.2 mi. S of Sulphide del Rey", "", "-110.862407", 200.0);
        assertEquals(CurationComment.UNABLE_DETERMINE_VALIDITY, geoRefValidationServiceService.getCurationStatus());
    }
}

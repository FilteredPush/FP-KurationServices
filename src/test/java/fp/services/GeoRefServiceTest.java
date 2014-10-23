package fp.services;

import fp.util.CurationComment;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by tianhong on 10/22/14.
 */
public class GeoRefServiceTest {
    private IGeoRefValidationService geoRefValidationServiceService = new GeoLocate2();
    //String serviceClassQN = "fp.services.COLService";
    //scientificNameService = (INewScientificNameValidationService)Class.forName(serviceClassQN).newInstance();

    @Test
    public void validTest(){
        geoRefValidationServiceService.validateGeoRef("USA", "Arizona", "", "1 mi. S of Portal", "31.899097", "-109.14083", 200.0);
        //System.out.println("scientificNameService1 = " + scientificNameService.getCorrectedScientificName());
        //assertTrue(scientificNameService.getCorrectedScientificName().equals("Eucerceris canaliculata"));
        assertTrue(geoRefValidationServiceService.getCurationStatus().equals(CurationComment.CORRECT));

        geoRefValidationServiceService.validateGeoRef("USA", "Arizona", "", "0.25 mi. N of Pigeon Springs", "33.714026", "-111.337804", 200.0);
        assertTrue(geoRefValidationServiceService.getCurationStatus().equals(CurationComment.CORRECT));
    }

    @Test
    public void unableTest(){
        geoRefValidationServiceService.validateGeoRef("USA", "Arizona", "", "2.2 mi. S of Sulphide del Rey", "33.275381", "-110.862407", 200.0);
        //System.out.println("scientificNameService1 = " + scientificNameService.getCorrectedScientificName());
        //assertTrue(scientificNameService.getCorrectedScientificName().equals("Eucerceris canaliculata"));
        assertTrue(geoRefValidationServiceService.getCurationStatus().equals(CurationComment.UNABLE_CURATED));
    }
}

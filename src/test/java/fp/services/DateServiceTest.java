package fp.services;

import fp.util.CurationComment;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by tianhong on 10/22/14.
 */
public class DateServiceTest {
    private IInternalDateValidationService internalDateValidationService = new InternalDateValidationService();


    @Test
    public void validNameTest(){
        String name = "Eucerceris canaliculata";
        internalDateValidationService.validateDate("1948-08-24","","237","1948","8","24","2012-09-26 02:54:34","A.C. Cole");
        System.out.println("scientificNameService1 = " + internalDateValidationService.getCorrectedDate());
        //assertTrue(scientificNameService.getCorrectedScientificName().equals("Eucerceris canaliculata"));
        assertTrue(internalDateValidationService.getCurationStatus().equals(CurationComment.CORRECT));
    }



}

package org.filteredpush.kuration.services.test;

import org.filteredpush.kuration.interfaces.IInternalDateValidationService;
import org.filteredpush.kuration.services.InternalDateValidationService;
import org.filteredpush.kuration.util.CurationComment;
import org.joda.time.Interval;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by tianhong on 10/22/14.
 */
public class DateServiceTest {
    private IInternalDateValidationService internalDateValidationService = new InternalDateValidationService();


    @Test
    public void validNameTest(){
        internalDateValidationService.validateDate("1948-08-24","","237","1948","8","24","2012-09-26 02:54:34","A.C. Cole");
        // assertTrue(internalDateValidationService.getCorrectedDate().equals("1948-08-24"));
        assertEquals(null,internalDateValidationService.getCorrectedDate());
        assertTrue(internalDateValidationService.getCurationStatus().equals(CurationComment.CORRECT));
        internalDateValidationService.validateDate("1848-08-24","","237","1848","8","24","2012-09-26 02:54:34","A.C. Cole");
        //assertTrue(internalDateValidationService.getCorrectedDate().equals("1848-08-24"));
        assertEquals(null,internalDateValidationService.getCorrectedDate());
        assertEquals(CurationComment.UNABLE_CURATED.toString(), internalDateValidationService.getCurationStatus().toString());
    }
    @Test
    public void inconsistentTest(){
        internalDateValidationService.validateDate("1948-08-24","","137","1948","7","27","2012-09-26 02:54:34","A.C. Cole");
        assertEquals(null,internalDateValidationService.getCorrectedDate());
        assertTrue(internalDateValidationService.getCurationStatus().equals(CurationComment.UNABLE_CURATED));
    }
    @Test
    public void constructFromAtomicTest(){
        internalDateValidationService.validateDate("0000-00-00","","237","1948","7","27","2012-09-26 02:54:34","A.C. Cole");
        assertTrue(internalDateValidationService.getCorrectedDate().equals("1948-07-27"));
        assertTrue(internalDateValidationService.getCurationStatus().equals(CurationComment.Filled_in));
    }
    @Test
    public void noDateTest(){
        internalDateValidationService.validateDate("0000-00-00","","237","","","","2012-09-26 02:54:34","A.C. Cole");
        //assertTrue(internalDateValidationService.getCorrectedDate().equals("1948-07-27"));
        assertTrue(internalDateValidationService.getCurationStatus().equals(CurationComment.UNABLE_DETERMINE_VALIDITY));
    }
    @Test
    public void rangeTest(){
    	// A date range
        internalDateValidationService.validateDate("1948-08-24/1948-09-01","","237","1948","8","24","2012-09-26 02:54:34","A.C. Cole");
        assertEquals(CurationComment.CORRECT.toString(), internalDateValidationService.getCurationStatus().toString());
        
        // Try a date range that puts the earlier date last
        internalDateValidationService.validateDate("1948-08-24/1947-09-01","","237","1948","8","24","2012-09-26 02:54:34","A.C. Cole");
        assertEquals(CurationComment.UNABLE_CURATED.toString(), internalDateValidationService.getCurationStatus().toString());
    } 
    @Test
    public void isRangeTest() { 
        assertFalse(InternalDateValidationService.isRange("1880-01-02"));
        assertTrue(InternalDateValidationService.isRange("1880-01-01/1880-12-31"));
        assertFalse(InternalDateValidationService.isRange("1880/01"));
        assertFalse(InternalDateValidationService.isRange("1880/01/01"));
        assertTrue(InternalDateValidationService.isRange("1980-01-01/1880-12-31"));  // is range doesn't test start/end
        assertTrue(InternalDateValidationService.isRange("1880/1881"));
    }
    
    @Test
    public void extractRangeTest() { 
    	Interval test = InternalDateValidationService.extractInterval("1880-01-01/1880-12-31");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());
    	test = InternalDateValidationService.extractInterval("1880");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());
    	test = InternalDateValidationService.extractInterval("1880-02");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(2, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfMonth());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(2, test.getEnd().getMonthOfYear());
    	assertEquals(29, test.getEnd().getDayOfMonth());
    }

}

package org.filteredpush.kuration.services.test;

import org.filteredpush.kuration.interfaces.IInternalDateValidationService;
import org.filteredpush.kuration.services.InternalDateValidationService;
import org.filteredpush.kuration.util.CurationComment;
import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by tianhong on 10/22/14.
 */
public class InternalDateValidationServiceTest {
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
    public void shortTest(){
        internalDateValidationService.validateDate("1948","","","1948","","","2012-09-26 02:54:34","A.C. Cole");
        assertEquals("1948-01-01/1948-12-31",internalDateValidationService.getCorrectedDate());
        assertTrue(internalDateValidationService.getCurationStatus().equals(CurationComment.CORRECT));
        
        internalDateValidationService.validateDate("1929-09","","","","","","","J. F. Rock");
        assertEquals("1948-01-01/1948-12-31",internalDateValidationService.getCorrectedDate());
        assertTrue(internalDateValidationService.getCurationStatus().equals(CurationComment.CORRECT));
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
        assertEquals(null,internalDateValidationService.getCorrectedDate());
        
        internalDateValidationService.validateDate("1948-08-24/1948-09-01","","","","","","2012-09-26 02:54:34","A.C. Cole");
        assertEquals(CurationComment.CORRECT.toString(), internalDateValidationService.getCurationStatus().toString());
        assertEquals(null,internalDateValidationService.getCorrectedDate());
        
        // Try a date range that puts the earlier date last
        internalDateValidationService.validateDate("1948-08-24/1947-09-01","","237","1948","8","24","2012-09-26 02:54:34","A.C. Cole");
        assertEquals(CurationComment.UNABLE_CURATED.toString(), internalDateValidationService.getCurationStatus().toString());
        
        // Try range partly overlapping
        internalDateValidationService.validateDate("1848-08-24/1948-09-01","","","","","","2012-09-26 02:54:34","A.C. Cole");
        assertEquals(CurationComment.CURATED.toString(), internalDateValidationService.getCurationStatus().toString());
        assertEquals("1918-01-01/1948-09-01",internalDateValidationService.getCorrectedDate());
        internalDateValidationService.validateDate("1948-08-24/2048-09-01","","","","","","2012-09-26 02:54:34","A.C. Cole");
        assertEquals(CurationComment.CURATED.toString(), internalDateValidationService.getCurationStatus().toString());
        assertEquals("1948-08-24/1995-12-31",internalDateValidationService.getCorrectedDate());
        internalDateValidationService.validateDate("1848-08-24/2048-09-01","","","","","","2012-09-26 02:54:34","A.C. Cole");
        assertEquals(CurationComment.CURATED.toString(), internalDateValidationService.getCurationStatus().toString());
        assertEquals("1918-01-01/1995-12-31",internalDateValidationService.getCorrectedDate());
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
    
    @Test
    public void extractDateTest() { 
    	DateMidnight test = InternalDateValidationService.extractDate("1980-04-03");
    	assertEquals(1980, test.getYear());
    	assertEquals(4, test.getMonthOfYear());
    	assertEquals(3, test.getDayOfMonth());
    	test = InternalDateValidationService.extractDate("1980-04");
    	assertEquals(1980, test.getYear());
    	assertEquals(4, test.getMonthOfYear());
    	assertEquals(1, test.getDayOfMonth());
    	test = InternalDateValidationService.extractDate("1980");
    	assertEquals(1980, test.getYear());
    	assertEquals(1, test.getMonthOfYear());
    	assertEquals(1, test.getDayOfMonth());
    	assertEquals(null,InternalDateValidationService.extractDate(""));
    }

	@Test
	public void testValidateDateHUHBotanists() {
		
		// 1884-1962
		String collector = "J. F. Rock";
		
		InternalDateValidationService service = new InternalDateValidationService();
        DateMidnight eventDate = null;
        DateTimeFormatter format = ISODateTimeFormat.date();
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy/MM/dd").getParser(),
                ISODateTimeFormat.date().getParser()
        };
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();

        try{
            eventDate = DateMidnight.parse("1904/01/01", formatter);
		    assertTrue(service.checkWithAuthorHarvard(eventDate, collector));
        } catch (Exception e) {
        	fail("Unexpected exception " + e.getMessage());
        }
        try{
		    service = new InternalDateValidationService();
		    assertTrue(service.checkWithAuthorHarvard(eventDate, "J.F. Rock"));
        } catch (Exception e) {
        	fail("Unexpected exception " + e.getMessage());
        }
        try{
		    service = new InternalDateValidationService();
		    assertEquals(null, service.checkWithAuthorHarvard(eventDate, "Not A Name, Test"));
            eventDate = DateMidnight.parse("1804/01/01", formatter);
        } catch (Exception e) {
        	fail("Unexpected exception " + e.getMessage());
        }
        try{
            service = new InternalDateValidationService();
		    assertFalse(service.checkWithAuthorHarvard(eventDate, collector));
        } catch (Exception e) {
        	fail("Unexpected exception " + e.getMessage());
        }
	}
	
	@Test
	public void testLookupBotanist() { 
		// 1884-1962
		String collector = "J. F. Rock";
		InternalDateValidationService service = new InternalDateValidationService();
		Interval test = service.lookUpHarvardBotanist(collector);
		assertEquals(1884,test.getStart().getYear());
		assertEquals(1962,test.getEnd().getYear());
		collector = "J.F. Rock";
		service = new InternalDateValidationService();
		test = service.lookUpHarvardBotanist(collector);
		assertEquals(1884,test.getStart().getYear());
		assertEquals(1962,test.getEnd().getYear());
	}
	
	@Test
	public void testValidateDateEntomologists() {
		
		// 1839-1905
		String collector = "Alpheus Spring Packard Jr.";
		
		InternalDateValidationService service = new InternalDateValidationService();
        DateMidnight eventDate = null;
        DateTimeFormatter format = ISODateTimeFormat.date();
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy/MM/dd").getParser(),
                ISODateTimeFormat.date().getParser()
        };
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();

        try{
            eventDate = DateMidnight.parse("1864/01/01", formatter);
		    assertTrue(service.checkWithAuthorSolr(eventDate, collector));
		    service = new InternalDateValidationService();
		    assertEquals(null, service.checkWithAuthorSolr(eventDate, "Not A Name, Test"));
            eventDate = DateMidnight.parse("1964/01/01", formatter);
            service = new InternalDateValidationService();
		    assertFalse(service.checkWithAuthorSolr(eventDate, collector));
        } catch (Exception e) {
        	fail("Unexpected exception " + e.getMessage());
        }
	}	
	
	@Test
	public void testLookupEntomologist() { 
		// 1839-1905
		String collector = "Alpheus Spring Packard Jr.";
		InternalDateValidationService service = new InternalDateValidationService();
		Interval test = service.lookupEntomologist(collector);
		assertEquals(1839,test.getStart().getYear());
		assertEquals(1905,test.getEnd().getYear());
	}	
    
}

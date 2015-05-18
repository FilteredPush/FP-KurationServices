package org.filteredpush.kuration.services.test;

import static org.junit.Assert.*;

import org.filteredpush.kuration.services.InternalDateValidationService;
import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

public class DateValidationServiceTest {

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
		    service = new InternalDateValidationService();
		    assertEquals(null, service.checkWithAuthorHarvard(eventDate, "Not A Name, Test"));
            eventDate = DateMidnight.parse("1804/01/01", formatter);
            service = new InternalDateValidationService();
		    assertFalse(service.checkWithAuthorHarvard(eventDate, collector));
        } catch (Exception e) {
        	fail("Unexpected exception " + e.getMessage());
        }
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

}

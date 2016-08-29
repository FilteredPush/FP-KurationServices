/** DateUtilsTest.java
 * 
 * Copyright 2015 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.filteredpush.kuration.util.test;

import static org.junit.Assert.*;

import java.util.Map;

import org.filteredpush.kuration.util.DateUtils;
import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DateUtilsTest {

	/**
	 * Test method for {@link org.filteredpush.kuration.util.DateUtils#createEventDateFromParts(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateEventDateFromParts() {
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts(null, null, null, "1882", "03", "24"));
		assertEquals(null, DateUtils.createEventDateFromParts(null, null, null, null, null, null));
		assertEquals(null, DateUtils.createEventDateFromParts(null, null, null, null, null, "01"));
		assertEquals(null, DateUtils.createEventDateFromParts(null, null, null, null, "01", "01"));
		assertEquals(null, DateUtils.createEventDateFromParts(null, "01", null, null, "01", "01"));
		assertEquals(null, DateUtils.createEventDateFromParts(null, "01", "01", null, "01", "01"));
		
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("1882-03-24", null, null, "1882", "03", "24"));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("1882-03-24", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("03/24/1882", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("24/03/1882", null, null, null, null, null));
		
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("1882-Mar-24", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("Mar/24/1882", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("March 24, 1882", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("Mar. 24, 1882", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("Mar 24, 1882", null, null, null, null, null));
		
		assertEquals("1882-04-04", DateUtils.createEventDateFromParts("04/04/1882", null, null, null, null, null));
		assertEquals("1882-02-03/1882-03-02", DateUtils.createEventDateFromParts("02/03/1882", null, null, null, null, null));
		
		assertEquals("1882-01-01/1882-01-31", DateUtils.createEventDateFromParts("1882-01", null, null, null, null, null));
		assertEquals("1882-04-01/1882-04-30", DateUtils.createEventDateFromParts("1882-04", null, null, null, null, null));
		assertEquals("1882-01-01/1882-01-31", DateUtils.createEventDateFromParts("1882-Jan", null, null, null, null, null));
		assertEquals("1882-01-01/1882-01-31", DateUtils.createEventDateFromParts("1882/Jan", null, null, null, null, null));
		assertEquals("1882-01-01/1882-12-31", DateUtils.createEventDateFromParts("1882", null, null, null, null, null));
		
		assertEquals("1890-02-01", DateUtils.createEventDateFromParts("1890-032", null, null, null, null, null));
		assertEquals("1890-02-01", DateUtils.createEventDateFromParts("1890-032", "32", "32", null, null, null));
		assertEquals("1890-02-01", DateUtils.createEventDateFromParts("1890-032", "32", null, null, null, null));
		
		assertEquals("1882-01-05", DateUtils.createEventDateFromParts(null, "5",  null, "1882", null, null));
		assertEquals("1882-01-05", DateUtils.createEventDateFromParts(null, "5", "5", "1882", null, null));
		assertEquals("1882-01-05", DateUtils.createEventDateFromParts(null, "005", null, "1882", null, null));
		assertEquals("1882-01-05", DateUtils.createEventDateFromParts("1882", "5", null, null, null, null));
		assertEquals("1882-01-05", DateUtils.createEventDateFromParts("1882", "005", null, null, null, null));

		assertEquals("1882-01-05/1882-01-06", DateUtils.createEventDateFromParts("1882", "005", "006", null, null, null));		
		assertEquals("1882-01-05/1882-01-06", DateUtils.createEventDateFromParts(null, "005", "006", "1882", null, null));		
		
		assertEquals(null, DateUtils.createEventDateFromParts("1880-02-32", null, null, null, null, null));
		assertEquals(null, DateUtils.createEventDateFromParts("Feb 31, 1880", null, null, null, null, null));
		
		//TODO: More tests of date creation
	}

	/**
	 * Test method for {@link org.filteredpush.kuration.util.DateUtils#isRange(java.lang.String)}.
	 */
    @Test
    public void isRangeTest() { 
        assertFalse(DateUtils.isRange("1880-01-02"));
        assertTrue(DateUtils.isRange("1880-01-01/1880-12-31"));
        assertFalse(DateUtils.isRange("1880/01"));
        assertFalse(DateUtils.isRange("1880/01/01"));
        assertTrue(DateUtils.isRange("1980-01-01/1880-12-31"));  // is range doesn't test start/end
        assertTrue(DateUtils.isRange("1880/1881"));
        assertTrue(DateUtils.isRange("1880-03"));
        assertTrue(DateUtils.isRange("1884-01-01T05:05Z/1884-12-05"));
    }

	/**
	 * Test method for {@link org.filteredpush.kuration.util.DateUtils#extractInterval(java.lang.String)}.
	 */
	@Test
	public void testExtractInterval() {
    	Interval test = DateUtils.extractDateInterval("1880-01-01/1880-12-31");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());
    	test = DateUtils.extractDateInterval("1880");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());
    	test = DateUtils.extractDateInterval("1880-02");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(2, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfMonth());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(2, test.getEnd().getMonthOfYear());
    	assertEquals(29, test.getEnd().getDayOfMonth());
    	test = DateUtils.extractDateInterval("1880-01-01T08:30Z/1880-12-31");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(0, test.getStart().getHourOfDay());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());    	
    	assertEquals(0, test.getEnd().getHourOfDay());    	
    }	

	/**
	 * Test method for {@link org.filteredpush.kuration.util.DateUtils#extractDate(java.lang.String)}.
	 */
    @Test
    public void extractDateTest() { 
    	DateMidnight test = DateUtils.extractDate("1980-04-03");
    	assertEquals(1980, test.getYear());
    	assertEquals(4, test.getMonthOfYear());
    	assertEquals(3, test.getDayOfMonth());
    	test = DateUtils.extractDate("1980-04");
    	assertEquals(1980, test.getYear());
    	assertEquals(4, test.getMonthOfYear());
    	assertEquals(1, test.getDayOfMonth());
    	test = DateUtils.extractDate("1980");
    	assertEquals(1980, test.getYear());
    	assertEquals(1, test.getMonthOfYear());
    	assertEquals(1, test.getDayOfMonth());
    	assertEquals(null,DateUtils.extractDate(""));
    }
    
    @Test
    public void isConsistentTest() { 
    	assertEquals(true, DateUtils.isConsistent("", "", "", ""));
    	assertEquals(true, DateUtils.isConsistent(null, "", "", ""));
    	assertEquals(true, DateUtils.isConsistent(null, "", null, ""));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18", "1884", "03", "18"));
    	assertEquals(false, DateUtils.isConsistent("1884-03-18", "1884", "03", "17"));
    	assertEquals(false, DateUtils.isConsistent("1884-03-18", "1884", "03", ""));
    	assertEquals(false, DateUtils.isConsistent("1884-03-18", "1884", "03", null));
    	assertEquals(false, DateUtils.isConsistent("1884-03-18", "1884", null, "18"));
    	assertEquals(false, DateUtils.isConsistent("1884-03-18", null, "03", "18"));
    	assertEquals(false, DateUtils.isConsistent(null, "1884", "03", "18"));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01", "1884", "01", null));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01", "1884", null, null));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01", null, null, null));
    	assertEquals(false, DateUtils.isConsistent(null, "1884", "1", "1"));
    	assertEquals(true, DateUtils.isConsistent("1884-03-01", "1884", "03", "1"));
    	assertEquals(true, DateUtils.isConsistent("1884-03-01", "1884", "03", "01"));
    	assertEquals(true, DateUtils.isConsistent("1884-03", "1884", "03", "01"));
    	assertEquals(false, DateUtils.isConsistent("1884-03", "1884", "03", "02"));
    	assertEquals(true, DateUtils.isConsistent("1884-03", "1884", "03", ""));
    	assertEquals(true, DateUtils.isConsistent("1884-03", "1884", "03", null));
    	assertEquals(true, DateUtils.isConsistent("1884-01-01/1884-01-31", "1884", "01", null));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01/1884-01-05", "1884", "01", null));    	
    	assertEquals(true, DateUtils.isConsistent("1884-01-01/1884-01-31", "1884", "01", ""));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01/1884-01-05", "1884", "01", ""));    	
    	assertEquals(true, DateUtils.isConsistent("1884-01-01/1884-12-31", "1884", null, null));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01/1884-12-05", "1884", null, null));    	
    	assertEquals(false, DateUtils.isConsistent("1884-01-01T05:05Z/1884-12-05", "1884", null, null));
    	
    	assertEquals(true, DateUtils.isConsistent("1884-03-18/1884-03-19", "1884", "03", "18"));
    }
    
    @Test
    public void isConsistentTest2() {
    	assertEquals(true, DateUtils.isConsistent("", "", "", "", "",""));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18", "", "", "1884", "03", "18"));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18", "078", "078", "1884", "03", "18"));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18/1884-03-19", "078", "079", "1884", "03", "18"));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18/1884-03-19", "078", "079", null, null, null));
    }
    
    @Test
    public void containsTimeTest() { 
    	assertEquals(false, DateUtils.containsTime(""));
    	assertEquals(false, DateUtils.containsTime(null));
    	assertEquals(false, DateUtils.containsTime("1905-04-08"));
    	assertEquals(false, DateUtils.containsTime("1905-04-08/1905-06-18"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T08"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04Z"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04UTC"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04:06"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04:06Z"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04:06-05:00"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04:06-04:30"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T01:02:03.004Z"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T08/1905-06-18T08"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T21/1905-06-18"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08/1905-06-18T15"));
    	
    	assertEquals(true, DateUtils.containsTime("1905-04-08T00:00:00.000Z"));
    }
    
    @Test
    public void extractTimeTest() { 
    	assertEquals(null, DateUtils.extractZuluTime(""));
    	assertEquals(null, DateUtils.extractZuluTime(null));
    	assertEquals(null, DateUtils.extractZuluTime("1905-04-08"));
    	assertEquals("08:00:00.000Z", DateUtils.extractZuluTime("1905-04-08T08Z"));
    	assertEquals("08:32:16.000Z", DateUtils.extractZuluTime("1905-04-08T08:32:16Z"));
    	assertEquals("13:32:16.000Z", DateUtils.extractZuluTime("1905-04-08T08:32:16-05:00"));
    	assertEquals(null, DateUtils.extractZuluTime("1251e3254w2v"));
    }
    
    
    @Test
    public void extractDateFromVerbatimTest() { 
    	Map<String,String> result = DateUtils.extractDateFromVerbatim("May 9th 1880");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1880-05-09", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim(null);
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim(" ");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim("[ ]");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim("[]");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim("..");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim("zzzzzz");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim("****");
    	assertEquals(0, result.size());
    	
    	result = DateUtils.extractDateFromVerbatim("Jul. 9th 1880");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1880-07-09", result.get("result"));   
    	
    	result = DateUtils.extractDateFromVerbatim("Sept. 5 1901");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1901-09-05", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("[Sept. 5 1901]");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1901-09-05", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("May 13. 1883");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1883-05-13", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("May 9th, 1915");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1915-05-09", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Oct. 13th, 1991");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1991-10-13", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Oct 13th, 1991");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1991-10-13", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("October 13th, 1991");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1991-10-13", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("October 14th 1902");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1902-10-14", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("October 15 1916");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1916-10-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Oct. 15-1916");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1916-10-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("May 16-1910");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1910-05-16", result.get("result"));    	
    	
    	result = DateUtils.extractDateFromVerbatim("May 20th. 1902");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1902-05-20", result.get("result"));      	
    	
    	result = DateUtils.extractDateFromVerbatim("11-VI-1886");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1886-06-11", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("6.I.1928");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1928-01-06", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("11-VII-1885");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1885-07-11", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("7. VII. 1878");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1878-07-07", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("7. VIII. 1878");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1878-08-07", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("7. X. 1877");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1877-10-07", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("7,V,1941");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1941-05-07", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("8.14.1893");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1893-08-14", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("31 April 1902");
    	assertEquals(0, result.size());
    	
    	result = DateUtils.extractDateFromVerbatim("July, 14, 1879");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1879-07-14", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("21 Sept.,1902");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1902-09-21", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("21 Sept.,1902.");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1902-09-21", result.get("result"));     	
    	
    	result = DateUtils.extractDateFromVerbatim("June 38, 1939");
    	assertEquals(0, result.size());
    	
    	result = DateUtils.extractDateFromVerbatim("May, 1 1962");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1962-05-01", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Sept. 1,1962");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1962-09-01", result.get("result"));    	
    	
    	result = DateUtils.extractDateFromVerbatim("11/5 1898");
    	assertEquals("ambiguous", result.get("resultState"));
    	assertEquals("1898-05-11/1898-11-05", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("May, 18. 1898");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1898-05-18", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("III/20/1958");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1958-03-20", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("May 20. 1898");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1898-05-20", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("22 Sept, 1904");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1904-09-22", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("1943 June 10");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1943-06-10", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("June 17.1883");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1883-06-17", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("janvier 17 1883");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1883-01-17", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("janv. 17 1883");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1883-01-17", result.get("result"));    	
    	
    	result = DateUtils.extractDateFromVerbatim("1933, July 16");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1933-07-16", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("2010年10月18日");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("2010-10-18", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("1910-01-01/1911-12-31");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1910-01-01/1911-12-31", result.get("result"));      	

    	result = DateUtils.extractDateFromVerbatim("April 1871");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1871-04", result.get("result"));  
    	
    	result = DateUtils.extractDateFromVerbatim("June, 1871");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1871-06", result.get("result"));  
    	
    	result = DateUtils.extractDateFromVerbatim("July 16-26, 1945");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1945-07-16/1945-07-26", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("July 16 to 26, 1945");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1945-07-16/1945-07-26", result.get("result"));     	
    	
    	result = DateUtils.extractDateFromVerbatim("July 16-26 1945");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1945-07-16/1945-07-26", result.get("result")); 
    	
    	/*
    	 May and June 1899
    	 [29 Apr - 24 May 1847]
		 July 17 and 18, 1914
    	 Sept.-Oct. 1943
    	*/  
    	
    }
}

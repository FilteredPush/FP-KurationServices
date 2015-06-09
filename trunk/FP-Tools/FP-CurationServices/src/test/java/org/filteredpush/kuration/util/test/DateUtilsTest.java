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
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts(null, null, "1882", "03", "24"));
		
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
    }

	/**
	 * Test method for {@link org.filteredpush.kuration.util.DateUtils#extractInterval(java.lang.String)}.
	 */
	@Test
	public void testExtractInterval() {
    	Interval test = DateUtils.extractInterval("1880-01-01/1880-12-31");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());
    	test = DateUtils.extractInterval("1880");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());
    	test = DateUtils.extractInterval("1880-02");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(2, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfMonth());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(2, test.getEnd().getMonthOfYear());
    	assertEquals(29, test.getEnd().getDayOfMonth());
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

}

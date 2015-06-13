/** DateUtils.java
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
 */
package org.filteredpush.kuration.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Utility functions for working with dwc date concepts.
 * 
 * @author mole
 *
 */
public class DateUtils {

	private static final Log logger = LogFactory.getLog(DateUtils.class);
	
	/**
	 * Attempt to construct an ISO formatted date as a string built from atomic parts of the date.
	 * 
	 * @param verbatimEventDate
	 * @param startDayOfYear
	 * @param year
	 * @param month
	 * @param day
	 * 
	 * @return null, or a string in the form of an ISO date consistent with the input fields.
	 * 	 
	 */
	public static String createEventDateFromParts(String verbatimEventDate, String startDayOfYear, String year, String month, String day) {
		String result = null;
		// TODO: Add support for extraction/comparison with verbatim event date and day of year.
		if (year!=null && year.matches("[0-9]{4}") && (month==null || month.trim().length()==0) &&( day==null || day.trim().length()==0 )) {  
		    result = year;
		}
		if (year!=null && year.matches("[0-9]{4}") && month!=null && month.matches("[0-9]{1,2}") &&( day==null || day.trim().length()==0 )) {  
		    result = year + "-" + month;
		}
		if (year!=null && year.matches("[0-9]{4}") && month!=null && month.matches("[0-9]{1,2}") && day!=null && day.matches("[0-9]{1,2}")) {  
		    result = year + "-" + month + "-" + day;
		}
		return result;
	}

    /**
     * Test to see if a string appears to represent a date range.
     * 
     * @param eventDate to check
     * @return true if a date range, false otherwise.
     */
    public static boolean isRange(String eventDate) { 
    	boolean isRange = false;
    	if (eventDate!=null) { 
    		String[] dateBits = eventDate.split("/");
    		if (dateBits!=null && dateBits.length==2) { 
    			//probably a range.
    			DateTimeParser[] parsers = { 
    					DateTimeFormat.forPattern("yyyy-MM").getParser(),
    					DateTimeFormat.forPattern("yyyy").getParser(),
    					ISODateTimeFormat.date().getParser() 
    			};
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    			try { 
    				// must be at least a 4 digit year.
    				if (dateBits[0].length()>3 && dateBits[1].length()>3) { 
    					DateMidnight startDate = DateMidnight.parse(dateBits[0],formatter);
    					DateMidnight endDate = DateMidnight.parse(dateBits[1],formatter);
    					// both start date and end date must parse as dates.
    					isRange = true;
    				}
    			} catch (Exception e) { 
    				// not a date range
    			}
    		}
    	}
    	return isRange;
    }	
	
    
    /**
     * Given a string that may be a date or a date range, extract an interval of
     * time from that date range.
     * 
     * @param eventDate
     * @return
     */
    public static Interval extractInterval(String eventDate) {
    	Interval result = null;
    	DateTimeParser[] parsers = { 
    			DateTimeFormat.forPattern("yyyy-MM").getParser(),
    			DateTimeFormat.forPattern("yyyy").getParser(),
    			ISODateTimeFormat.date().getParser() 
    	};
    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    	if (isRange(eventDate)) {
    		String[] dateBits = eventDate.split("/");
    		try { 
    			// must be at least a 4 digit year.
    			if (dateBits[0].length()>3 && dateBits[1].length()>3) { 
    				DateMidnight startDate = DateMidnight.parse(dateBits[0],formatter);
    				DateMidnight endDate = DateMidnight.parse(dateBits[1],formatter);
    				// both start date and end date must parse as dates.
    				result = new Interval(startDate, endDate);
    			}
    		} catch (Exception e) { 
    			// not a date range
               logger.error(e.getMessage());
    		}
    	} else {
    		try { 
               DateMidnight startDate = DateMidnight.parse(eventDate, formatter);
               logger.debug(startDate);
               if (eventDate.length()==4) { 
                  result = new Interval(startDate,startDate.plusMonths(12).minusDays(1));
               } else if (eventDate.length()==7) { 
                  result = new Interval(startDate,startDate.plusMonths(1).minusDays(1));
               } else { 
                  result = new Interval(startDate,startDate.plusDays(1));
               }
    		} catch (Exception e) { 
    			// not a date
               logger.error(e.getMessage());
    		}
    	}
    	return result;
    }
    
    /**
     * Extract a joda date from an event date.
     * 
     * @param eventDate
     * @return
     */
    public static DateMidnight extractDate(String eventDate) {
    	DateMidnight result = null;
    	DateTimeParser[] parsers = { 
    			DateTimeFormat.forPattern("yyyy-MM").getParser(),
    			DateTimeFormat.forPattern("yyyy").getParser(),
    			ISODateTimeFormat.date().getParser() 
    	};
    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    		try { 
               result = DateMidnight.parse(eventDate, formatter);
               logger.debug(result);
    		} catch (Exception e) { 
    			// not a date
               logger.error(e.getMessage());
    		}
    	return result;
    }        
}

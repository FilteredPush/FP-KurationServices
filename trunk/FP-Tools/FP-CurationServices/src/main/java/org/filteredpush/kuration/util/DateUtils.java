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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Utility functions for working with DarwinCore date concepts.
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
	 * @param endDayOfYear
	 * @param year
	 * @param month
	 * @param day
	 * 
	 * @return null, or a string in the form of an ISO date consistent with the input fields.
	 * 	 
	 */
	public static String createEventDateFromParts(String verbatimEventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day) {
		String result = null;

		if (verbatimEventDate!=null && verbatimEventDate.trim().length()>0) { 
			Map<String,String> verbatim = extractDateFromVerbatim(verbatimEventDate); 
			if (verbatim.size()>0) { 
				if (verbatim.get("resultState")!=null && verbatim.get("resultState").equals("date")) { 
					result = verbatim.get("result");
				}
				if (verbatim.get("resultState")!=null && verbatim.get("resultState").equals("ambiguous")) { 
					result = verbatim.get("result");
				}		
				if (verbatim.get("resultState")!=null && verbatim.get("resultState").equals("range")) { 
					result = verbatim.get("result");
				}					
			}
		}
		if (year!=null && year.matches("[0-9]{4}") && isEmpty(month) && isEmpty(day) && isEmpty(startDayOfYear)) { 
		    result = year;
		}		
		if (year!=null && year.matches("[0-9]{4}") && 
				(month==null || month.trim().length()==0) && 
				( day==null || day.trim().length()==0 ) && 
				startDayOfYear !=null && startDayOfYear.trim().length() > 0
				) {
			try { 
				StringBuffer assembly = new StringBuffer();
				if (endDayOfYear !=null && endDayOfYear.trim().length() > 0 && !startDayOfYear.trim().equals(endDayOfYear.trim())) {  
					assembly.append(year).append("-").append(String.format("%03d",Integer.parseInt(startDayOfYear))).append("/");
					assembly.append(year).append("-").append(String.format("%03d",Integer.parseInt(endDayOfYear)));
				} else { 
					assembly.append(year).append("-").append(String.format("%03d",Integer.parseInt(startDayOfYear)));
				}
			    Map<String,String> verbatim = extractDateFromVerbatim(assembly.toString()) ;
			    logger.debug(verbatim.get("resultState"));
			    logger.debug(verbatim.get("result"));
				if (verbatim.get("resultState")!=null && (verbatim.get("resultState").equals("date") || verbatim.get("resultState").equals("range"))) { 
					result = verbatim.get("result");
				}
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}		
		if (    (verbatimEventDate!=null && verbatimEventDate.matches("^[0-9]{4}$")) &&
				(year==null || year.trim().length()==0) && 
				(month==null || month.trim().length()==0) && 
				( day==null || day.trim().length()==0 ) && 
				startDayOfYear !=null && startDayOfYear.trim().length() > 0
				) {
			try { 
				StringBuffer assembly = new StringBuffer();
				if (endDayOfYear !=null && endDayOfYear.trim().length() > 0 && !startDayOfYear.trim().equals(endDayOfYear.trim())) {  
					assembly.append(verbatimEventDate).append("-").append(String.format("%03d",Integer.parseInt(startDayOfYear))).append("/");
					assembly.append(verbatimEventDate).append("-").append(String.format("%03d",Integer.parseInt(endDayOfYear)));
				} else { 
					assembly.append(verbatimEventDate).append("-").append(String.format("%03d",Integer.parseInt(startDayOfYear)));
				}
			    Map<String,String> verbatim = extractDateFromVerbatim(assembly.toString()) ;
			    logger.debug(verbatim.get("resultState"));
			    logger.debug(verbatim.get("result"));
				if (verbatim.get("resultState")!=null && (verbatim.get("resultState").equals("date") || verbatim.get("resultState").equals("range"))) { 
					result = verbatim.get("result");
				}
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
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
	 * 
	 * @param verbatimEventDate
	 * @return
	 */
	public static Map<String,String> extractDateFromVerbatim(String verbatimEventDate) {
		Map result = new HashMap<String,String>();
		String resultDate = null;
		
		if (verbatimEventDate.matches("^[0-9]{4}[-/]([0-9]{1,2}|[A-Za-z]+)[-/][0-9]{1,2}.*")) { 
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("yyyy/MM/dd").getParser(),
						DateTimeFormat.forPattern("yyyy/MMM/dd").getParser(),
						DateTimeFormat.forPattern("yyyy-MMM-dd").getParser(),
						ISODateTimeFormat.dateOptionalTimeParser().getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM-dd");
				result.put("resultState", "date");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}
		if (verbatimEventDate.matches("^([0-9]{1,2}|[A-Za-z]+)[-/]([0-9]{1,2}|[A-Za-z]+)[-/][0-9]{4}$")) { 
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("MMM/dd/yyyy").getParser(),
						DateTimeFormat.forPattern("dd/MMM/yyyy").getParser(),
						DateTimeFormat.forPattern("MMM-dd-yyyy").getParser(),
						DateTimeFormat.forPattern("dd-MMM-yyyy").getParser()						
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM-dd");
			    result.clear();
				result.put("resultState", "date");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}
		if (verbatimEventDate.matches("^[0-9]{4}[-][0-9]{3}/[0-9]{4}[-][0-9]{3}$")) { 
			try { 
				String[] bits = verbatimEventDate.split("/");
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("yyyy-D").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				LocalDate parseStartDate = LocalDate.parse(bits[0],formatter);
				LocalDate parseEndDate = LocalDate.parse(bits[1],formatter);
				resultDate =  parseStartDate.toString("yyyy-MM-dd") + "/" + parseEndDate.toString("yyyy-MM-dd");
				logger.debug(resultDate);
			    result.clear();
				result.put("resultState", "range");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}		
		if (result.size()==0) {
			String resultDateMD = null;
			String resultDateDM = null;
			DateMidnight parseDate1 = null;
			DateMidnight parseDate2 = null;
			try { 
				DateTimeParser[] parsers = { 
					DateTimeFormat.forPattern("MM/dd/yyyy").getParser(),
					DateTimeFormat.forPattern("MM-dd-yyyy").getParser(),
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				parseDate1 = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				resultDateMD = parseDate1.toString("yyyy-MM-dd");
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
			try { 
				DateTimeParser[] parsers = { 
					DateTimeFormat.forPattern("dd/MM/yyyy").getParser(),
					DateTimeFormat.forPattern("dd-MM-yyyy").getParser(),
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				parseDate2 = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				resultDateDM = parseDate2.toString("yyyy-MM-dd");
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
			if (resultDateMD!=null && resultDateDM==null) {
				result.put("resultState", "date");
				result.put("result",resultDateMD);
			} else if (resultDateMD==null && resultDateDM!=null) { 
				result.put("resultState", "date");
				result.put("result",resultDateDM);
			} else if (resultDateMD!=null && resultDateDM!=null) { 
				if (resultDateMD.equals(resultDateDM)) { 
					result.put("resultState", "date");
					result.put("result",resultDateDM);
				} else { 
				    result.put("resultState", "ambiguous");
				    Interval range = null;
				    if (parseDate1.isBefore(parseDate2)) { 
				        result.put("result", resultDateMD + "/" + resultDateDM);
				    } else { 
				        result.put("result", resultDateDM + "/" + resultDateMD);
				    }
				}
			} 
		}
		if (verbatimEventDate.matches("^[0-9]{4}[-][0-9]{3}$")) { 
			if (result.size()==0) {
				try { 
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("yyyy-D").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					LocalDate parseDate = LocalDate.parse(verbatimEventDate,formatter);
					resultDate =  parseDate.toString("yyyy-MM-dd");
					logger.debug(resultDate);
					result.put("resultState", "date");
					result.put("result",resultDate);
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}			

			}	
		}
		
		if (result.size()==0) {
			try { 
				DateTimeParser[] parsers = { 
					DateTimeFormat.forPattern("yyyy/M").getParser(),
					DateTimeFormat.forPattern("yyyy-M").getParser(),
					DateTimeFormat.forPattern("yyyy-MMM").getParser(),
					DateTimeFormat.forPattern("yyyy/MMM").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				LocalDate parseDate = LocalDate.parse(verbatimEventDate,formatter);
				resultDate =  parseDate.dayOfMonth().withMinimumValue() + "/" + parseDate.dayOfMonth().withMaximumValue();
				logger.debug(resultDate);
				result.put("resultState", "range");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}
		if (result.size()==0) {
			try { 
				DateTimeParser[] parsers = { 
					DateTimeFormat.forPattern("yyyy").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				LocalDate parseDate = LocalDate.parse(verbatimEventDate,formatter);
				resultDate =  parseDate.dayOfYear().withMinimumValue() + "/" + parseDate.dayOfYear().withMaximumValue();
				logger.debug(resultDate);
				result.put("resultState", "range");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}	
		if (result.size()==0) {
			try { 
				DateTimeParser[] parsers = { 
					DateTimeFormat.forPattern("MMM dd, yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd, yyyy").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				LocalDate parseDate = LocalDate.parse(verbatimEventDate,formatter);
				resultDate =  parseDate.toString("yyyy-MM-dd");
				logger.debug(resultDate);
				result.put("resultState", "date");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}		
		
		return result;
	}

    /**
     * Test to see if a string appears to represent a date range of more than one day.
     * 
     * @param eventDate to check
     * @return true if a date range, false otherwise.
     */
    public static boolean isRange(String eventDate) { 
    	boolean isRange = false;
    	if (eventDate!=null) { 
    		String[] dateBits = eventDate.split("/");
    		if (dateBits!=null && dateBits.length==2) { 
    		    logger.debug(dateBits.length);
    			//probably a range.
    			DateTimeParser[] parsers = { 
    					DateTimeFormat.forPattern("yyyy-MM").getParser(),
    					DateTimeFormat.forPattern("yyyy").getParser(),
    					ISODateTimeFormat.dateOptionalTimeParser().getParser()
    			};
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    			try { 
    				// must be at least a 4 digit year.
    				if (dateBits[0].length()>3 && dateBits[1].length()>3) { 
    					DateMidnight startDate = LocalDate.parse(dateBits[0],formatter).toDateMidnight();
    					DateMidnight endDate = LocalDate.parse(dateBits[1],formatter).toDateMidnight();
    					// both start date and end date must parse as dates.
    					isRange = true;
    				}
    			} catch (Exception e) { 
    				// not a date range
    				e.printStackTrace();
    				logger.debug(e.getMessage());
    			}
    		} else if (dateBits!=null && dateBits.length==1) { 
    			logger.debug(dateBits[0]);
    			// Date bits does not contain a /
    			// Is eventDate in the form yyyy-mm-dd, if so, not a range  
    			DateTimeParser[] parsers = { 
    					DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
    			};
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    			try { 
    				DateMidnight date = DateMidnight.parse(eventDate,formatter);
    				isRange = false;
    			} catch (Exception e) { 
    				logger.debug(e.getMessage());
    				// not parsable with the yyyy-mm-dd parser.
    				DateTimeParser[] parsers2 = { 
        					DateTimeFormat.forPattern("yyyy-MM").getParser(),
        					DateTimeFormat.forPattern("yyyy").getParser(),
        			};
        			formatter = new DateTimeFormatterBuilder().append( null, parsers2 ).toFormatter();
        			try { 
        				// must be at least a 4 digit year.
        				if (dateBits[0].length()>3) { 
        					DateMidnight startDate = DateMidnight.parse(dateBits[0],formatter);
        					// date must parse as either year or year and month dates.
        					isRange = true;
        				}
        			} catch (Exception e1) { 
        				// not a date range
        			}    				
    				
    			}
    			
    		}
    	}
    	return isRange;
    }	
	
    
    /**
     * Given a string that may be a date or a date range, extract a interval of
     * dates from that date range (ignoring time).
     * 
     * @param eventDate
     * @return An interval from one DateMidnight to another DateMidnight.
     */
    public static Interval extractDateInterval(String eventDate) {
    	Interval result = null;
    	DateTimeParser[] parsers = { 
    			DateTimeFormat.forPattern("yyyy-MM").getParser(),
    			DateTimeFormat.forPattern("yyyy").getParser(),
    			ISODateTimeFormat.dateOptionalTimeParser().getParser() 
    	};
    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    	if (eventDate!=null && eventDate.contains("/") && isRange(eventDate)) {
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
    			e.printStackTrace();
               logger.error(e.getMessage());
    		}
    	}
    	return result;
    }
    
  
    
    /**
     * Extract a single joda date from an event date.
     * 
     * @param eventDate
     * @return a DateMidnight or null if a date cannot be extracted
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
    
    /**
     * Identify whether an event date is consistent with its atomic parts.  
     * 
     * @param eventDate
     * @param startDayOfYear
     * @param endDayOfYear
     * @param year
     * @param month
     * @param day
     * 
     * @return true if consistent, otherwise false.
     */
    public static boolean isConsistent(String eventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day) {
		// TODO: Add support for eventTime
    	boolean result = false;
    	result = isConsistent(eventDate,year,month,day);
    	logger.debug(result);
    	if ((result || (!isEmpty(eventDate) && isEmpty(year) && isEmpty(month) && isEmpty(day))) && (!isEmpty(startDayOfYear) || !isEmpty(endDayOfYear))) {
    		if (endDayOfYear==null || endDayOfYear.trim().length()==0 || startDayOfYear.trim().equals(endDayOfYear.trim())) {
    			int startDayInt = -1;
    			try {
    				startDayInt = Integer.parseInt(startDayOfYear);
    			} catch (NumberFormatException e) {
    				logger.debug(e.getMessage());
    				logger.debug(startDayOfYear + " is not an integer."); 
    				result = false; 
    			} 
    			if (DateUtils.extractDate(eventDate).getDayOfYear() == startDayInt) { 
    				result=true;
    			} else { 
    				result = false;
    			}
    		} else {
       			int startDayInt = -1;
       			int endDayInt = -1;
    			try {
    				startDayInt = Integer.parseInt(startDayOfYear);
    				endDayInt = Integer.parseInt(endDayOfYear);
    			} catch (NumberFormatException e) {
    				logger.debug(e.getMessage());
    				result = false; 
    			} 
    			Interval eventDateInterval = DateUtils.extractDateInterval(eventDate);
    			logger.debug(eventDateInterval);
    			int endDayOfInterval = eventDateInterval.getEnd().getDayOfYear();  // midnight on the next day, so subtract 1 to get the same integer day.
    			if (eventDateInterval.getStart().getDayOfYear() == startDayInt && endDayOfInterval == endDayInt ) { 
    				result=true;
    			} else { 
    				result = false;
    			}
    		}
    	}
    	return result;
    }
    
    /**
     * Identify whether an event date and a year, month, and day are consistent, where consistent 
     * means that either the eventDate is a single day and the year-month-day represent the same day
     * or that eventDate is a date range that defines the same date range as year-month (where day is 
     * null) or the same date range as year (where day and month are null).  If all of eventDate, 
     * year, month, and day are null or empty, then returns true.  If eventDate specifies an interval
     * of more than one day and day is specified, then result is true if the day is the first day of the 
     * interval.  If eventDate is not null and year, month, and day are, then result is false (data is 
     * not consistent with no data).
     * 
     * @param eventDate
     * @param year
     * @param month
     * @param day
     * 
     * @return true if eventDate is consistent with year-month-day.
     */
    public static boolean isConsistent(String eventDate, String year, String month, String day) {
    	boolean result = false;
    	StringBuffer date = new StringBuffer();
    	if (!isEmpty(eventDate)) {
    		if (!isEmpty(year) && !isEmpty(month) && !isEmpty(day)) { 
    			date.append(year).append("-").append(month).append("-").append(day);
    			if (!isRange(eventDate)) { 
    				DateMidnight eventDateDate = extractDate(eventDate);
    				DateMidnight bitsDate = extractDate(date.toString());
    				if (eventDateDate!=null && bitsDate !=null) { 
    					if (eventDateDate.year().compareTo(bitsDate)==0 && eventDateDate.monthOfYear().compareTo(bitsDate)==0 && eventDateDate.dayOfMonth().compareTo(bitsDate)==0) {
    						result = true;   
    					}	   
    				}
    			} else {
    				Interval eventDateDate = extractDateInterval(eventDate);
    				DateMidnight bitsDate = extractDate(date.toString());
    				if (eventDateDate!=null && bitsDate !=null) { 
    					if (eventDateDate.getStart().year().compareTo(bitsDate)==0 && eventDateDate.getStart().monthOfYear().compareTo(bitsDate)==0 && eventDateDate.getStart().dayOfMonth().compareTo(bitsDate)==0) {
    						result = true;   
    					}	   
    				}    				
    				
    			}
    		}
    		if (!isEmpty(year) && !isEmpty(month) && isEmpty(day)) { 
    			date.append(year).append("-").append(month);
    			Interval eventDateInterval = extractDateInterval(eventDate);
    			Interval bitsInterval = extractDateInterval(date.toString());
    			if (eventDateInterval.equals(bitsInterval)) {
    				result = true;
    			}
    		}    	
    		if (!isEmpty(year) && isEmpty(month) && isEmpty(day)) { 
    			date.append(year);
    			Interval eventDateInterval = extractDateInterval(eventDate);
    			Interval bitsInterval = extractDateInterval(date.toString());
    			if (eventDateInterval.equals(bitsInterval)) {
    				result = true;
    			}
    		}    		
    	} else { 
    		if (isEmpty(year) && isEmpty(month) && isEmpty(day)) {
    			// eventDate, year, month, and day are all empty, treat as consistent.
    			result = true;
    		}
    	}
        return result;
    }
    
    /**
     * Does a string contain a non-blank value.
     * 
     * @param aString to check
     * @return true if the string is null, is an empty string, or contains only whitespace.
     */
    public static boolean isEmpty(String aString)  {
    	boolean result = true;
    	if (aString != null && aString.trim().length()>0) { 
    		result = false;
    	}
    	return result;
    }
    
}

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
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
	 * Verbatim dates that parse to years prior to this year are considered suspect
	 * by default.
	 * 
	 */
	public static final int YEAR_BEFORE_SUSPECT = 1000;
	
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
			Map<String,String> verbatim = extractDateToDayFromVerbatim(verbatimEventDate, DateUtils.YEAR_BEFORE_SUSPECT); 
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
				if (!isEmpty(endDayOfYear) && !startDayOfYear.trim().equals(endDayOfYear.trim())) {  
					assembly.append(year).append("-").append(String.format("%03d",Integer.parseInt(startDayOfYear))).append("/");
					assembly.append(year).append("-").append(String.format("%03d",Integer.parseInt(endDayOfYear)));
				} else { 
					assembly.append(year).append("-").append(String.format("%03d",Integer.parseInt(startDayOfYear)));
				}
			    Map<String,String> verbatim = extractDateToDayFromVerbatim(assembly.toString(), DateUtils.YEAR_BEFORE_SUSPECT) ;
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
			    Map<String,String> verbatim = extractDateToDayFromVerbatim(assembly.toString(), DateUtils.YEAR_BEFORE_SUSPECT) ;
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
	 * Attempt to extract a date or date range in standard format from a provided verbatim 
	 * date string.  
	 * 
	 * @param verbatimEventDate
	 * @return
	 */
	public static Map<String,String> extractDateFromVerbatim(String verbatimEventDate) {
		return extractDateFromVerbatim(verbatimEventDate, DateUtils.YEAR_BEFORE_SUSPECT);
	}
	
	/**
	 * Extract a date from a verbatim date, returning ranges specified to day.
	 * 
	 * @param verbatimEventDate
	 * @param yearsBeforeSuspect
	 * @return 
	 */
	public static Map<String,String> extractDateToDayFromVerbatim(String verbatimEventDate, int yearsBeforeSuspect) {
		Map<String,String> result =  extractDateFromVerbatim(verbatimEventDate, yearsBeforeSuspect);
		if (result.size()>0 && result.get("resultState").equals("range")) {
			String dateRange = result.get("result");
			try { 
				   Interval parseDate = extractDateInterval(dateRange);
				   logger.debug(parseDate);
				   String resultDate =  parseDate.getStart().toString("yyyy-MM-dd") + "/" + parseDate.getEnd().toString("yyyy-MM-dd");
				   result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}
		return result;
	}
	
	/**
	 * Given a string that may represent a date or range of dates, or date time or range of date times,
	 * attempt to extract a standard date from that string.
	 * 
	 * @param verbatimEventDate
	 * @param yearsBeforeSuspect  Dates that parse to a year prior to this year are marked as suspect.
	 * 
	 * @return a map with keys resultState for the nature of the match and result for the resulting date. 
	 */
	public static Map<String,String> extractDateFromVerbatim(String verbatimEventDate, int yearsBeforeSuspect) {		
		Map result = new HashMap<String,String>();
		String resultDate = null;
		
		// Strip off leading and trailing []
		if (verbatimEventDate!=null && verbatimEventDate.startsWith("[")) { 
			verbatimEventDate = verbatimEventDate.substring(1);
		}
		if (verbatimEventDate!=null && verbatimEventDate.endsWith("]")) { 
			verbatimEventDate = verbatimEventDate.substring(0,verbatimEventDate.length()-1);
		}
		
		// Stop before doing work if provided verbatim string is null.
		if (isEmpty(verbatimEventDate)) { 
			return result;
		}
		
		if (verbatimEventDate.matches("^[0-9]{1,2}[-. ][0-9]{1,2}[-. ][0-9]{4}/[0-9]{1,2}[-. ][0-9]{1,2}[-. ][0-9]{4}$")) {
			// if verbatim date is a range with identical first and last dates, use just one.
			String[] bits = verbatimEventDate.split("/");
			if (bits.length==2 && bits[0].equals(bits[1])) { 
				verbatimEventDate = bits[0];
			}
		}
		if (verbatimEventDate.matches("^[0-9]{1,2}[./ ][0-9]{1,2}[./ ][0-9]{4}[-][0-9]{1,2}[./ ][0-9]{1,2}[./ ][0-9]{4}$")) {
			// if verbatim date is a range with identical first and last dates, use just one.
			String[] bits = verbatimEventDate.split("-");
			if (bits.length==2 && bits[0].equals(bits[1])) { 
				verbatimEventDate = bits[0];
			}
		}
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
		if (verbatimEventDate.matches("^[0-9]{1,2}[-/ ][0-9]{4}")) { 
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("MM-yyyy").getParser(),
						DateTimeFormat.forPattern("MM/yyyy").getParser(),
						DateTimeFormat.forPattern("MM yyyy").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM");
				result.put("resultState", "range");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}		
		if (verbatimEventDate.matches("^[0-9]{4}年[0-9]{1,2}月[0-9]{1,2}[日号]$")) { 
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("yyyy年MM月dd日").getParser(),
						DateTimeFormat.forPattern("yyyy年MM月dd号").getParser(),
						ISODateTimeFormat.dateOptionalTimeParser().getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter().withLocale(Locale.CHINESE);
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM-dd");
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
		if (result.size()==0 && verbatimEventDate.matches("^[A-Za-z]{3,9}[.]{0,1}[-/ ][0-9]{4}$")) { 
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("MMM-yyyy").getParser(),
						DateTimeFormat.forPattern("MMM/yyyy").getParser(),
						DateTimeFormat.forPattern("MMM yyyy").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				String cleaned = verbatimEventDate.replace(".", "");
				DateMidnight parseDate = LocalDate.parse(cleaned,formatter).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM");
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
					DateTimeFormat.forPattern("MM/dd yyyy").getParser(),
					DateTimeFormat.forPattern("MM-dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MM.dd.yyyy").getParser()
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
					DateTimeFormat.forPattern("dd/MM yyyy").getParser(),
					DateTimeFormat.forPattern("dd-MM-yyyy").getParser(),
					DateTimeFormat.forPattern("dd.MM.yyyy").getParser()
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
		if (result.size()==0 && verbatimEventDate.matches("^([0-9]{1,2}|[A-Za-z]+)[-/.]([0-9]{1,2}|[A-Za-z]+)[-/. ][0-9]{4}$")) { 
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("MMM/dd/yyyy").getParser(),
						DateTimeFormat.forPattern("dd/MMM/yyyy").getParser(),
						DateTimeFormat.forPattern("MMM/dd yyyy").getParser(),
						DateTimeFormat.forPattern("dd/MMM yyyy").getParser(),
						DateTimeFormat.forPattern("MMM-dd-yyyy").getParser(),
						DateTimeFormat.forPattern("dd-MMM-yyyy").getParser(),
						DateTimeFormat.forPattern("MMM-dd yyyy").getParser(),
						DateTimeFormat.forPattern("dd-MMM yyyy").getParser(),
						DateTimeFormat.forPattern("MMM.dd.yyyy").getParser(),
						DateTimeFormat.forPattern("dd.MMM.yyyy").getParser(),
						DateTimeFormat.forPattern("MM.dd.yyyy").getParser(),
						DateTimeFormat.forPattern("dd.MM.yyyy").getParser()						
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter.withLocale(Locale.ENGLISH)).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM-dd");
			    result.clear();
				result.put("resultState", "date");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
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
				LocalDate parseDate = LocalDate.parse(verbatimEventDate,formatter.withLocale(Locale.ENGLISH));
				resultDate =  parseDate.toString("yyyy-MM");
				// resultDate =  parseDate.dayOfMonth().withMinimumValue() + "/" + parseDate.dayOfMonth().withMaximumValue();
				logger.debug(resultDate);
				if (verbatimEventDate.matches("^[0-9]{4}[-][0-9]{2}$")) { 
				   String century = verbatimEventDate.substring(0,2);
				   String startBit = verbatimEventDate.substring(0,4);
				   String endBit = verbatimEventDate.substring(5, 7);
				   // 1815-16  won't parse here, passes to next block
				   // 1805-06  could be month or abbreviated year
				   // 1805-03  should to be month
				   if (Integer.parseInt(startBit)>=Integer.parseInt(century+endBit)) { 
				      result.put("resultState", "range");
				      result.put("result",resultDate);
				   } else { 
					  result.put("resultState", "suspect");
				      result.put("result",resultDate);
				   }
				} else {
				   result.put("resultState", "range");
				   result.put("result",resultDate);
				}
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}
		if (result.size()==0 && verbatimEventDate.matches("^[0-9]{4}[-][0-9]{2}$")) {
			try { 
				String century = verbatimEventDate.substring(0,2);
				String startBit = verbatimEventDate.substring(0,4);
				String endBit = verbatimEventDate.substring(5, 7);
				String assembly = startBit+"/"+century+endBit;
				logger.debug(assembly);
				Interval parseDate = Interval.parse(assembly);
				logger.debug(parseDate);
				resultDate =  parseDate.getStart().toString("yyyy") + "/" + parseDate.getEnd().toString("yyyy");
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
					DateTimeFormat.forPattern("yyyy MMM dd").getParser(),
					DateTimeFormat.forPattern("yyyy MMM. dd").getParser(),
					DateTimeFormat.forPattern("yyyy, MMM dd").getParser(),
					DateTimeFormat.forPattern("yyyy, MMM. dd").getParser(),
					DateTimeFormat.forPattern("yyyy.MMM.dd").getParser(),
					
					DateTimeFormat.forPattern("yyyy MMM dd'st'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM. dd'st'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM dd'nd'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM. dd'nd'").getParser(),	
					DateTimeFormat.forPattern("yyyy MMM dd'rd'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM. dd'rd'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM dd'th'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM. dd'th'").getParser(),
					
					DateTimeFormat.forPattern("MMM dd, yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'st', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'nd', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'rd', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'th', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd, yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'st', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'nd', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'rd', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'th', yyyy").getParser(),
					
					DateTimeFormat.forPattern("MMM.dd,yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'st',yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'nd',yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'rd',yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'th',yyyy").getParser(),	
					
					DateTimeFormat.forPattern("MMM.dd.yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'st'.yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'nd'.yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'rd'.yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'th'.yyyy").getParser(),					
					
					DateTimeFormat.forPattern("MMM-dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM-dd yyyy").getParser(),
					DateTimeFormat.forPattern("dd-MMM-yyyy").getParser(),
					DateTimeFormat.forPattern("dd.MMM.yyyy").getParser(),
					DateTimeFormat.forPattern("dd,MMM,yyyy").getParser(),
					DateTimeFormat.forPattern("dd.MMM.,yyyy").getParser(),
					DateTimeFormat.forPattern("dd. MMM.,yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM, dd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM, dd. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM, dd, yyyy").getParser(),					
					DateTimeFormat.forPattern("MMM, dd., yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd/yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd,yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd, yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd,yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd, yyyy").getParser(),
					DateTimeFormat.forPattern("dd. MMM. yyyy").getParser(),
					DateTimeFormat.forPattern("dd. MMM.yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM., yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM.,yyyy").getParser(),
					
					DateTimeFormat.forPattern("dd MMM, yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM,yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM.yyyy").getParser(),
					DateTimeFormat.forPattern("dd.MMM-yyyy").getParser(),
					DateTimeFormat.forPattern("dd.MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM-yyyy").getParser(),
					DateTimeFormat.forPattern("dd-MMM yyyy").getParser(),
					DateTimeFormat.forPattern("ddMMMyyyy").getParser(),
					
					DateTimeFormat.forPattern("MMM dd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'st' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'nd' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'rd' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'th' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'st' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'nd' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'rd' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'th' yyyy").getParser(),	
					DateTimeFormat.forPattern("MMMdd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd yyyy").getParser(),
					
					DateTimeFormat.forPattern("dd MMM, yyyy").getParser(),
					DateTimeFormat.forPattern("dd'st' MMM, yyyy").getParser(),
					DateTimeFormat.forPattern("dd'nd' MMM, yyyy").getParser(),
					DateTimeFormat.forPattern("dd'rd' MMM, yyyy").getParser(),
					DateTimeFormat.forPattern("dd'th MMM', yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM., yyyy").getParser(),
					DateTimeFormat.forPattern("dd'st' MMM., yyyy").getParser(),
					DateTimeFormat.forPattern("dd'nd' MMM., yyyy").getParser(),
					DateTimeFormat.forPattern("dd'rd' MMM., yyyy").getParser(),
					DateTimeFormat.forPattern("dd'th' MMM., yyyy").getParser(),
					
					DateTimeFormat.forPattern("dd MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd'st' MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd'nd' MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd'rd' MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd'th' MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM. yyyy").getParser(),
					DateTimeFormat.forPattern("dd'st' MMM. yyyy").getParser(),
					DateTimeFormat.forPattern("dd'nd' MMM. yyyy").getParser(),
					DateTimeFormat.forPattern("dd'rd' MMM. yyyy").getParser(),
					DateTimeFormat.forPattern("dd'th' MMM. yyyy").getParser(),					
					
					DateTimeFormat.forPattern("dd/MMM/yyyy").getParser(),
					DateTimeFormat.forPattern("dd/MMM yyyy").getParser(),
					DateTimeFormat.forPattern("MMM/dd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM/dd/yyyy").getParser(),
					
					DateTimeFormat.forPattern("MMM dd. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'st'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'nd'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'rd'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'th'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'st'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'nd'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'rd'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'th'. yyyy").getParser(),					
					DateTimeFormat.forPattern("MMM dd.yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd.yyyy").getParser(),

					DateTimeFormat.forPattern("MMM. dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'st'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'nd'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'rd'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'th'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'st'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'nd'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'rd'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'th'-yyyy").getParser(),
					
					DateTimeFormat.forPattern("yyyy-MMM-dd").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				String cleaned = cleanMonth(verbatimEventDate);
				
				try {
					// Specify English locale, or local default will be used
				    LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.ENGLISH));
				    resultDate =  parseDate.toString("yyyy-MM-dd");
				} catch (Exception e) {
					try {
						logger.debug(e.getMessage());
						LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.FRENCH));
						resultDate =  parseDate.toString("yyyy-MM-dd");
					} catch (Exception e1) { 
						try { 
							logger.debug(e1.getMessage());
							LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.ITALIAN));
							resultDate =  parseDate.toString("yyyy-MM-dd");
						} catch (Exception e2) {
							try { 
							logger.debug(e2.getMessage());
							LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.GERMAN));
							resultDate =  parseDate.toString("yyyy-MM-dd");
							} catch (Exception e3) { 
								try { 
								    logger.debug(e2.getMessage());
								    LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.forLanguageTag("es")));
								    resultDate =  parseDate.toString("yyyy-MM-dd");
								} catch (Exception e4) { 
									logger.debug(e2.getMessage());
									LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.forLanguageTag("pt")));
									resultDate =  parseDate.toString("yyyy-MM-dd");
								}
							}
						}
					}
				}	
				logger.debug(resultDate);
				result.put("resultState", "date");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}		
		if (result.size()==0) {
			if (verbatimEventDate.matches(".*[0-9]{4}.*")) { 
				try { 
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("MMM, yyyy").getParser(),
							DateTimeFormat.forPattern("MMM., yyyy").getParser(),
							DateTimeFormat.forPattern("MMM.,yyyy").getParser(),
							DateTimeFormat.forPattern("MMM.-yyyy").getParser(),
							DateTimeFormat.forPattern("MMM.yyyy").getParser(),
							DateTimeFormat.forPattern("MMM. yyyy").getParser(),
							DateTimeFormat.forPattern("MMM-yyyy").getParser(),
							DateTimeFormat.forPattern("MMM -yyyy").getParser(),
							DateTimeFormat.forPattern("MMM yyyy").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					String cleaned = cleanMonth(verbatimEventDate);
					// Strip off a trailing period after a final year
					if (cleaned.matches("^.*[0-9]{4}[.]$")) { 
						cleaned = cleaned.replaceAll("[.]$", "");
					}
					LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.ENGLISH));
					resultDate =  parseDate.toString("yyyy-MM");
					logger.debug(resultDate);
					result.put("resultState", "range");
					result.put("result",resultDate);
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}
		if (result.size()==0 &&  verbatimEventDate.matches("^[0-9]{4}([- ]+| to |[/ ]+)[0-9]{4}$")) {
			try { 
				String cleaned = verbatimEventDate.replace(" ", "");
				cleaned = cleaned.replace("-", "/");
				if (cleaned.matches("^[0-9]{4}to[0-9]{4}$")) { 
					int len = verbatimEventDate.length();
					int lastYear = len - 4;
					cleaned = verbatimEventDate.substring(0,4) + "/" + verbatimEventDate.substring(lastYear, len);
				}
				logger.debug(cleaned);
				Interval parseDate = Interval.parse(cleaned);
				logger.debug(parseDate);
				resultDate =  parseDate.getStart().toString("yyyy") + "/" + parseDate.getEnd().toString("yyyy");
				result.put("resultState", "range");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}	
		if (result.size()==0 && verbatimEventDate.matches("^[A-Za-z]+[-][A-Za-z]+[/ ][0-9]{4}$")) { 
			try { 
				String[] bits = verbatimEventDate.replace(" ", "/").split("-");
				if (bits!=null && bits.length==2) { 
					String year = verbatimEventDate.substring(verbatimEventDate.length()-4,verbatimEventDate.length());
					String startBit = bits[0]+"/"+year;
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("MMM/yyyy").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					LocalDate parseStartDate = LocalDate.parse(startBit,formatter.withLocale(Locale.ENGLISH));
					LocalDate parseEndDate = LocalDate.parse(bits[1],formatter.withLocale(Locale.ENGLISH));
					resultDate =  parseStartDate.toString("yyyy-MM") + "/" + parseEndDate.toString("yyyy-MM");
					logger.debug(resultDate);
					result.clear();
					result.put("resultState", "range");
					result.put("result",resultDate);
				}
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}
		if (result.size()==0) {
			try { 
				Interval parseDate = Interval.parse(verbatimEventDate);
				logger.debug(parseDate);
				resultDate =  parseDate.getStart().toString("yyyy-MM-dd") + "/" + parseDate.getEnd().toString("yyyy-MM-dd");
				result.put("resultState", "range");
				result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}	
		if (result.size()==0) {
			String cleaned = verbatimEventDate.trim();
			if (verbatimEventDate.matches("^[A-Za-z.]+[ ,]+[0-9]{1,2}-[0-9]{0,2}[ ,]+[0-9]{4}$")) { 
				cleaned = cleaned.replace("-", " to ");
			}
			if (cleaned.contains(" to ")) { 
				String[] bits = cleaned.split(" to ");
				String yearRegex = ".*([0-9]{4}).*";
				Matcher yearMatcher = Pattern.compile(yearRegex).matcher(cleaned);
				String monthRegex = "([A-Za-z.]+).*";
				Matcher monthMatcher = Pattern.compile(monthRegex).matcher(cleaned);				
				if (yearMatcher.matches() && monthMatcher.matches()) {
				    String year = yearMatcher.group(1);
				    String month = monthMatcher.group(1);
				        if (bits.length==2) { 
				        	if (!bits[0].contains(year)) { 
				        		bits[0] = bits[0] + " " + year;
				        	}
				        	if (!bits[1].contains(year)) { 
				        		bits[1] = bits[1] + " " + year;
				        	}
				        	if (!bits[1].contains(month)) { 
				        		bits[1] = month + " " + bits[1];
				        	}				        	
				        	Map<String,String> resultBit0 = DateUtils.extractDateFromVerbatim(bits[0]);
				        	if (resultBit0.size()>0 && resultBit0.get("resultState").equals("date")) {
				        	    Map<String,String> resultBit1 = DateUtils.extractDateFromVerbatim(bits[1]);
				        	    if (resultBit1.size()>0 && resultBit1.get("resultState").equals("date")) {
				    				result.put("resultState", "range");
				    				result.put("result",resultBit0.get("result")+ "/" + resultBit1.get("result"));
				        	    }
				        	}
				        	logger.debug(bits[0]);
				        	logger.debug(bits[1]);
				        }
				}
			}
		}		
		if (result!=null && result.size()>0) {
			Interval testExtract = DateUtils.extractDateInterval(result.get("result").toString());
			if(testExtract==null || testExtract.getStart().getYear()< yearsBeforeSuspect) { 
				result.put("resultState", "suspect");
				logger.debug(result.get("result"));
				logger.debug(testExtract);
			}
			if (!verbatimEventDate.matches(".*[0-9]{4}.*")) { 
				result.clear();
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
    				if (dateBits[1].length()==4) { 
    	                  result = new Interval(startDate,endDate.plusMonths(12).minusDays(1));
    	               } else if (dateBits[1].length()==7) { 
    	                  result = new Interval(startDate,endDate.plusMonths(1).minusDays(1));
    	               } else { 
    				      result = new Interval(startDate, endDate);
    	               }
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
    			ISODateTimeFormat.dateOptionalTimeParser().getParser(), 
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
   
    /**
     * Does eventDate match an ISO date that contains a time (including the instant of 
     * midnight (a time with all zero elements)). 
     * 
     * @param eventDate string to check for an ISO date with a time.
     * @return true if eventDate is an ISO date that includes a time, or if eventDate is an 
     * ISO date range either the start or end of which contains a time.  
     */
    public static boolean containsTime(String eventDate) {
    	boolean result = false;
    	if (!isEmpty(eventDate)) { 
    		if (eventDate.endsWith("UTC")) { eventDate = eventDate.replace("UTC", "Z"); } 
    		DateTimeParser[] parsers = { 
    				ISODateTimeFormat.dateHour().getParser(),
    				ISODateTimeFormat.dateTimeParser().getParser(),
    				ISODateTimeFormat.dateHourMinute().getParser(),
    				ISODateTimeFormat.dateHourMinuteSecond().getParser(),
    				ISODateTimeFormat.dateTime().getParser() 
    		};
    		DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    		if (eventDate.matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
    			try { 
    				LocalDate match = LocalDate.parse(eventDate, formatter);
    				result = true;
    				logger.debug(match);
    			} catch (Exception e) { 
    				// not a date with a time
    				logger.error(e.getMessage());
    			}    		
    		}
    		if (isRange(eventDate) && eventDate.contains("/") && !result) { 
    			String[] bits = eventDate.split("/");
    			if (bits!=null && bits.length>1) { 
    				// does either start or end date contain a time?
    				if (bits[0].matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
    					try { 
    						LocalDate match = LocalDate.parse(bits[0], formatter);
    						result = true;
    						logger.debug(match);
    					} catch (Exception e) { 
    						// not a date with a time
    						logger.error(e.getMessage());
    					}     
    				}
    				if (bits[1].matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
    					try { 
    						LocalDate match = LocalDate.parse(bits[1], formatter);
    						result = true;
    						logger.debug(match);
    					} catch (Exception e) { 
    						// not a date with a time
    						logger.error(e.getMessage());
    					}     	  
    				}
    			}
    		}
    	}
    	return result;
    }
    
    public static String extractZuluTime(String eventDate) {
    	String result = null;
    	if (!isEmpty(eventDate)) { 
    		if (eventDate.endsWith("UTC")) { eventDate = eventDate.replace("UTC", "Z"); } 
    		DateTimeParser[] parsers = { 
    				ISODateTimeFormat.dateHour().getParser(),
    				ISODateTimeFormat.dateTimeParser().getParser(),
    				ISODateTimeFormat.dateHourMinute().getParser(),
    				ISODateTimeFormat.dateHourMinuteSecond().getParser(),
    				ISODateTimeFormat.dateTime().getParser() 
    		};
    		DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    		if (eventDate.matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
    			try { 
    	    		result = instantToStringTime(Instant.parse(eventDate, formatter));
    				logger.debug(result);
    			} catch (Exception e) { 
    				// not a date with a time
    				logger.error(e.getMessage());
    			}    		
    		}
    		if (isRange(eventDate) && eventDate.contains("/") && result!=null) { 
    			String[] bits = eventDate.split("/");
    			if (bits!=null && bits.length>1) { 
    				// does either start or end date contain a time?
    				if (bits[0].matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
    					try { 
    	    				result = instantToStringTime(Instant.parse(bits[0], formatter));
    						logger.debug(result);
    					} catch (Exception e) { 
    						// not a date with a time
    						logger.error(e.getMessage());
    					}     
    				}
    				if (bits[1].matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+") && result!=null) { 
    					try { 
    	    				result = instantToStringTime(Instant.parse(bits[1], formatter));
    	    				logger.debug(result);
    					} catch (Exception e) { 
    						// not a date with a time
    						logger.error(e.getMessage());
    					}     	  
    				}
    			}
    		}
    	}
    	return result;
    }    
    
    /**
     * Given an instant, return the time within one day that it represents as a string.
     * 
     * @param instant to obtain time from.
     * @return string in the form hh:mm:ss.sssZ or an empty string if instant is null.  
     */
    protected static String instantToStringTime(Instant instant) {
    	String result = "";
    	if (instant!=null) { 
    		StringBuffer time = new StringBuffer();
    		time.append(String.format("%02d",instant.get(DateTimeFieldType.hourOfDay())));
    		time.append(":").append(String.format("%02d",instant.get(DateTimeFieldType.minuteOfHour())));
    		time.append(":").append(String.format("%02d",instant.get(DateTimeFieldType.secondOfMinute())));
    		time.append(".").append(String.format("%03d",instant.get(DateTimeFieldType.millisOfSecond())));
    		String timeZone = instant.getZone().getID();
    		if (timeZone.equals("UTC")) { 
    		    time.append("Z"); 
    		} else { 
    			time.append(timeZone);
    		}
    		result = time.toString();
    	}
    	return result;
    }
    
    

    public static String cleanMonth(String verbatimEventDate) {
    	String cleaned = verbatimEventDate;
    	if (!isEmpty(verbatimEventDate)) { 
    	cleaned = cleaned.replace("Sept.", "Sep.");
		cleaned = cleaned.replace("Sept ", "Sep. ");
		cleaned = cleaned.replace("Sept,", "Sep.,");
		cleaned = cleaned.replace("  ", " ").trim();
		cleaned = cleaned.replace(" ,", ",");
		cleaned = cleaned.replace(" - ", "-");
		cleaned = cleaned.replace("- ", "-");
		cleaned = cleaned.replace(" -", "-");
		// Strip off a trailing period after a final year
		if (cleaned.matches("^.*[0-9]{4}[.]$")) { 
			cleaned = cleaned.replaceAll("[.]$", "");
		}
		cleaned = cleaned.replace(".i.", ".January.");
		cleaned = cleaned.replace(" i ", " January ");
		cleaned = cleaned.replace(".ii.", ".February.");
		cleaned = cleaned.replace(" ii ", " February ");	
		cleaned = cleaned.replace(".v.", ".May.");
		cleaned = cleaned.replace(" v ", " May ");
		cleaned = cleaned.replace(".iv.", ".April.");
		cleaned = cleaned.replace(" iv ", " April ");	
		cleaned = cleaned.replace(".vi.", ".June.");
		cleaned = cleaned.replace(" vi ", " June ");	
		cleaned = cleaned.replace(".x.", ".October.");
		cleaned = cleaned.replace(" x ", " October ");
		cleaned = cleaned.replace(".ix.", ".September.");
		cleaned = cleaned.replace(" ix ", " September ");	
		cleaned = cleaned.replace(".xi.", ".November.");
		cleaned = cleaned.replace(" xi ", " November ");		
		cleaned = cleaned.replace(",i,", ".January.");
		cleaned = cleaned.replace("-i-", " January ");
		cleaned = cleaned.replace(",ii,", ".February.");
		cleaned = cleaned.replace("-ii-", " February ");	
		cleaned = cleaned.replace(",v,", ".May.");
		cleaned = cleaned.replace("-v-", " May ");
		cleaned = cleaned.replace(",iv,", ".April.");
		cleaned = cleaned.replace("-iv-", " April ");	
		cleaned = cleaned.replace(",vi,", ".June.");
		cleaned = cleaned.replace("-vi-", " June ");	
		cleaned = cleaned.replace(",x,", ".October.");
		cleaned = cleaned.replace("-x-", " October ");
		cleaned = cleaned.replace(",ix,", ".September.");
		cleaned = cleaned.replace("-ix-", " September ");	
		cleaned = cleaned.replace(",xi,", ".November.");
		cleaned = cleaned.replace("-xi-", " November ");				
		cleaned = cleaned.replace("XII", "December");
		cleaned = cleaned.replace("xii", "December");
		cleaned = cleaned.replace("XI", "November");
		cleaned = cleaned.replace("IX", "September");
		cleaned = cleaned.replace("X", "October");
		cleaned = cleaned.replace("VIII", "August");
		cleaned = cleaned.replace("viii", "August");
		cleaned = cleaned.replace("VII", "July");
		cleaned = cleaned.replace("vii", "July");
		cleaned = cleaned.replace("VI", "June");
		cleaned = cleaned.replace("IV", "April");
		cleaned = cleaned.replace("V", "May");
		cleaned = cleaned.replace("III", "March");
		cleaned = cleaned.replace("iii", "March");
		cleaned = cleaned.replace("II", "February");
		cleaned = cleaned.replace("I", "January");
		
		// Italian months are lower case, if capitalized, skip a step and go right to english.
		// Joda date time parsing, as used here is case sensitive for months.
		cleaned = cleaned.replace("Dicembre", "December");
		cleaned = cleaned.replace("Novembre", "November");
		cleaned = cleaned.replace("Ottobre", "October");
		cleaned = cleaned.replace("Settembre", "September");
		cleaned = cleaned.replace("Agosto", "August");
		cleaned = cleaned.replace("Luglio", "July");
		cleaned = cleaned.replace("Giugno", "June");
		cleaned = cleaned.replace("Maggio", "May");
		cleaned = cleaned.replace("Aprile", "April");
		cleaned = cleaned.replace("Marzo", "March");
		cleaned = cleaned.replace("Febbraio", "February");
		cleaned = cleaned.replace("Gennaio", "January");			
		// likewise french, also handle omitted accents
		cleaned = cleaned.replace("Janvier", "January");
		cleaned = cleaned.replace("Février", "February");
		cleaned = cleaned.replace("Fevrier", "February");
		cleaned = cleaned.replace("fevrier", "February");
		cleaned = cleaned.replace("Mars", "March");
		cleaned = cleaned.replace("Avril", "April");
		cleaned = cleaned.replace("Mai", "May");
		cleaned = cleaned.replace("Juin", "June");
		cleaned = cleaned.replace("Juillet", "July");
		cleaned = cleaned.replace("Août", "August");
		cleaned = cleaned.replace("Aout", "August");
		cleaned = cleaned.replace("aout", "August");
		cleaned = cleaned.replace("Septembre", "September");
		cleaned = cleaned.replace("Octobre", "October");
		cleaned = cleaned.replace("Novembre", "November");
		cleaned = cleaned.replace("Décembre", "December");
		cleaned = cleaned.replace("Decembre", "December");
		cleaned = cleaned.replace("decembre", "December");
		// likewise spanish
		cleaned = cleaned.replace("Enero", "January");
		cleaned = cleaned.replace("Febrero", "February");
		cleaned = cleaned.replace("Marzo", "March");
		cleaned = cleaned.replace("Abril", "April");
		cleaned = cleaned.replace("Mayo", "May");
		cleaned = cleaned.replace("Junio", "June");
		cleaned = cleaned.replace("Julio", "July");
		cleaned = cleaned.replace("Agosto", "August");
		cleaned = cleaned.replace("Septiembre", "September");
		cleaned = cleaned.replace("Setiembre", "September");  // alternative spelling
		cleaned = cleaned.replace("setiembre", "September");
		cleaned = cleaned.replace("Octubre", "October");
		cleaned = cleaned.replace("Noviembre", "November");
		cleaned = cleaned.replace("Diciembre", "December");
    	}
		return cleaned;
    }
  
    /**
     * Run from the command line, arguments -f to specify a file, -m to show matches.
     * 
     * @param args "-f filename" to check a file containing a list of dates, one per line.
     *    "-m" to show matched dates and their interpretations otherwise lists non-matched lines.  
     */
    public static void main(String[] args) { 
        try {
        	URL datesURI = DateUtils.class.getResource("/org.filteredpush.kuration.services/example_dates.csv");
        	File datesFile = new File(datesURI.toURI());
        	if (args[0]!=null && args[0].toLowerCase().equals("-f")) {
        		if (args[1]!=null) { 
        			datesFile = new File(args[1]);
        		}
            }
        	boolean showMatches = false;
        	for (int i=0; i<args.length; i++) {
        		if (args[i].equals("-m")) { showMatches = true; } 
        	}
			BufferedReader reader = new BufferedReader(new FileReader(datesFile));
			String line = null;
			int unmatched = 0;
			int matched = 0;
			while ((line=reader.readLine())!=null) {
				Map<String,String> result = DateUtils.extractDateFromVerbatim(line);
				if (result==null || result.size()==0) {
					if (!showMatches) {
					   System.out.println(line);
					}
					unmatched++;
				} else { 
					matched++;
				   if (showMatches) { 
					   // if (result.get("resultState").equals("suspect")) 
					   System.out.println(line + "\t" + result.get("resultState") + "\t" + result.get("result"));
				   }
				}
			}
			reader.close();
			System.out.println("Unmatched lines: " + unmatched);
			System.out.println("Matched lines: " + matched);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());;
			System.out.println(e.getMessage());
		} catch (URISyntaxException e) {
			System.out.println(e.getMessage());
		}
        
    }
    
}
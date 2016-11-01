/**  DateValidator.java
 *
 * Copyright 2016 President and Fellows of Harvard College
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
package org.filteredpush.kuration.validators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.filteredpush.kuration.provenance.BaseRecord;
import org.filteredpush.kuration.provenance.CurationStatus;
import org.filteredpush.kuration.provenance.NamedContext;
import org.kurator.akka.data.CurationStep;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.DateUtils;

import static org.filteredpush.kuration.provenance.CurationStatus.*;

/**
 * @author mole
 *
 */
public class DateValidator {
	
	private static final Log logger = LogFactory.getLog(DateValidator.class);

	/**
	 * 
	 * @param eventDate
	 * @param year
	 * @param month
	 * @param day
	 * @param dayOfYear
	 * @param eventTime
	 * @return
	 */
	public static BaseRecord validateEventConsistencyWithContext(String eventDate, String year, String month,
                                                                 String day, String startDayOfYear, String endDayOfYear,
                                                                 String eventTime, String verbatimEventDate) {
		Map<String,String> initialValues = new HashMap<String,String>();
		initialValues.put("eventDate", eventDate);
		initialValues.put("year", year);
		initialValues.put("month", month);
		initialValues.put("day", day);
		initialValues.put("startDayOfYear", startDayOfYear);
		initialValues.put("eventTime", eventTime);
		initialValues.put("verbatimEventDate", verbatimEventDate);

        BaseRecord result = new BaseRecord(initialValues);

		String scopeTestValue = null;

		if (DateUtils.isEmpty(eventDate)) {
            result.update("dwc:eventDate does not contain a value.");
			if (DateUtils.isEmpty(year) && DateUtils.isEmpty(verbatimEventDate) ) {
				result.update(UNABLE_DETERMINE_VALIDITY,
                        "dwc:year and dwc:verbatimEventDate do not contain values.",
                        "Event does not specify an identifiable date or date range.");
			} else {
				String newEventDate = DateUtils.createEventDateFromParts(verbatimEventDate, startDayOfYear,
                        endDayOfYear, year, month, day);

				if (newEventDate!=null) {
					result.update("eventDate", newEventDate, FILLED_IN);
					scopeTestValue = newEventDate;
				} else {
					result.update(UNABLE_CURATE, "Unable to construct an event date from atomic parts.");
				}
			}
		} else {
            NamedContext isEventDateConsistent = new NamedContext("isEventDateConsistent");

			if (DateUtils.isConsistent(eventDate, startDayOfYear, endDayOfYear, year, month, day)) {
				result.update(isEventDateConsistent, CORRECT, "dwc:eventDate is consistent with atomic parts");
				scopeTestValue = eventDate;
			} else {
				result.update(isEventDateConsistent, UNABLE_CURATE,
                        "dwc:eventDate is not consistent with atomic parts (" + eventDate + " <> [" + startDayOfYear +
                                "][" + endDayOfYear + "][" + year + "][" + month + "][" + day + "])");
			}
		}

		if (!DateUtils.isEmpty(verbatimEventDate)) {
			String extractedVerbatimDate = DateUtils.createEventDateFromParts(verbatimEventDate, null, null, null,
                    null, null);

			if (eventDate.trim().equals(extractedVerbatimDate.trim())) {
				result.update("dwc:verbatimEventDate parses to the same value as dwc:eventDate.");
			} else {
				result.update("dwc:verbatimEventDate does not parse to the same value as dwc:eventDate " +
                        "(["+ extractedVerbatimDate + "]<>["+ eventDate +"]). ");
			}
		}

		if (!DateUtils.isEmpty(eventTime)) {
			if (DateUtils.containsTime(eventDate)) {
				if (!DateUtils.extractZuluTime(eventDate).equals(DateUtils.extractZuluTime("1970-01-10T" + eventTime))) {
					result.update(UNABLE_CURATE, "dwc:eventDate is not consistent with eventTime");
			    } else {
				    logger.debug(DateUtils.extractZuluTime(eventDate).equals(DateUtils.extractZuluTime("1970-01-10T" + eventTime)));
			    }
			}
		}

		if (scopeTestValue!=null) {
			if (DateUtils.specificToDay(scopeTestValue)) {
				result.update("dwc:eventDate specifies a date to a day or less.");
			} else {
				if (DateUtils.specificToMonthScale(scopeTestValue)) {
				    result.update("dwc:eventDate specifies a date range of between a day and a month.");
				} else {
					if (DateUtils.specificToYearScale(scopeTestValue)) {
						result.update("dwc:eventDate specifies a date range of between a month and a year.");
					} else {
						if (DateUtils.specificToDecadeScale(scopeTestValue)) {
						    result.update("dwc:eventDate specifies a date range of between a year and a decade.");
						} else {
						    result.update("dwc:eventDate specifies a date range spanning more than a decade.");
						}
					}
				}
			}
		}


		return result;
	}

	/**
	 *
	 * @param eventDate
	 * @param year
	 * @param month
	 * @param day
	 * @param dayOfYear
	 * @param eventTime
	 * @return
	 */
	public static CurationStep validateEventConsistency(String eventDate, String year, String month, String day, String startDayOfYear, String endDayOfYear, String eventTime, String verbatimEventDate) { 
		Map<String,String> initialValues = new HashMap<String,String>();
		Map<String,String> curatedValues = new HashMap<String,String>();
		initialValues.put("eventDate", eventDate);
		initialValues.put("year", year);
		initialValues.put("month", month);
		initialValues.put("day", day);
		initialValues.put("startDayOfYear", startDayOfYear);
		initialValues.put("eventTime", eventTime);
		initialValues.put("verbatimEventDate", verbatimEventDate);
		String specification = "";
		CurationStep result = new CurationStep(specification, initialValues);
		
		String scopeTestValue = null;
		
/*
        // example alternative organization splitting more clearly into individual tests
		if (DateUtils.isEmpty(eventDate)) {
			result.addCurationComment("dwc:eventDate does not contain a value.");
		}
		if (DateUtils.isEmpty(year) && DateUtils.isEmpty(verbatimEventDate) ) {
				result.addCurationComment("dwc:year and dwc:verbatimEventDate do not contain values.");
		}
		if (DateUtils.isEmpty(eventDate) && DateUtils.isEmpty(year) && DateUtils.isEmpty(verbatimEventDate) ) {
				result.addCurationComment("Event does not specify an identifiable date or date range.");
				result.addCurationState(CurationComment.UNABLE_DETERMINE_VALIDITY);
		}
*/

		if (DateUtils.isEmpty(eventDate)) {
			result.addCurationComment("dwc:eventDate does not contain a value.");
			if (DateUtils.isEmpty(year) && DateUtils.isEmpty(verbatimEventDate) ) { 
				result.addCurationComment("dwc:year and dwc:verbatimEventDate do not contain values.");
				result.addCurationComment("Event does not specify an identifiable date or date range.");
				result.addCurationState(CurationComment.UNABLE_DETERMINE_VALIDITY);
			} else { 
				String newEventDate = DateUtils.createEventDateFromParts(verbatimEventDate, startDayOfYear, endDayOfYear, year, month, day);
				if (newEventDate!=null) { 
					result.addCurationState(CurationComment.FILLED_IN);
					curatedValues.put("eventDate", newEventDate);
					scopeTestValue = newEventDate;
				} else { 
					result.addCurationComment("Unable to construct an event date from atomic parts.");
					result.addCurationState(CurationComment.UNABLE_CURATED);
				}
			}
		} else { 
			if (DateUtils.isConsistent(eventDate, startDayOfYear, endDayOfYear, year, month, day)) { 
				result.addCurationComment("dwc:eventDate is consistent with atomic parts");
				result.addCurationState(CurationComment.CORRECT);
				scopeTestValue = eventDate;
			} else {
				result.addCurationComment("dwc:eventDate is not consistent with atomic parts (" + eventDate + " <> [" + startDayOfYear + "][" + endDayOfYear + "][" + year + "][" + month + "][" + day + "])");
				result.addCurationState(CurationComment.UNABLE_CURATED);
			}
		}
		
		if (!DateUtils.isEmpty(verbatimEventDate)) { 
			String extractedVerbatimDate = DateUtils.createEventDateFromParts(verbatimEventDate, null, null, null, null, null);
			if (!DateUtils.isEmpty(eventDate)) {
				if (eventDate.trim().equals(extractedVerbatimDate.trim())) {
					result.addCurationComment("dwc:verbatimEventDate parses to the same value as dwc:eventDate.");
				} else {
					result.addCurationComment("dwc:verbatimEventDate does not parse to the same value as dwc:eventDate (["+ extractedVerbatimDate + "]<>["+ eventDate +"]). ");
			    }
			} else {
				result.addCurationComment("dwc:verbatimEventDate has a value while dwc:eventDate does not (["+ extractedVerbatimDate + "]). ");
			}
		}
		
		if (!DateUtils.isEmpty(eventTime)) {
			if (DateUtils.containsTime(eventDate)) { 
				if (!DateUtils.extractZuluTime(eventDate).equals(DateUtils.extractZuluTime("1970-01-10T" + eventTime))) { 
					result.addCurationComment("dwc:eventDate is not consistent with eventTime");
					result.replaceCurationStates(CurationComment.UNABLE_CURATED);
			    } else { 
				    logger.debug(DateUtils.extractZuluTime(eventDate).equals(DateUtils.extractZuluTime("1970-01-10T" + eventTime)));
			    }
			}
		}
		
		if (scopeTestValue!=null) { 
			if (DateUtils.specificToDay(scopeTestValue)) { 
				result.addCurationComment("dwc:eventDate specifies a date to a day or less.");
			} else { 
				if (DateUtils.specificToMonthScale(scopeTestValue)) { 
				    result.addCurationComment("dwc:eventDate specifies a date range of between a day and a month.");
				} else { 
					if (DateUtils.specificToYearScale(scopeTestValue)) { 
						result.addCurationComment("dwc:eventDate specifies a date range of between a month and a year.");
					} else { 
						if (DateUtils.specificToDecadeScale(scopeTestValue)) { 
						    result.addCurationComment("dwc:eventDate specifies a date range of between a year and a decade.");
						} else { 
						    result.addCurationComment("dwc:eventDate specifies a date range spanning more than a decade.");
						}
					}
				}   
			}
		}
		
		
		return result;
	}
	
}

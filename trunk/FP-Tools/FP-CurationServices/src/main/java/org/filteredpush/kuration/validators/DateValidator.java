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
import java.util.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.filteredpush.kuration.util.CurationException;
import org.kurator.akka.data.CurationStep;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.DateUtils;
import org.kurator.data.provenance.BaseRecord;
import org.kurator.data.provenance.CurationStatus;
import org.kurator.data.provenance.GlobalContext;
import org.kurator.data.provenance.NamedContext;


/**
 * @author mole
 *
 */
public class DateValidator {
	
	private static final Log logger = LogFactory.getLog(DateValidator.class);

	private static String getActorName() {
		return "Event date validator";
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

		GlobalContext globalContext = new GlobalContext(DateValidator.class.getSimpleName(), DateValidator.getActorName());
        BaseRecord result = new BaseRecord(initialValues, globalContext);
		String scopeTestValue = null;

        NamedContext eventDateIsNotEmpty = new NamedContext("eventDateIsNotEmpty",
                Collections.singletonList("eventDate"));

		if (DateUtils.isEmpty(eventDate)) {
            result.update(eventDateIsNotEmpty, CurationStatus.NOT_COMPLETE,
                    "dwc:eventDate does not contain a value.");

			if (DateUtils.isEmpty(year) && DateUtils.isEmpty(verbatimEventDate) ) {
				result.update(eventDateIsNotEmpty, CurationStatus.DATA_PREREQUISITES_NOT_MET,
                        "dwc:year and dwc:verbatimEventDate do not contain values.",
                        "Event does not specify an identifiable date or date range.");
			} else {
                NamedContext eventDateFromAtomicParts = new NamedContext("eventDateFromAtomicParts",
                        Collections.singletonList("eventDate"), Arrays.asList("verbatimEventDate", "startDayOfYear",
                        "endDayOfYear", "year", "month", "day"));

				String newEventDate = DateUtils.createEventDateFromParts(verbatimEventDate, startDayOfYear,
                        endDayOfYear, year, month, day);

				if (newEventDate!=null) {
					result.update(eventDateFromAtomicParts, "eventDate", newEventDate, CurationStatus.FILLED_IN,
							"Constructed event date from atomic parts.");
					scopeTestValue = newEventDate;
				} else {
					result.update(eventDateFromAtomicParts, CurationStatus.DATA_PREREQUISITES_NOT_MET,
                            "Unable to construct an event date from atomic parts.");
				}
			}
		} else {
            result.update(eventDateIsNotEmpty, CurationStatus.COMPLETE,
                    "dwc:eventDate does contain a value.");

            NamedContext eventDateIsConsistent = new NamedContext("eventDateIsConsistent",
                    Collections.singletonList("eventDate"), Arrays.asList("startDayOfYear", "endDayOfYear", "year",
                    "month", "day"));

			if (DateUtils.isConsistent(eventDate, startDayOfYear, endDayOfYear, year, month, day)) {
				result.update(eventDateIsConsistent, CurationStatus.COMPLIANT, "dwc:eventDate is consistent with atomic parts");
				scopeTestValue = eventDate;
			} else {
				result.update(eventDateIsConsistent, CurationStatus.NOT_COMPLIANT,
                        "dwc:eventDate is not consistent with atomic parts (" + eventDate + " <> [" + startDayOfYear +
                                "][" + endDayOfYear + "][" + year + "][" + month + "][" + day + "])");
			}
		}

		// TODO: Add internal prerequisites not met cases
		if (!DateUtils.isEmpty(verbatimEventDate)) {
            NamedContext checkVerbatimEventDate = new NamedContext("checkVerbatimEventDate",
                    Collections.singletonList("eventDate"), Arrays.asList("verbatimEventDate"));

			String extractedVerbatimDate = DateUtils.createEventDateFromParts(verbatimEventDate, null, null, null,
                    null, null);

			if (eventDate.trim().equals(extractedVerbatimDate.trim())) {
				result.update(checkVerbatimEventDate, CurationStatus.COMPLIANT,
                        "dwc:verbatimEventDate parses to the same value as dwc:eventDate.");
			} else {
				result.update(checkVerbatimEventDate, CurationStatus.NOT_COMPLIANT,
                        "dwc:verbatimEventDate does not parse to the same value as dwc:eventDate " +
                        "(["+ extractedVerbatimDate + "]<>["+ eventDate +"]). ");
			}
		}

		if (!DateUtils.isEmpty(eventTime)) {
            NamedContext eventDateContainsEventTime = new NamedContext("eventDateContainsEventTime",
                    Collections.singletonList("eventDate"), Collections.singletonList("eventTime")); // Measure

			if (DateUtils.containsTime(eventDate)) {
                result.update(eventDateContainsEventTime, "eventTime", eventTime,
                        CurationStatus.COMPLETE, "dwc:eventDate contains eventTime");

                NamedContext eventDateIsConsistentWithEventTime = new NamedContext("eventDateIsConsistentWithEventTime",
                        Collections.singletonList("eventDate"), Collections.singletonList("eventTime"));

				if (!DateUtils.extractZuluTime(eventDate).equals(DateUtils.extractZuluTime("1970-01-10T" + eventTime))) {
					result.update(eventDateIsConsistentWithEventTime, CurationStatus.NOT_COMPLIANT,
                            "dwc:eventDate is not consistent with eventTime");
			    } else {
                    result.update(eventDateIsConsistentWithEventTime, CurationStatus.COMPLIANT,
                            "dwc:eventDate is consistent with eventTime",
                            "([" + eventTime + "]<>[" + eventDate + "]");

				    logger.debug(DateUtils.extractZuluTime(eventDate).equals(DateUtils.extractZuluTime("1970-01-10T" + eventTime)));
			    }
			} else {
                result.update(eventDateContainsEventTime, CurationStatus.NOT_COMPLETE,
                        "dwc:eventDate does not contain eventTime");
            }
		}

        NamedContext durationInSeconds = new NamedContext("durationInSeconds",
                Collections.singletonList("eventDate"));

        // Assert a measure (number of seconds in this event date)
		long duration = DateUtils.measureDurationSeconds(eventDate);

		if (duration > 0) {
			result.update(durationInSeconds, "durationInSeconds",
					Long.toString(duration),
					CurationStatus.COMPLETE,
					"Number of seconds in this dwc:eventDate");
		} else {
			result.update(durationInSeconds, "durationInSeconds",
					Long.toString(duration),
					CurationStatus.NOT_COMPLETE,
					"dwc:eventDate does not contain an interval of time");
		}


		if (scopeTestValue!=null) {

            // assert a set of validations (is event date duration within sets of specified durations)
            NamedContext eventDateSpecificToDay = new NamedContext("eventDateSpecificToDay",
                    Collections.singletonList("eventDate"));

			if (DateUtils.specificToDay(scopeTestValue)) {
				result.update(eventDateSpecificToDay, CurationStatus.COMPLIANT,
                        "dwc:eventDate specifies a date to a day or less.");
			} else {
                result.update(eventDateSpecificToDay, CurationStatus.NOT_COMPLIANT,
                        "dwc:eventDate does not specify a date to a day or less.");
            }

            NamedContext specificToMonthScale = new NamedContext("eventDateSpecificToMonthScale",
                    Collections.singletonList("eventDate"));

            if (DateUtils.specificToMonthScale(scopeTestValue)) {
                result.update(specificToMonthScale, CurationStatus.COMPLIANT,
                        "dwc:eventDate specifies a date range of between a day and a month.");
            } else {
                result.update(specificToMonthScale, CurationStatus.NOT_COMPLIANT,
                        "dwc:eventDate does not specify a date range of between a day and a month.");
            }

            NamedContext specificToYearScale = new NamedContext("eventDateSpecificToYearScale",
                    Collections.singletonList("eventDate"));

            if (DateUtils.specificToYearScale(scopeTestValue)) {
                result.update(specificToYearScale, CurationStatus.COMPLIANT,
                        "dwc:eventDate specifies a date range of between a month and a year.");
            } else {
                result.update(specificToYearScale, CurationStatus.NOT_COMPLIANT,
                        "dwc:eventDate does not specify a date range of between a month and a year.");
            }

            NamedContext specificToDecadeScale = new NamedContext("eventDateSpecificToDecadeScale",
                    Collections.singletonList("eventDate"));

            if (DateUtils.specificToDecadeScale(scopeTestValue)) {
				result.update(specificToDecadeScale, CurationStatus.COMPLIANT,
                        "dwc:eventDate specifies a date range of between a year and a decade.");
			} else {
                result.update(specificToDecadeScale, CurationStatus.NOT_COMPLIANT,
                        "dwc:eventDate does not specify a date range of between a year and a decade.");
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
		

        // example alternative organization splitting more clearly into individual tests
		if (DateUtils.isEmpty(eventDate)) {
			result.addCurationComment("dwc:eventDate does not contain a value.");
		} else {
            result.addCurationComment("dwc:eventDate does contain a value.");
		}

		if (DateUtils.isEmpty(year) && DateUtils.isEmpty(verbatimEventDate) ) {
				result.addCurationComment("dwc:year and dwc:verbatimEventDate do not contain values.");
		}
		if (DateUtils.isEmpty(eventDate) && DateUtils.isEmpty(year) && DateUtils.isEmpty(verbatimEventDate) ) {
				result.addCurationComment("Event does not specify an identifiable date or date range.");
				result.addCurationState(CurationComment.UNABLE_DETERMINE_VALIDITY);
		}

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

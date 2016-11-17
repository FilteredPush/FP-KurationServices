package org.filteredpush.kuration.validators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.data.DateFragment;
import org.datakurator.data.Update;
import org.datakurator.data.annotations.*;
import org.datakurator.data.annotations.CurationStage;
import org.datakurator.data.annotations.GlobalContext;
import org.datakurator.data.annotations.NamedContext;
import org.datakurator.data.assertions.Improvement;
import org.datakurator.data.assertions.Measure;
import org.datakurator.data.assertions.Validation;
import org.datakurator.data.provenance.*;;
import org.filteredpush.kuration.util.DateUtils;

@GlobalContext(actorName = "Event date validator")
public class AnnotatedDateValidator implements Curation {
    private static final Log logger = LogFactory.getLog(DateValidator.class);

    @CurationObject
    private DateFragment record;

    public void setRecord(DateFragment record) {
        this.record = record;
    }

    @PreEnhancement
    public void preEnhancement() {
        checkEventDateCompleteness();
        checkContainsEventTime();
        checkDurationInSeconds();

        validateConsistencyWithAtomicParts();
        validateVerbatimEventDate();
        validateConsistencyWithEventTime();
        validateSpecificToDay();
        validateSpecificToMonth();
        validateSpecificToYear();
        validateSpecificToDecade();
    }

    @Enhancement
    public void enhancement() {
        if (DateUtils.isEmpty(record.getEventDate())) {
            fillInFromAtomicParts();
        }
    }

    @PostEnhancement
    public void postEnhancement() {
        checkEventDateCompleteness();
        checkContainsEventTime();
        checkDurationInSeconds();

        validateConsistencyWithAtomicParts();
        validateVerbatimEventDate();
        validateConsistencyWithEventTime();
        validateSpecificToDay();
        validateSpecificToMonth();
        validateSpecificToYear();
        validateSpecificToDecade();
    }

	/*
	 * Measures
	 *
	 */

	@FieldsActedUpon("eventDate")
	@FieldsConsulted("eventTime")
    @NamedContext("eventDateContainsEventTime")
    private void checkContainsEventTime() {

        Measure m = record.assertMeasure();

        if (!DateUtils.isEmpty(record.getEventDate())) {
            if (DateUtils.containsTime(record.getEventDate())) {
                m.complete("dwc:eventDate contains eventTime");
            } else {
                m.incomplete("dwc:eventDate does not contain eventTime");
            }
        } else {
            m.prereqUnmet("dwc:eventDate does not contain a value.");
        }

    }

    @FieldsActedUpon("eventDate")
    @NamedContext("eventDateIsNotEmpty")
    private void checkEventDateCompleteness() {

        Measure m = record.assertMeasure();

        if (DateUtils.isEmpty(record.getEventDate())) {
            m.incomplete("dwc:eventDate does not contain a value.");
        } else {
            m.complete("dwc:eventDate does contain a value.");
        }

    }

    @FieldsActedUpon("eventDate")
    @NamedContext("durationInSeconds")
    private void checkDurationInSeconds() {

        Measure m = record.assertMeasure();

        // Assert a measure (number of seconds in this event date)
        long duration = DateUtils.measureDurationSeconds(record.getEventDate());

        if (!DateUtils.isEmpty(record.getEventDate())) {
            if (duration > 0) {
                m.complete(new Update("durationInSeconds", Long.toString(duration)), "Number of seconds in this dwc:eventDate");
            } else {
                m.incomplete(new Update("durationInSeconds", Long.toString(duration)),
                        "dwc:eventDate does not contain an interval of time");
            }
        } else {
            m.prereqUnmet("dwc:eventDate does not contain a value.");
        }

    }

	/*
	 * Validations
	 *
	 */

    @FieldsActedUpon("eventDate")
    @NamedContext("eventDateSpecificToDecadeScale")
    private void validateSpecificToDecade() {

        Validation v = record.assertValidation();

        if (!DateUtils.isEmpty(record.getEventDate())) {
            if (DateUtils.specificToDecadeScale(record.getScopeTestValue())) {
                v.compliant("dwc:eventDate specifies a duration of a decade or less.");
            } else {
                v.nonCompliant("dwc:eventDate does not specify a duration of a decade or less.");
            }
        } else {
            v.prereqUnmet("dwc:eventDate does not contain a value.");
        }

    }

    @FieldsActedUpon("eventDate")
    @NamedContext("eventDateSpecificToYearScale")
    private void validateSpecificToYear() {

        Validation v = record.assertValidation();

        if (!DateUtils.isEmpty(record.getEventDate())) {
            if (DateUtils.specificToYearScale(record.getScopeTestValue())) {
                v.compliant("dwc:eventDate specifies a duration of a year or less.");
            } else {
                v.nonCompliant("dwc:eventDate does not specify a duration of a year or less.");
            }
        } else {
            v.prereqUnmet("dwc:eventDate does not contain a value.");
        }

    }

    @FieldsActedUpon("eventDate")
    @NamedContext("eventDateSpecificToMonthScale")
    private void validateSpecificToMonth() {

        Validation v = record.assertValidation();

        if (!DateUtils.isEmpty(record.getEventDate())) {
            if (DateUtils.specificToMonthScale(record.getScopeTestValue())) {
                v.compliant("dwc:eventDate specifies a duration of a month or less.");
            } else {
                v.nonCompliant("dwc:eventDate does not specify a duration of a month or less.");
            }
        } else {
            v.prereqUnmet("dwc:eventDate does not contain a value.");
        }

    }

    @FieldsActedUpon("eventDate")
    @NamedContext("eventDateSpecificToDay")
    private void validateSpecificToDay() {

        Validation v = record.assertValidation();

        if (!DateUtils.isEmpty(record.getEventDate())) {
            if (DateUtils.specificToDay(record.getScopeTestValue())) {
                v.compliant("dwc:eventDate specifies a duration of a day or less.");
            } else {
                v.nonCompliant("dwc:eventDate does not specify a duration of a day or less.");
            }
        } else {
            v.prereqUnmet("dwc:eventDate does not contain a value.");
        }

    }

    @FieldsActedUpon("eventDate")
    @FieldsConsulted("eventTime")
    @NamedContext("eventDateIsConsistentWithEventTime")
    private void validateConsistencyWithEventTime() {

        Validation v = record.assertValidation();

        if (!DateUtils.isEmpty(record.getEventDate())) {
            if (!DateUtils.isEmpty(record.getEventTime())) {
                boolean isConsistent = DateUtils.extractZuluTime(record.getEventDate()).equals(
                        DateUtils.extractZuluTime("1970-01-10T" + record.getEventTime()));

                if (!isConsistent) {
                    v.nonCompliant("dwc:eventDate is not consistent with eventTime");
                } else {
                    v.compliant("dwc:eventDate is consistent with eventTime",
                            "([" + record.getEventTime() + "]<>[" + record.getEventDate() + "]");
                }
            } else {
                v.prereqUnmet("dwc:eventTime does not contain a value.");
            }
        } else {
            v.prereqUnmet("dwc:eventDate does not contain a value.");
        }

    }

    @FieldsActedUpon("eventDate")
    @FieldsConsulted("verbatimEventDate")
    @NamedContext("checkVerbatimEventDate")
    private void validateVerbatimEventDate() {

        Validation v = record.assertValidation();

        if (!DateUtils.isEmpty(record.getEventDate())) {
            if (!DateUtils.isEmpty(record.getVerbatimEventDate())) {
                String extractedVerbatimDate = DateUtils.createEventDateFromParts(record.getVerbatimEventDate(), null, null,
                        null, null, null);

                if (record.getEventDate().trim().equals(extractedVerbatimDate.trim())) {
                    v.compliant("dwc:verbatimEventDate parses to the same value as dwc:eventDate.");
                } else {
                    v.nonCompliant("dwc:verbatimEventDate does not parse to the same value as dwc:eventDate " +
                                    "([" + extractedVerbatimDate + "]<>[" + record.getEventDate() + "]). ");
                }
            } else {
                v.prereqUnmet("dwc:verbatimEventDate does not contain a value.");
            }
        } else {
            v.prereqUnmet("dwc:eventDate does not contain a value.");
        }

    }

    @FieldsActedUpon("eventDate")
    @FieldsConsulted({"startDayOfYear", "endDayOfYear", "year", "month", "day"})
    @NamedContext("eventDateIsConsistent")
    private void validateConsistencyWithAtomicParts() {

        Validation v = record.assertValidation();

        if (!DateUtils.isEmpty(record.getEventDate())) {
            if (DateUtils.isConsistent(record.getEventDate(), record.getStartDayOfYear(), record.getEndDayOfYear(),
                    record.getYear(), record.getMonth(), record.getDay())) {
                v.compliant("dwc:eventDate is consistent with atomic parts");
                record.setScopeTestValue(record.getEventDate());
            } else {
                v.nonCompliant("dwc:eventDate is not consistent with atomic parts (${context.fieldsActedUpon} <>" +
                                " ${context.fieldsConsulted})");
            }
        } else {
            v.prereqUnmet("dwc:eventDate does not contain a value.");
        }

    }

	/*
	 * Improvements
	 *
	 */

    @FieldsActedUpon("eventDate")
    @FieldsConsulted({"verbatimEventDate", "startDayOfYear", "endDayOfYear", "year", "month", "day"})
    @NamedContext("eventDateFromAtomicParts")
    private void fillInFromAtomicParts() {

        Improvement i = record.assertImprovement();

        if (DateUtils.isEmpty(record.getYear()) && DateUtils.isEmpty(record.getVerbatimEventDate()) ) {
            i.prereqUnmet("dwc:year and dwc:verbatimEventDate do not contain values.",
                    "Event does not specify an identifiable date or date range.");
        } else {
            String newEventDate = DateUtils.createEventDateFromParts(record.getVerbatimEventDate(),
                    record.getStartDayOfYear(), record.getEndDayOfYear(),
                    record.getYear(), record.getMonth(), record.getDay());

            if (newEventDate!=null) {
                i.fillIn(new Update("eventDate", newEventDate), "Constructed event date from atomic parts.");
                record.setScopeTestValue(newEventDate);
            } else {
                i.prereqUnmet("Unable to construct an event date from atomic parts.");
            }
        }

    }
}

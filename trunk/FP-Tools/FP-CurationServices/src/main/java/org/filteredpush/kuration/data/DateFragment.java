package org.filteredpush.kuration.data;

import org.kurator.data.provenance.BaseRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lowery on 11/8/16.
 */
public class DateFragment extends BaseRecord {
    private String scopeTestValue;

    public DateFragment(String eventDate, String year, String month,
                        String day, String startDayOfYear, String endDayOfYear,
                        String eventTime, String verbatimEventDate) {
        Map<String, String> initialValues = new HashMap<>();

        initialValues.put("eventDate", eventDate);
        initialValues.put("year", year);
        initialValues.put("month", month);
        initialValues.put("day", day);
        initialValues.put("startDayOfYear", startDayOfYear);
        initialValues.put("endDayOfYear", endDayOfYear);
        initialValues.put("eventTime", eventTime);
        initialValues.put("verbatimEventDate", verbatimEventDate);

        setInitialValues(initialValues);
    }

    public String getEventDate() {
        return get("eventDate");
    }

    public String getYear() {
        return get("year");
    }

    public String getMonth() {
        return get("month");
    }

    public String getDay() {
        return get("day");
    }

    public String getStartDayOfYear() {
        return get("startDayOfYear");
    }

    public String getEndDayOfYear() {
        return get("endDayOfYear");
    }

    public String getEventTime() {
        return get("eventTime");
    }

    public String getVerbatimEventDate() {
        return get("verbatimEventDate");
    }

    public String getScopeTestValue() {
        return scopeTestValue;
    }

    public void setScopeTestValue(String scopeTestValue) {
        this.scopeTestValue = scopeTestValue;
    }

}

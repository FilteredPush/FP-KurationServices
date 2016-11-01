/**  BaseRecord.java
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

package org.filteredpush.kuration.provenance;

import java.util.*;

public class BaseRecord {
    private Map<String, String> initialValues;
    private Map<String, String> curatedValues;

    private Stack<CurationStep> updateHistory = new Stack<>();

    private CurationStatus currentStatus;

    public BaseRecord(Map<String, String> initialValues) {
        this.initialValues = initialValues;
        this.curatedValues = initialValues;
    }

    public void update(NamedContext context, String comment) {
        updateHistory.push(new CurationStep(curatedValues, context, comment));
    }

    public void update(NamedContext context, CurationStatus status, String... comment) {
        updateHistory.push(new CurationStep(curatedValues, context, status, Arrays.asList(comment)));
        currentStatus = status;
    }

    public void update(NamedContext context, String field, String value, CurationStatus status, String... comment) {
        updateHistory.push(new CurationStep(Collections.singletonMap(field, value), context, status, Arrays.asList(comment)));
        curatedValues.put(field, value);
        currentStatus = status;
    }

    public void update(String comment) {
        updateHistory.push(new CurationStep(curatedValues, comment));
    }

    public void update(CurationStatus status, String... comment) {
        updateHistory.push(new CurationStep(curatedValues, status, Arrays.asList(comment)));
        currentStatus = status;
    }

    public void update(String field, String value, CurationStatus status, String... comment) {
        updateHistory.push(new CurationStep(Collections.singletonMap(field, value), status, Arrays.asList(comment)));
        curatedValues.put(field, value);
        currentStatus = status;
    }

    public List<CurationStep> getCurationHistory(NamedContext context) {
        List<CurationStep> historyForContext = new ArrayList<>();

        for (CurationStep curationStep : updateHistory) {
            if (curationStep.getContext() != null && curationStep.getContext().equals(context))
                historyForContext.add(curationStep);
        }

        return historyForContext;
    }

    public List<CurationStep> getCurationHistory() {
        return updateHistory;
    }

    public Map<String, String> getInitialValues() {
        return initialValues;
    }

    public Map<String, String> getFinalValues() {
        return curatedValues;
    }

    public CurationStatus getCurationStatus() {
        return currentStatus;
    }
}

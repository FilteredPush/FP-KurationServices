/**  DateValidatorTest.java
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

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.data.ffdq.DQReport;
import org.datakurator.postprocess.FFDQPostProcessor;
import org.filteredpush.kuration.util.CurationComment;
import org.junit.Test;
import org.kurator.akka.data.CurationStep;
import org.datakurator.data.ffdq.DQReportBuilder;
import org.datakurator.data.provenance.BaseRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;

/**
 * @author mole
 *
 */
public class DateValidatorTest {

	private static final Log logger = LogFactory.getLog(DateValidatorTest.class);
	
	/**
	 * Test method for {@link org.filteredpush.kuration.validators.DateValidator#validateEventConsistency(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidateEventConsistency() {
		CurationStep testResult = DateValidator.validateEventConsistency("1904-02-05", "1904", "02", "05", "36", "36", null, "Feb 5, 1904");
		logger.debug(testResult.getCurationComments().get(0));
		logger.debug(testResult.getValidationMethodSpecification());
		assertEquals(CurationComment.CORRECT.toString(), testResult.getCurationStates().get(0));
		
		testResult = DateValidator.validateEventConsistency("1906-02-05", "1904", "02", "05", "36", "36", null, "Feb 5, 1904");
		logger.debug(testResult.getCurationComments().get(0));
		assertEquals(CurationComment.UNABLE_CURATED.toString(), testResult.getCurationStates().get(0));
		
		testResult = DateValidator.validateEventConsistency("1904-03-05", "1904", "02", "05", "36", "36", null, "Feb 5, 1904");
		logger.debug(testResult.getCurationComments().get(0));
		assertEquals(CurationComment.UNABLE_CURATED.toString(), testResult.getCurationStates().get(0));
		
		testResult = DateValidator.validateEventConsistency("1904-02-08", "1904", "02", "05", "36", "36", null, "Feb 5, 1904");
		logger.debug(testResult.getCurationComments().get(0));
		assertEquals(CurationComment.UNABLE_CURATED.toString(), testResult.getCurationStates().get(0));
		
		testResult = DateValidator.validateEventConsistency("1904-02-05", "1904", "02", "05", "38", "38", null, "Feb 5, 1904");
		logger.debug(testResult.getCurationComments().get(0));
		assertEquals(CurationComment.UNABLE_CURATED.toString(), testResult.getCurationStates().get(0));
		
		testResult = DateValidator.validateEventConsistency("1904-02-05", "1904", "02", "05", "36", "38", null, "Feb 5, 1904");
		logger.debug(testResult.getCurationComments().get(0));
		assertEquals(CurationComment.UNABLE_CURATED.toString(), testResult.getCurationStates().get(0));
		
		testResult = DateValidator.validateEventConsistency("1904-02-05T06:32Z", "1904", "02", "05", "36", "36", "08:00Z", "Feb 5, 1904");
		logger.debug(testResult.getCurationComments().get(0));
		assertEquals(CurationComment.UNABLE_CURATED.toString(), testResult.getCurationStates().get(0));
		
		testResult = DateValidator.validateEventConsistency("1904-02-05T08:32Z", "1904", "02", "05", "36", "36", "08:32Z", "Feb 5, 1904");
		logger.debug(testResult.getCurationComments().get(0));
		assertEquals(CurationComment.CORRECT.toString(), testResult.getCurationStates().get(0));
		
		testResult = DateValidator.validateEventConsistency("1904-02-05T08:32Z", "1904", "02", "05", "36", "36", "08:32:30Z", "Feb 5, 1904");
		logger.debug(testResult.getCurationComments().get(0));
		assertEquals(CurationComment.UNABLE_CURATED.toString(), testResult.getCurationStates().get(0));
		
		//TODO: Include verbatim in assessment when atomic parts are included.
		//testResult = DateValidator.validateEventConsistency("1904-02-05", "1904", "02", "05", "36", "36", null, "Feb 6, 1904");
		//logger.debug(testResult.getCurationComments().get(0));
		//assertEquals(CurationComment.UNABLE_CURATED.toString(), testResult.getCurationStates().get(0));
	}

	/**
	 * Test method for {@link org.filteredpush.kuration.validators.DateValidator#validateEventConsistency(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidateEventConsistencyWithContext() throws IOException {
		//BaseRecord testResult = DateValidator.validateEventConsistencyWithContext("1904-02-05", "1904", "02", "05", "36", "36", null, "Feb 5, 1904");
		//assertEquals(CurationStatus.COMPLIANT, testResult.getCurationStatus());

		//printAssertions(testResult);

		//testResult = DateValidator.validateEventConsistencyWithContext("1904-02-08", "1904", "02", "05", "36", "36", null, "Feb 5, 1904");

		//printAssertions(testResult);

        BaseRecord testResult = DateValidator.validateEventConsistencyWithContext("01", "", "1904", "02", "05", "36", "36", null, "Feb 5, 1904");

		printAssertions(testResult);
		//logger.debug(testResult.getCurationHistory(new NamedContext("isEventDateConsistent")).get(0).getCurationComments().get(0));
		//assertEquals(CurationStatus.DATA_PREREQUISITES_NOT_MET, testResult.getCurationStatus());
	}

	private void printAssertions(BaseRecord testResult) throws IOException {
		InputStream config = DateValidatorTest.class.getResourceAsStream("/ffdq-assertions.json");
		DQReportBuilder builder = new DQReportBuilder(config);

		DQReport report = builder.createReport(testResult);
		StringWriter writer = new StringWriter();

		writer.append(report.toJson());

		config = DateValidatorTest.class.getResourceAsStream("/ffdq-assertions.json");
		FFDQPostProcessor postProcessor = new FFDQPostProcessor(Collections.singletonList(report), config);

		System.out.println(writer.toString());
	}

	public void testValidateEventConsistencyFFDQAssertions() {

	}
}

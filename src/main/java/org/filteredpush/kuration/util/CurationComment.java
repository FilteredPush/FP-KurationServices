package org.filteredpush.kuration.util;

import java.util.LinkedHashMap;

/**
 * Constants for marking the outcome of the application of a mechanism that 
 * implements some specification.  
 * 
 * See discussion on: http://wiki.filteredpush.org/wiki/AnalysisPostProcessing
 */
public class CurationComment {

	public static CurationCommentType construct(CurationStatus status,String details,String source) {
		LinkedHashMap<String,String> info = new LinkedHashMap<String,String>();
		info.put("status", status.toString());
		info.put("details", details);
		info.put("source", source);
        return new CurationCommentType(info);
	}	
	
	/**
	 * Indicates that no issues were found in the data per the specification.  
	 */
	public static CurationStatus CORRECT = new CurationStatus("Valid     ");
	/**
	 * Indicates that a change to the data has been proposed.
	 */
	public static CurationStatus CURATED = new CurationStatus("Curated   ");
	/**
	 * Indicates that one or more terms which were blank in the input have been
	 * filled in with some non-blank value in the output.
	 */
    public static CurationStatus FILLED_IN = new CurationStatus("Filled in ");
    /**
     * Indicates that it was possible to perform the tests of the specification on the
     * data, but that it was not possible to validate the provided data to the specification.
     * This tends to indicate a Solve_With_More_Data outcome.
     */
	public static CurationStatus UNABLE_CURATED = new CurationStatus("NotCurated");
	/**
	 * Some prerequisite for performing the tests in the specification was not met.  This could 
	 * be internal to the data (some required field was missing), or external (a webservice
	 * was down and unable to be consulted).
	 */
	public static CurationStatus UNABLE_DETERMINE_VALIDITY = new CurationStatus("!Validated");

}

package edu.harvard.mcz.nametools;

import org.apache.commons.lang3.StringUtils;

public abstract class AuthorNameComparator {
	
	public abstract double getSimilarityThreshold();

	/**
	 * Compare two authorship strings, and assert a comparison between the
	 * two in the form of a NameComparison.
	 * 
	 * @param anAuthor
	 * @param toOtherAuthor
	 * @return a string description classifying the match between the two 
	 * authorship strings, with awareness of string distance, parenthesies, and year.
	 * 
	 * @see NameComparison
	 */
	public abstract NameComparison compare(String anAuthor, String toOtherAuthor);
	
	/**
	 * Test to see if an authorship string appears to contain a year.
	 * 
	 * @param authorship to test for a year.
	 * 
	 * @return true if a four digit number is found.
	 */
	public static boolean calculateHasYear(String authorship) { 
		boolean result = false;
		if (authorship!=null && authorship.replaceAll("[^0-9]", "").length()==4) { 
			result = true;
		}
		return result;
	}
	
	/**
	 * Test to see if an authorship string appears to contain parentheses.
	 * 
	 * @param authorship to test for parenthesies
	 * @return true if authorship string contains '()';
	 */
	public static boolean calculateHasParen(String authorship) { 
		boolean result = false;
		if (authorship!=null && authorship.replaceAll("[^()]", "").equals("()")) { 
			result = true;
		}
		return result;
	}
	
	/**
	 * Test whether or not an authorship string is consistent with the 
	 * expected forms of a zoological authorship string. 
	 * 
	 * @param authorship string to test
	 * @return false if the string contains elements inconsistent with 
	 * a zoological authorship string, otherwise returns true.  
	 */
	public static boolean consistentWithICZNAuthor(String authorship) { 
		boolean result = true;
		if (authorship!=null) { 
			if (authorship.contains(" ex ")) {
				// ex author, botanical
				result = false;
			}
			if (authorship.contains(":")) {
				// sanctioning author, fungal
				result = false;  
			}
			if (authorship.matches("\\(.*\\)[ A-Za-z]+")) { 
				// string after parenthesies, botanical
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * Test whether or not an authorship string is consistent with the 
	 * expected forms of a botanical authorship string. 
	 * 
	 * @param authorship string to test
	 * @return false if the string contains elements inconsistent with 
	 * a botanical authorship string, otherwise returns true.  
	 */
	public static boolean consistentWithICNapfAuthor(String authorship) { 
		boolean result = true;
		if (authorship!=null) { 
			if (AuthorNameComparator.calculateHasYear(authorship)) {
				// botanical names do not normally contain a year of publication
				result = false;
			}
			if (authorship.matches("^\\(.*\\)$")) { 
				// if parentheses are present, botanical names also have an author outside the parentheses
				result = false;
			}
		}
		return result;
	}
	
	public static double calulateSimilarityOfAuthor(String anAuthor, String toOtherAuthor) { 
		String au = toOtherAuthor.toLowerCase().replaceAll("[, ]", "");
		String au1 = anAuthor.toLowerCase().replaceAll("[, ]", "");
		return AuthorNameComparator.stringSimilarity(au, au1);
	}

	public static double calulateSimilarityOfAuthorAlpha(String anAuthor, String toOtherAuthor) { 
		String au = toOtherAuthor.toLowerCase().replaceAll("[^A-Za-z]", "");
		String au1 = anAuthor.toLowerCase().replaceAll("[^A-Za-z]", "");
		return AuthorNameComparator.stringSimilarity(au, au1);
	}

	/**
	 * Return a measure of the similarity between two strings in the range of
	 * 0 (no similarity) to 1 (exact same strings), using a measure of the
	 * string edit distance scaled to the length differences of the two strings.
	 * 
	 * @param string1
	 * @param string2
	 * @return a double in the range 0 to 1.
	 */
	public static double stringSimilarity(String string1, String string2) {
		double result = 0d;
		String longer = string1;
		String shorter = string2;
		if (string1.length() < string2.length()) {
			// flip so that longer string is the longest.
			longer = string2;
			shorter = string1;
		}
		if (longer.length() == 0) { 
			result =  1.0; 
		} else { 
			result =  (longer.length() - StringUtils.getLevenshteinDistance(longer, shorter)) / (double) longer.length();
		}
		return result;
	}
	
}
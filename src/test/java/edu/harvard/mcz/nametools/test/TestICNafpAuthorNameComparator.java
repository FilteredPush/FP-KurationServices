package edu.harvard.mcz.nametools.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.harvard.mcz.nametools.ICNafpAuthorNameComparator;
import edu.harvard.mcz.nametools.ICZNAuthorNameComparator;
import edu.harvard.mcz.nametools.NameComparison;

public class TestICNafpAuthorNameComparator {

    private ICNafpAuthorNameComparator comparator;
	
	@Before
	public void setUp() throws Exception {
		comparator = new ICNafpAuthorNameComparator(.75d,.5d);
	}

	@Test
	public void testCompare() {
		assertEquals(NameComparison.MATCH_ERROR, comparator.compare(null, "Smith").getMatchType());
		assertEquals(NameComparison.MATCH_ERROR, comparator.compare("Smith", null).getMatchType());
		assertEquals(NameComparison.MATCH_ERROR, comparator.compare(null, null).getMatchType());
		
		assertEquals(NameComparison.MATCH_ADDSAUTHOR, comparator.compare("", "Conrad").getMatchType());
		
		assertEquals(NameComparison.MATCH_PARENTHESIESDIFFER, comparator.compare("Schilder","(Schilder)").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARENTHESIESDIFFER, comparator.compare("(Schilder)","Schilder").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("Schilder","(Schilder) Jones").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("Fries; Fries","Fries: Fries").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("Fries, Fries","Fries: Fries").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("Fries ex Fries","Fries: Fries").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("(Persoon) P. Kummer: Fries","(Persoon) Kummer, Fries").getMatchType()); 
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("(Persoon) P. Kummer: Fries","(Persoon) R. Kummer; Fries").getMatchType());
		assertEquals(NameComparison.MATCH_PARTSDIFFER, comparator.compare("(Persoon) P. Kummer: Fries","Persoon ex R. Kummer: Fries").getMatchType());
		
		assertEquals(NameComparison.MATCH_AUTHSIMILAR, comparator.compare("(Persoon) P. Kummer: Fries","(Persoon) R. Kummer: Fries").getMatchType());
		assertEquals(NameComparison.MATCH_AUTHSIMILAR, comparator.compare("(Persoon) P. Kummer: Fries","(Persoon) Kemmer: Fries").getMatchType());
		assertEquals(NameComparison.MATCH_AUTHSIMILAR, comparator.compare("(Persoon) P. Kummer: Fries","(Persoon) Ze Kummer: Fries").getMatchType());
		
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("L.","Linnaeus").getMatchType());
		assertEquals(NameComparison.MATCH_STRONGDISSIMILAR, comparator.compare("L.","Lamarck").getMatchType());
		
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("(Persoon) P. Kummer: Fries","(Persoon) Kummer: Fries").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(Schmidt) Coker & Beers ex Pouzar: Fries").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(Schmidt) Coker & A. Beers ex R. Pouzar: Fries").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(Schmidt) Coker & A. Beers ex R. Pouzar: Fr.").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(Schmidt) Coker & A. Beers ex R. Pouzar: Fr.").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("(Schmidt) Coker & Beers ex Pouzar: Fries","(Schmidt) Coker & Beers ex Pouzar: Fr.").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("(Schmidt) Coker & Beers ex Pouzar: Fries","(Schmidt) Coker & Beer. ex Pouza.: Fr.").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("(Schmidt) Coker & Beers ex Pouzar: Fries","(Schmi.) Coker & Beer. ex Pouza.: Fr.").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("(Schmidt) Coker & Beers ex Pouzar: Fries","(Schmi.) Cok. and Beer. ex Pouza.: Fr.").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(Schmidt) Coker & Beers ex Pouzar: Fr.").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare( "(Schmidt) Coker & Beers ex Pouzar: Fr." , "(J. C. Schmidt) Coker & Beers ex Pouzar: Fries").getMatchType());
		assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare( "Fries" , "Fr.").getMatchType());
		assertEquals(NameComparison.MATCH_STRONGDISSIMILAR, comparator.compare( "Fries" , "Fr").getMatchType());
        assertEquals(NameComparison.MATCH_SAMEBUTABBREVIATED, comparator.compare("Pouzar","P.").getMatchType());
		assertEquals(NameComparison.MATCH_STRONGDISSIMILAR, comparator.compare( "Pouzar" , "P").getMatchType());
		
		assertEquals(NameComparison.MATCH_EXACT, comparator.compare("Conrad ", "Conrad").getMatchType());
		assertEquals(NameComparison.MATCH_EXACT, comparator.compare("J.C. Schmidt", "J. C. Schmidt").getMatchType());
		
		// too much string difference
		assertEquals(NameComparison.MATCH_STRONGDISSIMILAR, comparator.compare("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(Schmi.) R. C. Cok. & A. Bee. ex R. Pouz.: Fr.").getMatchType());
	    assertEquals(NameComparison.MATCH_STRONGDISSIMILAR, comparator.compare("Pouzar","Fries").getMatchType());
	    assertEquals(.0000000d, comparator.compare("Pouzar","Fries").getSimilarity(),.000001d);
	    assertEquals(NameComparison.MATCH_STRONGDISSIMILAR, comparator.compare("Pouzar","Poies").getMatchType());
	    assertEquals(NameComparison.MATCH_STRONGDISSIMILAR, comparator.compare("Pouzar","Pouies").getMatchType());
	    assertEquals(.5d, comparator.compare("Pouzar","Pouies").getSimilarity(),.000001d);
	    assertEquals(NameComparison.MATCH_DISSIMILAR, comparator.compare("Pouzar","Pouzes").getMatchType());
	    assertEquals(.6666666d, comparator.compare("Pouzar","Pouzes").getSimilarity(),.000001d);
	    assertEquals(NameComparison.MATCH_AUTHSIMILAR, comparator.compare("Pouzar","Pouzas").getMatchType());
	    assertEquals(.8333333333d, comparator.compare("Pouzar","Pouzas").getSimilarity(),.000001d);
	}

	@Test
	public void testTokenizeAuthorship() {
		assertEquals(0,ICNafpAuthorNameComparator.tokenizeAuthorship("").size());
		assertEquals(0,ICNafpAuthorNameComparator.tokenizeAuthorship(null).size());
		assertEquals(1,ICNafpAuthorNameComparator.tokenizeAuthorship("Smith").size());
		assertEquals(2,ICNafpAuthorNameComparator.tokenizeAuthorship("(Jones) Smith").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Jones ex George) Smith").size());
		assertEquals(4,ICNafpAuthorNameComparator.tokenizeAuthorship("(Jones ex George) Smith ex Tome").size());
		
		assertEquals(4,ICNafpAuthorNameComparator.tokenizeAuthorship("(Schultz Bipontinus ex Seemann) Bentham & Hooker f. ex Hemsley").size());
		assertEquals(4,ICNafpAuthorNameComparator.tokenizeAuthorship("(Schultz Bipontinus ex A. Richard) Bentham & Hooker f. ex Vatke").size());
		assertEquals(4,ICNafpAuthorNameComparator.tokenizeAuthorship("(Ehrenberg ex Fries) Léveillé: Fries").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Persoon) P. Kummer: Fries").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Persoon) Kummer: Fries").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Fries) Patouillard & Hariot: Fries").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Linnaeus: Fries) Redhead, Lutzoni, Moncalvo & Vilgalys").size());
		assertEquals(3,ICNafpAuthorNameComparator.tokenizeAuthorship("(Tode: Fries) De Notaris").size());
		assertEquals(2,ICNafpAuthorNameComparator.tokenizeAuthorship("Fries: Fries").size());
		assertEquals(2,ICNafpAuthorNameComparator.tokenizeAuthorship("(G. Don) Exell").size());
	}

	@Test
	public void testIniitalExtraction() { 
		assertEquals("A",ICNafpAuthorNameComparator.extractInitials("A. Smith"));
		assertEquals("A",ICNafpAuthorNameComparator.extractInitials("A.Smith"));
		assertEquals("JC",ICNafpAuthorNameComparator.extractInitials("J.C. Schmidt"));
		assertEquals("JC",ICNafpAuthorNameComparator.extractInitials("J. C. Schmidt"));
		assertEquals("JC",ICNafpAuthorNameComparator.extractInitials("J. C. Schmidt c"));
		assertEquals("JCC",ICNafpAuthorNameComparator.extractInitials("J. C. Schmidt, C.Smi."));
		assertEquals("G",ICNafpAuthorNameComparator.extractInitials("(G. Don) Exell"));
		assertEquals("G",ICNafpAuthorNameComparator.extractInitials("G. Don"));
		assertEquals("G",ICNafpAuthorNameComparator.extractInitials(" G. Don"));
		
		
		assertEquals("",ICNafpAuthorNameComparator.extractInitials("Fr."));
		assertEquals("",ICNafpAuthorNameComparator.extractInitials(".F"));
		assertEquals("",ICNafpAuthorNameComparator.extractInitials("Fries"));
	}
	
	@Test
	public void testAbbreviationComparision() { 
		assertTrue(ICNafpAuthorNameComparator.matchedOnWordsInTokens("Fries","Fr."));
		assertTrue(ICNafpAuthorNameComparator.matchedOnWordsInTokens("Fries: Fries","Fr.: Fr."));
		assertTrue(ICNafpAuthorNameComparator.matchedOnWordsInTokens("Smith ex Jones","Smi. ex Jone."));
		assertTrue(ICNafpAuthorNameComparator.matchedOnWordsInTokens("(Smith) Fries: Fries","(Smi.) Fr.: Fr."));
		assertTrue(ICNafpAuthorNameComparator.matchedOnWordsInTokens("A. Smith","A. Smi."));
		
		
		assertTrue(ICNafpAuthorNameComparator.matchedOnWordsInTokens("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(Schmidt) Coker & Beers ex Pouzar: Fries"));
		assertTrue(ICNafpAuthorNameComparator.matchedOnWordsInTokens("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(Schmidt) Coker & Beers ex Pouzar: Fr."));
		
		assertFalse(ICNafpAuthorNameComparator.matchedOnWordsInTokens("Fries: Fries","Fr.; Fr."));
		assertFalse(ICNafpAuthorNameComparator.matchedOnWordsInTokens("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(J. R. Schmidt) Coker & Beers ex Pouzar: Fries"));
		assertFalse(ICNafpAuthorNameComparator.matchedOnWordsInTokens("(J. C. Schmidt) Coker & Beers ex Pouzar: Fries","(J. R. Schmidt) Coker & Beers ex Pouzar: Fr."));
		
		
		assertTrue(ICNafpAuthorNameComparator.matchedOnWordsInTokens("L.","Linnaeus"));
		assertFalse(ICNafpAuthorNameComparator.matchedOnWordsInTokens("L.","Lamarck"));
	}
	
	@Test 
	public void testCompareStart() { 
		assertTrue(ICNafpAuthorNameComparator.compareSameOrStartsWith("L", "Linnaeus"));
		assertTrue(ICNafpAuthorNameComparator.compareSameOrStartsWith("Sm", "Smith"));
		assertTrue(ICNafpAuthorNameComparator.compareSameOrStartsWith("Smith", "Sm"));
		
		assertFalse(ICNafpAuthorNameComparator.compareSameOrStartsWith("L", "Lamarck"));
		assertFalse(ICNafpAuthorNameComparator.compareSameOrStartsWith("Joe", "Smith"));
		
	}
	
}

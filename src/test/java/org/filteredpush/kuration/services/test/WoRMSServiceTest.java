package org.filteredpush.kuration.services.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.services.sciname.WoRMSService;
import org.filteredpush.kuration.util.CurationComment;

public class WoRMSServiceTest {

	private static final Log logger = LogFactory.getLog(WoRMSServiceTest.class);
	private WoRMSService service;
	
	@Before
	public void setUp() throws Exception {
		service = new WoRMSService(true);
	}


	@Test
	public void testValidateScientificNameStringString() {
		String name = "Murex (Haustellum) ruthae";
		String author = "Vokes, 1988";
		service.validateScientificName(name, author);
		logger.debug(service.getCorrectedScientificName() + " " + service.getCorrectedAuthor() + " " + service.getCurationStatus());
		assertEquals(name,service.getCorrectedScientificName());
		assertEquals(author,service.getCorrectedAuthor());
		assertEquals("urn:lsid:marinespecies.org:taxname:404020",service.getGUID());
		assertEquals(CurationComment.CORRECT, service.getCurationStatus());
		
		// Make sure that a second call with a different name doesn't retain information from the first call.
		name = "Murex scolopax";
		author = "Dillwyn";
		service.validateScientificName(name, author);
		logger.debug(service.getCorrectedScientificName() + " " + service.getCorrectedAuthor() + " " + service.getCurationStatus());
		assertEquals(name,service.getCorrectedScientificName());
		assertEquals("Dillwyn, 1817",service.getCorrectedAuthor());
		assertEquals("urn:lsid:marinespecies.org:taxname:730428",service.getGUID());
		assertEquals(CurationComment.CURATED, service.getCurationStatus());
		
	}

    @Test
    public void unableNameTest() {
    	// present with something that isn't a scientific name, should be unable to curate.
        service.validateScientificName("John smith", "(not an author, 1897)");
        assertTrue(service.getCorrectedScientificName().equals(""));
        assertTrue(service.getCurationStatus().toString().equals(CurationComment.UNABLE_CURATED.toString()));
    }
    
    @Test
    public void subspeciesTest() { 
    	String name = "Macoma baltica inconspicua";
    	String author = "Brod. and Sow.";
		service.validateScientificName(name, author);
		logger.debug(service.getCorrectedScientificName() + " " + service.getCorrectedAuthor() + " " + service.getCurationStatus());
		assertEquals(CurationComment.CURATED.toString(), service.getCurationStatus().toString());
		assertEquals(name,service.getCorrectedScientificName());
		assertEquals("(Broderip & Sowerby, 1829)",service.getCorrectedAuthor());
		
		name = "Bullia digitalis sulcata";
		author = "Reeve";
		service.validateScientificName(name, author);
		logger.debug(service.getCorrectedScientificName() + " " + service.getCorrectedAuthor() + " " + service.getCurationStatus());
		assertEquals(CurationComment.CURATED.toString(), service.getCurationStatus().toString());
		assertEquals(name,service.getCorrectedScientificName());
		assertEquals("Reeve, 1846",service.getCorrectedAuthor());
		
		// Make sure we aren't trying to treat binomial as a trinomial
		name = "Murex ruthae";
		author = "Vokes, 1988";
		service.validateScientificName(name, author);
		logger.debug(service.getComment());
		logger.debug(service.getCorrectedScientificName() + " " + service.getCorrectedAuthor() + " " + service.getCurationStatus());
		assertFalse(service.getComment().contains("Murex null Vokes"));
		
		// Make sure we aren't trying to treat a plant trinomial as a binomial
		name = "Ulva palmataa var. simplex";
		author = "(C.Agardh) Lyngbye";
		service.validateScientificName(name, author);
		logger.debug(service.getComment());
		logger.debug(service.getCorrectedScientificName() + " " + service.getCorrectedAuthor() + " " + service.getCurationStatus());
		assertFalse(service.getComment().contains("Also checking binomial: Ulva simplex"));
		
		name = "Cypraea spurca acicularis";
		author = "(Linnaeus, 1758)";
		service.validateScientificName(name, author);
		assertEquals("Gmelin, 1791", service.getCorrectedAuthor());
		assertEquals("Cypraea spurca acicularis", service.getCorrectedScientificName());
		// Match is Cypraea acicularis Gmelin, 1791
		// Cypraea spurca is Cypraea spurca Linnaeus, 1758
		// WoRMS has no acicularis Linnaeus...
		// That correction is change of rank isn't being included in output.
    }
    
    /**
     * Cases where WoRMS fuzzy matching casts too wide a net, and we tend to return results that
     * assert an undesirable proposed change to some other taxon.
     */
    @Test 
    public void testTooWideFuzzy() { 
    	// fuzzy matching when no author is provided is very iffy.
    	String name = "Limaea bronniana lata";
    	String author = "";
		service.validateScientificName(name, author);
		logger.debug(service.getCorrectedScientificName() + " " + service.getCorrectedAuthor() + " " + service.getCurationStatus());
		assertFalse(service.getCorrectedScientificName().equals("Limea lata"));
		
		// TODO: Mucella lima Martyn gets curated to Nucella lima (Gmelin, 1791).
		// Shouldn't be curating if the genera and author differ.
		
    }
	
    @Test 
    public void testFuzzyAuthorAssertion() { 
    	String name = "Clavus canicularis";
    	String author = "Roeding, 1798";
    	service.validateScientificName(name, author);
    	assertFalse(service.getComment().contains("Author Added"));
		assertEquals("(Röding, 1798)", service.getCorrectedAuthor());
		assertEquals("Clavus canalicularis", service.getCorrectedScientificName());
    	
    	name = "Clavus canicularis";
    	author = "";
    	service.validateScientificName(name, author);
    	assertTrue(service.getComment().contains("Author Added"));
		assertEquals("(Röding, 1798)", service.getCorrectedAuthor());
    }
    
}

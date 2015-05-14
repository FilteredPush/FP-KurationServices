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
	
}

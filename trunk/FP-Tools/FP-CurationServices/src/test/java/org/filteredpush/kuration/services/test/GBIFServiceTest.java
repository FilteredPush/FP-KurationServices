/**
 * 
 */
package org.filteredpush.kuration.services.test;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.services.sciname.GBIFService;
import org.filteredpush.kuration.services.sciname.SciNameServiceParent;
import org.filteredpush.kuration.util.CurationComment;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class GBIFServiceTest {

	private static final Log logger = LogFactory.getLog(GBIFServiceTest.class);

	
	private SciNameServiceParent service;

	@Before
	public void setUp() throws Exception {
		service = new GBIFService();
	}


	/**
	 * Test method for {@link org.filteredpush.kuration.services.sciname.SciNameServiceParent#validateScientificName(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidateScientificNameStringString() {
		String name = "Murex ramosus";
		String authorship = "Linnaeus, 1758";
        service.validateScientificName(name, authorship);
        assertEquals(name, service.getCorrectedScientificName());
        assertEquals(authorship, service.getCorrectedAuthor());
        logger.debug(service.getCurationStatus());
        logger.debug(service.getCorrectedScientificName());
        logger.debug(service.getCorrectedAuthor());
        assertEquals(CurationComment.CORRECT.toString(), service.getCurationStatus().toString());
        service.setValidationMode(GBIFService.MODE_TAXONOMIC);
        
        // TODO: Need to parse out the author from the accepted name in GBIF service.
        assertEquals("Chicoreus ramosus", service.getCorrectedScientificName());
        assertEquals("(Linnaeus, 1758)", service.getCorrectedAuthor());
	}
	
    @Test
    public void unableNameTest() {
    	// present with something that isn't a scientific name, should be unable to curate.
        service.validateScientificName("John smith", "(not an author, 1897)");
        assertTrue(service.getCorrectedScientificName().equals(""));
        assertEquals(CurationComment.UNABLE_CURATED.toString(), service.getCurationStatus().toString());
    }	

}

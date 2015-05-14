/**
 * 
 */
package org.filteredpush.kuration.services.test;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.services.sciname.IndexFungorumService;
import org.filteredpush.kuration.services.sciname.SciNameServiceParent;
import org.filteredpush.kuration.util.CurationComment;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class IndexFungorumServiceTest {

	private static final Log logger = LogFactory.getLog(IndexFungorumServiceTest.class);

	private SciNameServiceParent service;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		service = new IndexFungorumService();
	}

	/**
	 * Test method for {@link org.filteredpush.kuration.services.sciname.SciNameServiceParent#validateScientificName(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidateScientificNameStringString() {
		String name = "Umbilicaria aprina";
		String author = "Nyl.";
		service.validateScientificName(name, author);
		logger.debug(service.getCorrectedScientificName() + " " + service.getCorrectedAuthor() + " " + service.getCurationStatus());
		assertEquals(name,service.getCorrectedScientificName());
		assertEquals(author,service.getCorrectedAuthor());
		assertEquals("urn:lsid:indexfungorum.org:names:408077",service.getGUID());
		assertEquals(CurationComment.CORRECT, service.getCurationStatus());
		
		// Make sure that a second call with a different name doesn't retain information from the first call.
		name = "Lecanora albicans";
		author = "(Nyl) Hertel & Rambold";
		service.validateScientificName(name, author);
		logger.debug(service.getCorrectedScientificName() + " " + service.getCorrectedAuthor() + " " + service.getCurationStatus());
		assertEquals(name,service.getCorrectedScientificName());
		assertEquals("(Nyl.) Hertel & Rambold",service.getCorrectedAuthor());
		assertEquals("urn:lsid:indexfungorum.org:names:104445",service.getGUID());
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

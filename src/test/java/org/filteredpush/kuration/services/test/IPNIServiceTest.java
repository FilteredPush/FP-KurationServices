/**
 * 
 */
package org.filteredpush.kuration.services.test;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.services.sciname.IPNIService;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationException;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class IPNIServiceTest {
	
	private static final Log logger = LogFactory.getLog(IPNIServiceTest.class);

	private IPNIService service;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		service = new IPNIService();
	}

	/**
	 * Test method for {@link org.filteredpush.kuration.services.sciname.IPNIService#nameSearchAgainstServices(edu.harvard.mcz.nametools.NameUsage)}.
	 */
	@Test
	public void testNameSearchAgainstServices() {
		String scientificName = "Quercus alba";
		String author = "L.";
		try {
			assertEquals("295763-1",service.simplePlantNameSearch(scientificName, author));
		} catch (CurationException e) {
			fail("Unexpected exception " + e.getMessage());
		}
		
		service.validateScientificName(scientificName, author);
		logger.debug(service.getCurationStatus().toString());
		logger.debug(service.getCorrectedScientificName());
		logger.debug(service.getCorrectedAuthor());
		assertEquals(CurationComment.CORRECT,service.getCurationStatus());
		assertEquals(author,service.getCorrectedAuthor());
		assertEquals(scientificName,service.getCorrectedScientificName());
		
		scientificName = "Quercus alba";
		author = "Linnaeus";
		service.validateScientificName(scientificName, author);
		logger.debug(service.getCurationStatus().toString());
		logger.debug(service.getCorrectedScientificName());
		logger.debug(service.getCorrectedAuthor());
		assertEquals(CurationComment.CORRECT,service.getCurationStatus());
		assertEquals("L.",service.getCorrectedAuthor());
		assertEquals(scientificName,service.getCorrectedScientificName());		
		
		author = "L.";
		service.validateScientificName(scientificName, "Smith");
		assertEquals(CurationComment.CURATED,service.getCurationStatus());
		assertEquals(author,service.getCorrectedAuthor());
		assertEquals(scientificName,service.getCorrectedScientificName());	
		
		// A non-plant name
		service.validateScientificName("Sclerobunus", "");
		logger.debug(service.getCurationStatus().toString());
		logger.debug(service.getCorrectedScientificName());
		assertEquals(CurationComment.CURATED,service.getCurationStatus());
		assertEquals("Banks, 1893",service.getCorrectedAuthor());
		assertEquals("Sclerobunus",service.getCorrectedScientificName());	
			
	}
	
    @Test
    public void unableNameTest() {
    	// present with something that isn't a scientific name, should be unable to curate.
        service.validateScientificName("John smith", "(not an author, 1897)");
        assertTrue(service.getCorrectedScientificName().equals(""));
        assertTrue(service.getCurationStatus().toString().equals(CurationComment.UNABLE_CURATED.toString()));
    }	
    
    @Test
    public void missingAuthorTest() { 
		String scientificName = "Quercus alba";
		String author = "";
		service.validateScientificName(scientificName, author);
		logger.debug(service.getCurationStatus().toString());
		logger.debug(service.getCorrectedScientificName());
		logger.debug(service.getCorrectedAuthor());
		logger.debug(service.getComment());
		assertEquals(CurationComment.CURATED,service.getCurationStatus());
		assertEquals("L.",service.getCorrectedAuthor());
		assertEquals(scientificName,service.getCorrectedScientificName());
    }
    
    @Test
    public void auctorumTest() { 
		String scientificName = "Sisyrinchium bermudiana";
		String author = "auct. non L.";
		service.validateScientificName(scientificName, author);
		logger.debug(service.getCurationStatus().toString());
		logger.debug(service.getCorrectedScientificName());
		logger.debug(service.getCorrectedAuthor());
		logger.debug(service.getComment());
		assertEquals(CurationComment.UNABLE_CURATED,service.getCurationStatus());
		assertEquals("auct. non L.",service.getCorrectedAuthor());
		assertEquals(scientificName,service.getCorrectedScientificName());
    	
    }

}

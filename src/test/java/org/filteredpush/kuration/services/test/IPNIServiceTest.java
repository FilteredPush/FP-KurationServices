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
		service.setUseCache(false);
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

}

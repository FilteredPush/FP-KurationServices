/**
 * 
 */
package fp.services;

import static org.junit.Assert.*;

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
		assertEquals(CurationComment.CORRECT,service.getCurationStatus());
		assertEquals(author,service.getCorrectedAuthor());
		assertEquals(scientificName,service.getCorrectedScientificName());
		
		service.validateScientificName(scientificName, "Smith");
		assertEquals(CurationComment.CURATED,service.getCurationStatus());
		assertEquals(author,service.getCorrectedAuthor());
		assertEquals(scientificName,service.getCorrectedScientificName());		
			
	}

}

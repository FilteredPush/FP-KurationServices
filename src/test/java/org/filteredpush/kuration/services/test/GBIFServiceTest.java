/**
 * 
 */
package org.filteredpush.kuration.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.services.sciname.GBIFService;
import org.filteredpush.kuration.services.sciname.SciNameServiceParent;
import org.filteredpush.kuration.util.CurationComment;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the GBIF service wrapper.
 * 
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

    @Test 
    public void hemihomonymTest() { 
    	// animal 
    	service.validateScientificName("Agathis montana","Shestakov, 1932");
        logger.debug(service.getComment());
        logger.debug(service.getCurationStatus());
        logger.debug(service.getCorrectedScientificName());
        logger.debug(service.getCorrectedAuthor());
        assertEquals(CurationComment.CORRECT.toString(),service.getCurationStatus().toString());
        // plant 
    	service.validateScientificName("Agathis montana","de Laub");
        logger.debug(service.getComment());
        logger.debug(service.getCurationStatus());
        logger.debug(service.getCorrectedScientificName());
        logger.debug(service.getCorrectedAuthor());
        assertEquals(CurationComment.CORRECT.toString(),service.getCurationStatus().toString());
        // Need author to disambiguate
    	service.validateScientificName("Agathis montana","");
        logger.debug(service.getComment());
        logger.debug(service.getCurationStatus());
        logger.debug(service.getCorrectedScientificName());
        logger.debug(service.getCorrectedAuthor());
        assertEquals(CurationComment.UNABLE_DETERMINE_VALIDITY.toString(),service.getCurationStatus().toString());
        
        // should be able to correct author
    	service.validateScientificName("Asterina gibbosa","Pennant, 1777");
        logger.debug(service.getComment());
        logger.debug(service.getCurationStatus());
        logger.debug(service.getCorrectedScientificName());
        assertEquals("(Pennant, 1777)",service.getCorrectedAuthor());
        assertEquals(CurationComment.CURATED.toString(),service.getCurationStatus().toString());
    }
    
    @Test
    public void testValidateNameStringFillIn() {
		String name = "Murex ramosus";
    	String scientificNameToValidate = "";  //  empty string 
    	String authorToValidate = "Linnaeus, 1758";
    	String genus ="Murex";
    	String subgenus = "";
    	String specificEpithet = "ramosus";
    	String verbatimTaxonRank = "";
    	String infraspecificEpithet = "";
    	String taxonRank = "Species";
    	String kingdom = "Animalia";
    	service.validateScientificName(scientificNameToValidate, authorToValidate, genus, subgenus, specificEpithet, verbatimTaxonRank, infraspecificEpithet, taxonRank, kingdom, "", "", "", "", "");
        assertEquals(name, service.getCorrectedScientificName());
        assertEquals(authorToValidate, service.getCorrectedAuthor());
        logger.debug(service.getCurationStatus());
        logger.debug(service.getCorrectedScientificName());
        logger.debug(service.getCorrectedAuthor());
        assertEquals(CurationComment.FILLED_IN.toString(), service.getCurationStatus().toString());
    	service.validateScientificName(scientificNameToValidate, authorToValidate, "", subgenus, specificEpithet, verbatimTaxonRank, infraspecificEpithet, taxonRank, kingdom, "", "", "", "", genus);
        assertEquals(name, service.getCorrectedScientificName());
        assertEquals(authorToValidate, service.getCorrectedAuthor());
        logger.debug(service.getCurationStatus());
        logger.debug(service.getCorrectedScientificName());
        logger.debug(service.getCorrectedAuthor());
        assertEquals(CurationComment.CORRECT.toString(), service.getCurationStatus().toString());        
        
        
        service.validateScientificName("", "");
        assertEquals(CurationComment.UNABLE_DETERMINE_VALIDITY, service.getCurationStatus());
        
        service.validateScientificName("", "Linnaeus 1758");
        assertEquals(CurationComment.UNABLE_DETERMINE_VALIDITY, service.getCurationStatus());
        
        service.validateScientificName(null, null);
        assertEquals(CurationComment.UNABLE_DETERMINE_VALIDITY, service.getCurationStatus());
        
        service.validateScientificName("    ", "Linnaeus 1758");
        assertEquals(CurationComment.UNABLE_DETERMINE_VALIDITY, service.getCurationStatus());
    }
    
}

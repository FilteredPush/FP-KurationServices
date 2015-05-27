package org.filteredpush.kuration.services.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.interfaces.INewScientificNameValidationService;
import org.filteredpush.kuration.services.sciname.COLService;
import org.filteredpush.kuration.util.CurationComment;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tianhong on 10/22/14.
 */
public class COLServiceTest {
	
	private static final Log logger = LogFactory.getLog(COLServiceTest.class);
    
	private INewScientificNameValidationService scientificNameService = new COLService();
    //String serviceClassQN = "org.filteredpush.kuration.services.COLService";
    //scientificNameService = (INewScientificNameValidationService)Class.forName(serviceClassQN).newInstance();

    @Test
    public void validNameTest(){
        scientificNameService.validateScientificName("Eucerceris canaliculata", "(Say, 1823)");
        //assertTrue(scientificNameService.getCorrectedScientificName().equals("Eucerceris canaliculata"));
        logger.debug(scientificNameService.getCurationStatus());
        logger.debug(scientificNameService.getCorrectedScientificName());
        logger.debug(scientificNameService.getCorrectedAuthor());
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.CORRECT));
    } 
    
    @Test
    public void addAuthorCuratedTest() { 
        scientificNameService.validateScientificName("Quercus alba", "");
        assertEquals("L.", scientificNameService.getCorrectedAuthor());
        logger.debug(scientificNameService.getCurationStatus());
        logger.debug(scientificNameService.getCorrectedScientificName());
        logger.debug(scientificNameService.getCorrectedAuthor());
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.CURATED));        
    }
    
    @Test
    public void validNameWithAuthTest(){
        scientificNameService.validateScientificName("Eucerceris canaliculata (Say, 1823)", "(Say, 1823)");
        assertTrue(scientificNameService.getCorrectedScientificName().equals("Eucerceris canaliculata"));
        logger.debug(scientificNameService.getCurationStatus());
        logger.debug(scientificNameService.getCorrectedScientificName());
        logger.debug(scientificNameService.getCorrectedAuthor());
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.CORRECT));
    }    

    @Test
    public void unableNameTest() {
    	// present with something that isn't a scientific name, should be unable to curate.
        scientificNameService.validateScientificName("John smith", "(not an author, 1897)");
        assertTrue(scientificNameService.getCorrectedScientificName().equals(""));
        logger.debug(scientificNameService.getCurationStatus());
        logger.debug(scientificNameService.getCorrectedScientificName());
        logger.debug(scientificNameService.getCorrectedAuthor());
        assertEquals(CurationComment.UNABLE_CURATED.toString(), scientificNameService.getCurationStatus().toString());
        
        scientificNameService.validateScientificName("Speranza trilinearia", "");
        //assertTrue(scientificNameService.getCorrectedScientificName().equals(""));
        assertTrue(scientificNameService.getCurationStatus().toString().equals(CurationComment.UNABLE_CURATED.toString()));
    }

    @Test
    public void noResultTest() {
        //TODO: need to confirm why no result...
    	// there is a COL entry 10647270 for this taxon.
    	
        scientificNameService.validateScientificName("Norape tenera", "(Druce, 1897)");
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.UNABLE_DETERMINE_VALIDITY));
    }

    @Test
    public void COLCuratedTest() {
        //found in COL
        String name1 = "Euptoieta claudia";
        String author1 = "(Cramer, 1775)";
        scientificNameService.validateScientificName(name1, author1);
        assertTrue(scientificNameService.getCorrectedAuthor().equals("Cramer, 1775"));
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.CURATED));
    }

    @Test
    public void GBIFCuratedTest() {
        //found in GBIF, not COL
        String name2 = "Formicidae";
        String author2 = null;
        scientificNameService.validateScientificName(name2, author2);
        logger.debug(scientificNameService.getCurationStatus());
        logger.debug(scientificNameService.getCorrectedScientificName());
        logger.debug(scientificNameService.getCorrectedAuthor());
        assertTrue(scientificNameService.getCorrectedAuthor().equals("Latreille, 1802"));
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.CURATED));
    }

}

package fp.services;

import fp.util.CurationComment;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by tianhong on 10/22/14.
 */
public class SciNameServiceTest {
    private INewScientificNameValidationService scientificNameService = new COLService();
    //String serviceClassQN = "fp.services.COLService";
    //scientificNameService = (INewScientificNameValidationService)Class.forName(serviceClassQN).newInstance();

    @Test
    public void validNameTest(){
        scientificNameService.validateScientificName("Eucerceris canaliculata", "(Say, 1823)");
        //assertTrue(scientificNameService.getCorrectedScientificName().equals("Eucerceris canaliculata"));
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.CORRECT));
    }

    @Test
    public void unableNameTest() {
        scientificNameService.validateScientificName("Speranza trilinearia", "");
        //assertTrue(scientificNameService.getCorrectedScientificName().equals(""));
        assertTrue(scientificNameService.getCurationStatus().toString().equals(CurationComment.UNABLE_CURATED.toString()));
    }

    @Test
    public void noResultTest() {
        //todo: need to confirm why no result...
        scientificNameService.validateScientificName("Norape tenera", "(Druce, 1897)");
        //assertTrue(scientificNameService.getCorrectedScientificName().equals(""));
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
        assertTrue(scientificNameService.getCorrectedAuthor().equals("Latreille, 1802"));
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.CURATED));
    }

}

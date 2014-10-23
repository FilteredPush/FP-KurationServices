package fp.services;

/**
 * Created by tianhong on 10/22/14.
 */
public class SciNameServiceTest {
    private INewScientificNameValidationService scientificNameService = new COLService();
    //String serviceClassQN = "fp.services.COLService";
    //scientificNameService = (INewScientificNameValidationService)Class.forName(serviceClassQN).newInstance();
     /*
    @Test
    public void validNameTest(){
        String name = "Eucerceris canaliculata";
        scientificNameService.validateScientificName(name, "");
        System.out.println("scientificNameService1 = " + scientificNameService.getCorrectedScientificName());
        //assertTrue(scientificNameService.getCorrectedScientificName().equals("Eucerceris canaliculata"));
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.CORRECT));
    }

    @Test
    public void unableNameTest(){
        String name1 = "Speranza trilinearia";
        scientificNameService.validateScientificName(name1, "");
        System.out.println("scientificNameService2 = " + scientificNameService.getCorrectedScientificName());
        assertTrue(scientificNameService.getCorrectedScientificName().equals(""));
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.UNABLE_CURATED));

        String name2 = "Norape tenera";
        String author2 = "(Druce, 1897)";
        scientificNameService.validateScientificName(name2, author2);
        assertTrue(scientificNameService.getCorrectedScientificName().equals(""));
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.UNABLE_DETERMINE_VALIDITY));
    }

    @Test
    public void curatedNameTest(){
        //found in COL
        String name1 = "Euptoieta claudia";
        String author1 = "(Cramer, 1775)";
        scientificNameService.validateScientificName(name1, author1);
        assertTrue(scientificNameService.getCorrectedAuthor().equals("Cramer, 1775"));
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.CURATED));

        //found in GBIF, not COL
        String name2 = "Formicidae";
        String author2 = null;
        scientificNameService.validateScientificName(name2, author2);
        assertTrue(scientificNameService.getCorrectedAuthor().equals("Latreille, 1802"));
        assertTrue(scientificNameService.getCurationStatus().equals(CurationComment.CURATED));
    }
    */
}

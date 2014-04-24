package fp.services;

public interface IAdvancedScientificNameValidationService extends ICurationWithFileService {
	
	public void validateScientificName(String scientificName, String author);
	
	public String getCorrectedScientificName();
	
	public String getCorrectedAuthor();
	
	//public String getLSID();


	//public void validateScientificName(String scientificName, String author, String rank, String kingdom, String phylum, String tclass);

    public void validateScientificName(String scientificNameToValidate, String authorToValidate,
                                       String genus, String subgenus, String specificEpithet,
                                       String verbatimTaxonRank, String infraspecificEpithet,
                                       String taxonRank);


    }
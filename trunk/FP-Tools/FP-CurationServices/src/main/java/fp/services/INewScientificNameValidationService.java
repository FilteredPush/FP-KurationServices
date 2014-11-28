package fp.services;

import fp.util.CurationStatus;

public interface INewScientificNameValidationService {
	
	public void validateScientificName(String scientificName, String author);
	
	public String getCorrectedScientificName();
	
	public String getCorrectedAuthor();

    public CurationStatus getCurationStatus();

    public String getComment();

    public String getLSID();

    public String getServiceName();
	
	//public String getLSID();


	//public void validateScientificName(String scientificName, String author, String rank, String kingdom, String phylum, String tclass);

    public void validateScientificName(String scientificNameToValidate, String authorToValidate,
                                       String genus, String subgenus, String specificEpithet,
                                       String verbatimTaxonRank, String infraspecificEpithet,
                                       String taxonRank, String kingdom, String phylum,
                                       String tclass, String order, String family);


    }
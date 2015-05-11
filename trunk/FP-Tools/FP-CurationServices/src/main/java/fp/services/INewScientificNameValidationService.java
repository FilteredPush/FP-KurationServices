package fp.services;

import edu.harvard.mcz.nametools.AuthorNameComparator;
import fp.util.CurationStatus;

public interface INewScientificNameValidationService {
	
	/**
	 * If the service is specific to a nomenclatural code, return the appropriate scientific 
	 * name authorship comparator for that code.  If the service holds names for more than
	 * one code, try to figure out the correct code from the authorship string and kingdom.
	 * 
	 * @param authorship
	 * @param kingdom
	 * @return an AuthorNameComparator
	 */
	public AuthorNameComparator getAuthorNameComparator(String authorship, String kingdom);
	
	public void validateScientificName(String scientificName, String author);
	
	public String getCorrectedScientificName();
	
	public String getCorrectedAuthor();

    public CurationStatus getCurationStatus();

    public String getComment();

    public String getGUID();

    public String getServiceName();
	
	//public String getLSID();


	//public void validateScientificName(String scientificName, String author, String rank, String kingdom, String phylum, String tclass);

    public void validateScientificName(String scientificNameToValidate, String authorToValidate,
                                       String genus, String subgenus, String specificEpithet,
                                       String verbatimTaxonRank, String infraspecificEpithet,
                                       String taxonRank, String kingdom, String phylum,
                                       String tclass, String order, String family);


    }
package fp.services;

public interface IScientificNameValidationService extends ICurationWithFileService {
	
	public void validateScientificName(String scientificName, String author);
	
	public String getCorrectedScientificName();
	
	public String getCorrectedAuthor();
	
	public String getLSID();

	/**
	 * Validate a scientific name against the implemented service.
	 * 
	 * @param scientificName the name to validate.
	 * @param author the authorship of the name to validate
	 * @param rank the rank at which to test the name if provided by the service
	 * @param kingdom may be used by services or implementations to detect ambiregnal homonyms of the scientific name provided.
	 * @param phylum may be used by services or implementations to detect homonyms of the scientific name provided.
	 * @param tclass may be used by services or implementations to detect homonyms of the scientific name provided.
	 */
	public void validateScientificName(String scientificName, String author,
                                       String rank, String kingdom, String phylum, String tclass);
	
	public String getFoundKingdom();
	public String getFoundPhylum();
	public String getFoundOrder();
	public String getFoundClass();
	public String getFoundFamily();
	
}
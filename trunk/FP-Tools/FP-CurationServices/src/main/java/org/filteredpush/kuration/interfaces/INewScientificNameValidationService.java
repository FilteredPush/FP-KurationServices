package org.filteredpush.kuration.interfaces;

import org.filteredpush.kuration.util.CurationStatus;

import edu.harvard.mcz.nametools.AuthorNameComparator;

/**
 * Interface for classes that can be used by scientific name actors in workflows
 * for validation of scientific names.  
 * 
 * @author mole
 *
 */
public interface INewScientificNameValidationService {
	
	public static final String MODE_TAXONOMIC = "taxonomic";
	public static final String MODE_NOMENCLATURAL = "nomeclatural";
	
	/**
	 * If the service is specific to a nomenclatural code, return the appropriate scientific 
	 * name authorship comparator for that code.  If the service holds names for more than
	 * one code, try to figure out the correct code from the authorship string and kingdom.
	 * 
	 * TODO: This belongs inside SciNameServiceParent, not in the exposed interface.
	 * 
	 * @param authorship
	 * @param kingdom
	 * @return an AuthorNameComparator
	 */
	public AuthorNameComparator getAuthorNameComparator(String authorship, String kingdom);
	
	/**
	 * Attempt to validate a scientific name and authorship.  Obtain the result by invoking
	 * getCorrectedScientificName(), getCorrectedAuthor(), getCurationStatus(), and getComment(). 
	 * 
	 * @param scientificName the scientific name to validate
	 * @param author the corresponding scientificNameAuthor to validate
	 */
	public void validateScientificName(String scientificName, String author);
	
	/**
	 * Set the mode for returns from the service.  Not all service implementations
	 * may support all modes.
	 * 
	 * Possible usage:
	 * 
	 * service.validateScientificName("Quercus alba", "L.");
	 * service.setValidationMode(MODE_TAXONOMIC);
	 * acceptedName = service.getCorrectedScientificName();
	 * service.setValidationMode(MODE_NOMENCLATURAL);
	 * name = service.getCorrectedScientificName();
	 * 
	 * @param validationMode either MODE_TAXONOMIC or MODE_NOMENCLATURAL
	 */
	public void setValidationMode(String validationMode);
	
	public String getCorrectedScientificName();
	
	public String getCorrectedAuthor();

    public CurationStatus getCurationStatus();

    public String getComment();

    public String getGUID();

    public String getServiceName();
	
    public void validateScientificName(String scientificNameToValidate, String authorToValidate,
                                       String genus, String subgenus, String specificEpithet,
                                       String verbatimTaxonRank, String infraspecificEpithet,
                                       String taxonRank, String kingdom, String phylum,
                                       String tclass, String order, String family, String genericEpithet);

    public String getCorrectedKingdom();
    public String getCorrectedPhylum();
    public String getCorrectedOrder();
    public String getCorrectedClass();
    public String getCorrectedFamily();
    
    
    }
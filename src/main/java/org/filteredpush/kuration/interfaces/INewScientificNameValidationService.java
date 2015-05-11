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
	
	public void validateScientificName(String scientificName, String author);
	
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
                                       String tclass, String order, String family);


    }
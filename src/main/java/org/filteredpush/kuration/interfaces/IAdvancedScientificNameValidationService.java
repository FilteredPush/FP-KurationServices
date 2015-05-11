package org.filteredpush.kuration.interfaces;

import edu.harvard.mcz.nametools.AuthorNameComparator;

public interface IAdvancedScientificNameValidationService extends ICurationWithFileService {
	
	public AuthorNameComparator getAuthorNameComparator(String authorship, String kingdom);
	
	public void validateScientificName(String scientificName, String author);
	
	public String getCorrectedScientificName();
	
	public String getCorrectedAuthor();
	
	//public String getLSID();


	//public void validateScientificName(String scientificName, String author, String rank, String kingdom, String phylum, String tclass);

    public void validateScientificName(String scientificNameToValidate, String authorToValidate,
                                       String genus, String subgenus, String specificEpithet,
                                       String verbatimTaxonRank, String infraspecificEpithet,
                                       String taxonRank, String kingdom, String phylum,
                                       String tclass, String order, String family);


    }
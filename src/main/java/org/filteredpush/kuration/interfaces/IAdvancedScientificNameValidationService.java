/** 
 * IAdvancedScientificNameValidationService.java 
 * 
 * Copyright 2013 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
/**
 * 
 */
package org.filteredpush.kuration.interfaces;

/**
 * @author mole
 *
 */
public interface IStringValidationService extends ICurationWithFileService {

	public void validateString(String aString);

    public String getCorrectedValue();
	
}

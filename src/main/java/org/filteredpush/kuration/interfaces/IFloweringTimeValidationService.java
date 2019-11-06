package org.filteredpush.kuration.interfaces;

import java.util.Vector;

public interface IFloweringTimeValidationService extends ICurationWithFileService {
	
	@Deprecated
	public void validateFloweringTime(String scientificName, String eventDate, String reproductiveState, String country, String kingdom);
	
	/**
	 * Check reproductive state at date collected against some service that knows about scientific names and date ranges for reproductive states. 
	 * 
	 * @param scientificName to check
	 * @param eventDate to check
	 * @param reproductiveState to check
	 * @param country to check
	 * @param kingdom for the scientificName
	 * @param latitude to check
	 * @param longitude to check
	 */
	public void validateFloweringTime(String scientificName, String eventDate, String reproductiveState, String country, String kingdom, String latitude, String longitude);
	
	public Vector<String> getCorrectedFloweringTime();
}

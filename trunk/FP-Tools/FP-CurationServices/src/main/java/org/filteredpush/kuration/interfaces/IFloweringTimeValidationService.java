package org.filteredpush.kuration.interfaces;

import java.util.Vector;

public interface IFloweringTimeValidationService extends ICurationWithFileService {
	
	@Deprecated
	public void validateFloweringTime(String scientificName, String eventDate, String reproductiveState, String country, String kingdom);
	
	/**
	 * Check reproductive state at date collected against some service that knows about scientific names and date ranges for reproductive states. 
	 * 
	 * @param scientificName
	 * @param eventDate
	 * @param reproductiveState
	 * @param country
	 * @param kingdom
	 * @param latitude
	 * @param longitude
	 */
	public void validateFloweringTime(String scientificName, String eventDate, String reproductiveState, String country, String kingdom, String latitude, String longitude);
	
	public Vector<String> getCorrectedFloweringTime();
}
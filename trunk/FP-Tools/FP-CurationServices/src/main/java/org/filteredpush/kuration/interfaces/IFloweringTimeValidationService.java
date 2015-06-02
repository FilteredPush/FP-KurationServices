package org.filteredpush.kuration.interfaces;

import java.util.Vector;

public interface IFloweringTimeValidationService extends ICurationWithFileService {
	
	public void validateFloweringTime(String scientificName, String eventDate, String reproductiveState, String country, String kingdom);
	
	public Vector<String> getCorrectedFloweringTime();
}
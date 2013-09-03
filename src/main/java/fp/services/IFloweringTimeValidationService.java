package fp.services;

import java.util.Vector;

public interface IFloweringTimeValidationService extends ICurationWithFileService {
	
	public void validateFloweringTime(String scientificName, Vector<String> months);
	
	public Vector<String> getCorrectedFloweringTime();
}
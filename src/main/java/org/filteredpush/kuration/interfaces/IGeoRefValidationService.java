package org.filteredpush.kuration.interfaces;

public interface IGeoRefValidationService extends ICurationWithFileService {
	public void validateGeoRef(String country, String stateProvince, String county, String waterBody, String verbatimDepth, String locality, String latitude, String longitude, double certainty);
	
	public double getCorrectedLatitude();
	
	public double getCorrectedLongitude();
}
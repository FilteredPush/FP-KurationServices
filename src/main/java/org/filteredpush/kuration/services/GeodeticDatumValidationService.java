/**
 * 
 */
package org.filteredpush.kuration.services;

import java.util.List;

import org.filteredpush.kuration.interfaces.IStringValidationService;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationException;
import org.filteredpush.kuration.util.CurationStatus;
import org.filteredpush.kuration.util.SpecimenRecord;

/**
 * @author mole
 *
 */
public class GeodeticDatumValidationService extends BaseCurationService implements IStringValidationService {

	private String correctedValue;
	
	private boolean useEPSGCodes;
	
	public GeodeticDatumValidationService() {
		super();
		initDS();
		useEPSGCodes = false;
	}
	
	public GeodeticDatumValidationService(boolean useEPSGCodes) {
		super();
		initDS();
		this.useEPSGCodes = useEPSGCodes;
	}	
	
	protected void initDS() { 
		initBase();
		correctedValue = "";
	}
	
	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.ICurationWithFileService#setCacheFile(java.lang.String)
	 */
	@Override
	public void setCacheFile(String file) throws CurationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.ICurationWithFileService#flushCacheFile()
	 */
	@Override
	public void flushCacheFile() throws CurationException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.ICurationService#getLog()
	 */
	@Override
	public List<List> getLog() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.ICurationService#setUseCache(boolean)
	 */
	@Override
	public void setUseCache(boolean use) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.ICurationService#getServiceName()
	 */
	@Override
	public String getServiceName() {
		return "GeodeticDatumValidator";
	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.IStringValidationService#validateString(java.lang.String)
	 */
	@Override
	public void validateString(String aString) {
		initDS();
		this.addInputValue(SpecimenRecord.dwc_geodeticDatum, aString);
		setCurationStatus(CurationComment.UNABLE_CURATED);
		if (aString !=null) { 
			if (this.useEPSGCodes) { 
			   if (aString.equals("epsg:4326")) { setCurationStatus(CurationComment.CORRECT); } 
			   if (!getCurationStatus().toString().equals(CurationComment.CORRECT.toString())) { 
			       if (aString.trim().toLowerCase().replaceAll(" ", "").equals("wgs84")) { 
			      	  setCurationStatus(CurationComment.CURATED);
			    	  correctedValue = "epsg:4326";
			       }
			       if (aString.trim().toLowerCase().replaceAll(" ", "").equals("nad83")) { 
			      	  setCurationStatus(CurationComment.CURATED);
			    	  correctedValue = "epsg:4269";
			       }
			       if (aString.trim().toLowerCase().replaceAll(" ", "").equals("nad27")) { 
			      	  setCurationStatus(CurationComment.CURATED);
			    	  correctedValue = "epsg:4267";
			       }
			   } 
			} else {
			   if (aString.equals("WGS84")) { setCurationStatus(CurationComment.CORRECT); } 
			   if (aString.equals("WGS72")) { setCurationStatus(CurationComment.CORRECT); } 
			   if (aString.equals("NAD83")) { setCurationStatus(CurationComment.CORRECT); } 
			   if (aString.equals("NAD27")) { setCurationStatus(CurationComment.CORRECT); } 
			   if (!getCurationStatus().toString().equals(CurationComment.CORRECT.toString())) { 
			       if (aString.trim().toLowerCase().replaceAll(" ", "").equals("wgs84")) { 
			      	  setCurationStatus(CurationComment.CURATED);
			    	  correctedValue = "WGS84";
			       }
			   } 
			}
		}
		if (getCorrectedValue().length()>0) { 
			this.addCuratedValue("geodeticDatum", getCorrectedValue());
		}
	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.IStringValidationService#getCorrectedValue()
	 */
	@Override
	public String getCorrectedValue() {
		if (correctedValue==null) { 
			return "";
		}
		return correctedValue;
	}

}

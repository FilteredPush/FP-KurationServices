/**
 * 
 */
package org.filteredpush.kuration.services;

import java.util.List;

import org.filteredpush.kuration.interfaces.IStringValidationService;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationException;
import org.filteredpush.kuration.util.CurationStatus;

/**
 * @author mole
 *
 */
public class GeodeticDatumValidationService implements IStringValidationService {

	StringBuilder comment;
	
	private CurationStatus curationStatus;
	
	private String correctedValue;
	
	private boolean useEPSGCodes;
	
	public GeodeticDatumValidationService() { 
		init();
		useEPSGCodes = false;
	}
	
	public GeodeticDatumValidationService(boolean useEPSGCodes) { 
		init();
		this.useEPSGCodes = useEPSGCodes;
	}	
	
	protected void init() { 
		comment = new StringBuilder();
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
	 * @see org.filteredpush.kuration.interfaces.ICurationService#getComment()
	 */
	@Override
	public String getComment() {
		return comment.toString();
	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.ICurationService#addToComment(java.lang.String)
	 */
	@Override
	public void addToComment(String aComment) {
		if (comment.length()==0) { 
			comment.append(aComment);
		} else { 
			comment.append(" | ").append(aComment);
		}

	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.ICurationService#getCurationStatus()
	 */
	@Override
	public CurationStatus getCurationStatus() {
		return curationStatus;
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
		init();
		curationStatus = CurationComment.UNABLE_CURATED;
		if (aString !=null) { 
			if (this.useEPSGCodes) { 
			   if (aString.equals("epsg:4326")) { curationStatus = CurationComment.CORRECT; } 
			   if (!curationStatus.toString().equals(CurationComment.CORRECT.toString())) { 
			       if (aString.trim().toLowerCase().replaceAll(" ", "").equals("wgs84")) { 
			      	  curationStatus = CurationComment.CURATED;
			    	  correctedValue = "epsg:4326";
			       }
			       if (aString.trim().toLowerCase().replaceAll(" ", "").equals("nad83")) { 
			      	  curationStatus = CurationComment.CURATED;
			    	  correctedValue = "epsg:4269";
			       }
			       if (aString.trim().toLowerCase().replaceAll(" ", "").equals("nad27")) { 
			      	  curationStatus = CurationComment.CURATED;
			    	  correctedValue = "epsg:4267";
			       }
			   } 
			} else {
			   if (aString.equals("WGS84")) { curationStatus = CurationComment.CORRECT; } 
			   if (aString.equals("WGS72")) { curationStatus = CurationComment.CORRECT; } 
			   if (aString.equals("NAD83")) { curationStatus = CurationComment.CORRECT; } 
			   if (aString.equals("NAD27")) { curationStatus = CurationComment.CORRECT; } 
			   if (!curationStatus.toString().equals(CurationComment.CORRECT.toString())) { 
			       if (aString.trim().toLowerCase().replaceAll(" ", "").equals("wgs84")) { 
			      	  curationStatus = CurationComment.CURATED;
			    	  correctedValue = "WGS84";
			       }
			   } 
			}
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

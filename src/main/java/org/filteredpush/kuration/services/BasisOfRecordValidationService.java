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
public class BasisOfRecordValidationService implements IStringValidationService {

	StringBuilder comment;
	
	private CurationStatus curationStatus;
	
	private String correctedValue;
	
	public BasisOfRecordValidationService() { 
		init();
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
		return "BasisOfRecordValidator";
	}

	/* (non-Javadoc)
	 * @see org.filteredpush.kuration.interfaces.IStringValidationService#validateString(java.lang.String)
	 */
	@Override
	public void validateString(String aString) {
		init();
		curationStatus = CurationComment.UNABLE_CURATED;
		if (aString !=null) { 
			if (aString.equals("MaterialSample")) { curationStatus = CurationComment.CORRECT; } 
			if (aString.equals("LivingSpecimen")) { curationStatus = CurationComment.CORRECT; } 
			if (aString.equals("PreservedSpecimen")) { curationStatus = CurationComment.CORRECT; } 
			if (aString.equals("FossilSpecimen")) { curationStatus = CurationComment.CORRECT; } 
			if (aString.equals("HumanObservation")) { curationStatus = CurationComment.CORRECT; } 
			if (aString.equals("MachineObservation")) { curationStatus = CurationComment.CORRECT; } 
			if (aString.equals("Taxon")) { curationStatus = CurationComment.CORRECT; } 
			if (!curationStatus.toString().equals(CurationComment.CORRECT.toString())) { 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("materialsample")) { 
			    	curationStatus = CurationComment.CURATED;
			    	correctedValue = "MaterialSample";
			    } 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("livingspecimen")) { 
			    	curationStatus = CurationComment.CURATED;
			    	correctedValue = "LivingSpecimen";
			    } 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("preservedspecimen")) { 
			    	curationStatus = CurationComment.CURATED;
			    	correctedValue = "PreservedSpecimen";
			    } 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("fossilspecimen")) { 
			    	curationStatus = CurationComment.CURATED;
			    	correctedValue = "FossilSpecimen";
			    } 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("humanobservation")) { 
			    	curationStatus = CurationComment.CURATED;
			    	correctedValue = "HumanObservation";
			    } 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("machineobservation")) { 
			    	curationStatus = CurationComment.CURATED;
			    	correctedValue = "MachineObservation";
			    } 
			    if (aString.trim().toLowerCase().equals("taxon")) { 
			    	curationStatus = CurationComment.CURATED;
			    	correctedValue = "Taxon";
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

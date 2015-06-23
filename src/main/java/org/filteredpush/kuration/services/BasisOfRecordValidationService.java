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
public class BasisOfRecordValidationService extends BaseCurationService implements IStringValidationService {

	private String correctedValue;
	
	public BasisOfRecordValidationService() { 
		super();
		initBR();
	}
	
	protected void initBR() { 
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
	 * @see org.filteredpush.kuration.interfaces.IStringValidationService#validateString(java.lang.String)
	 */
	@Override
	public void validateString(String aString) {
		initBR();
		addInputValue(SpecimenRecord.dwc_basisOfRecord, aString);
		setCurationStatus(CurationComment.UNABLE_CURATED);
		if (aString !=null) { 
			if (aString.equals("MaterialSample")) { setCurationStatus(CurationComment.CORRECT); } 
			if (aString.equals("LivingSpecimen")) { setCurationStatus(CurationComment.CORRECT); } 
			if (aString.equals("PreservedSpecimen")) { setCurationStatus(CurationComment.CORRECT); } 
			if (aString.equals("FossilSpecimen")) { setCurationStatus(CurationComment.CORRECT); } 
			if (aString.equals("HumanObservation")) { setCurationStatus(CurationComment.CORRECT); } 
			if (aString.equals("MachineObservation")) { setCurationStatus(CurationComment.CORRECT); } 
			if (aString.equals("Taxon")) { setCurationStatus(CurationComment.CORRECT); } 
			if (!getCurationStatus().toString().equals(CurationComment.CORRECT.toString())) {
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("materialsample")) { 
			    	setCurationStatus(CurationComment.CURATED);
			    	correctedValue = "MaterialSample";
				    addToComment("Correcting capitalization and spacing to conform with controlled vocabulary.");
			    } 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("livingspecimen")) { 
			    	setCurationStatus(CurationComment.CURATED);
			    	correctedValue = "LivingSpecimen";
				    addToComment("Correcting capitalization and spacing to conform with controlled vocabulary.");
			    } 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("preservedspecimen")) { 
			    	setCurationStatus(CurationComment.CURATED);
			    	correctedValue = "PreservedSpecimen";
				    addToComment("Correcting capitalization and spacing to conform with controlled vocabulary.");
			    } 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("fossilspecimen")) { 
			    	setCurationStatus(CurationComment.CURATED);
			    	correctedValue = "FossilSpecimen";
				    addToComment("Correcting capitalization and spacing to conform with controlled vocabulary.");
			    } 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("humanobservation")) { 
			    	setCurationStatus(CurationComment.CURATED);
			    	correctedValue = "HumanObservation";
				    addToComment("Correcting capitalization and spacing to conform with controlled vocabulary.");
			    } 
			    if (aString.trim().toLowerCase().replaceAll(" ", "").equals("machineobservation")) { 
			    	setCurationStatus(CurationComment.CURATED);
			    	correctedValue = "MachineObservation";
				    addToComment("Correcting capitalization and spacing to conform with controlled vocabulary.");
			    } 
			    if (aString.trim().toLowerCase().equals("taxon")) { 
			    	setCurationStatus(CurationComment.CURATED);
			    	correctedValue = "Taxon";
				    addToComment("Correcting capitalization and spacing to conform with controlled vocabulary.");
			    } 
			}
		}
		if (getCorrectedValue().length()>0) { 
			this.addCuratedValue(SpecimenRecord.dwc_basisOfRecord, getCorrectedValue());
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

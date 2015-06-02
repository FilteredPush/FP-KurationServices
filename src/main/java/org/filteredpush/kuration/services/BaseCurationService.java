package org.filteredpush.kuration.services;

import java.util.HashMap;
import java.util.Map;

import org.filteredpush.kuration.interfaces.ICurationService;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationStatus;

public abstract class BaseCurationService implements ICurationService {

	/**
	 * Comments added during curation.
	 */
	private StringBuffer comments;
	/**
	 * List of external services that have been invoked.
	 */
	private StringBuffer services;
	private Map<String,String> inputValues;
	private Map<String,String> curatedValues;
	private CurationStatus curationStatus;
	 
    public static final String SEPARATOR = " | ";
	
    public Map<String,String> getInputValues() {
    	if (inputValues==null) { 
    		inputValues = new HashMap<String,String>();
    	}
    	return inputValues;
    }
    
    public Map<String,String> getCuratedValues() {
    	if (curatedValues==null) { 
    		curatedValues = new HashMap<String,String>();
    	}
    	return curatedValues;
    }
    
    public void addInputValue(String key, String value) { 
    	inputValues.put(key, value);
    }
    
    public void addCuratedValue(String key, String value) { 
    	curatedValues.put(key, value);
    }
    
	public BaseCurationService() { 
		initBase();
	}
	
	private void initBase() { 
		comments = new StringBuffer();
		services = new StringBuffer();
		inputValues = new HashMap<String,String>();
		curatedValues = new HashMap<String,String>();
        curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
	}
	
	protected void init() { 
		comments = new StringBuffer();
		services = new StringBuffer();
	}
	
	@Override
	public String getComment() {
		return comments.toString();
	}

	@Override
	public void addToComment(String comment) {
	    if (comment!=null && comment.length()>0) {
	    	if (comments.length()>0) { 
                comments.append(SEPARATOR).append(comment);
	    	}  else { 
	    		comments.append("comment");
	    	}
        }
	}

	@Override
	public CurationStatus getCurationStatus() {
		return curationStatus;
	}
	
	@Override
	public void setCurationStatus(CurationStatus newStatus) {
		if (newStatus !=null) { 
		    curationStatus = newStatus;
		}
	}

	@Override
	public void addToServiceName(String serviceName) {
		if (serviceName!=null && serviceName.length()>0) { 
			services.append(SEPARATOR).append(serviceName);
		}
		
	}

	@Override
	public String getServiceName() {
		return services.toString();
	}

}

package org.filteredpush.kuration.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.kuration.interfaces.ICurationService;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationStatus;
import org.kurator.akka.data.CurationStep;

public abstract class BaseCurationService implements ICurationService {

	private static final Log logger = LogFactory.getLog(BaseCurationService.class);

	private CurationStep curationStep;
	
	/**
	 * Comments added during curation.
	 */
	// private StringBuffer comments;
	/**
	 * List of external services that have been invoked.
	 */
	private StringBuffer services;
	private Map<String,String> inputValues;
	private Map<String,String> curatedValues;
	private CurationStatus curationStatus;
	 
    public static final String SEPARATOR = " | ";
	
    /**
	 * @return the curationStep
	 */
	public CurationStep getCurationStep() {
		return curationStep;
	}

	/**
	 * @param curationStep the curationStep to set
	 */
	public void setCurationStep(CurationStep curationStep) {
		this.curationStep = curationStep;
	}

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
		initBase(new CurationStep("BaseCurationService: Not initialized properly.", new HashMap<String, String>()));
	}
	
	protected void initBase(CurationStep curationStep) {
		services = new StringBuffer();
		inputValues = new HashMap<String,String>();
		curatedValues = new HashMap<String,String>();
        curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
        this.curationStep = curationStep;
	}
	
	protected void init() { 
		services = new StringBuffer();
	}
	
	@Override
	public String getComment() {
		StringBuffer result = new StringBuffer();
		Iterator<String> i = curationStep.getCurationComments().iterator();
		while (i.hasNext()) { 
			String comment = i.next();
			if (comment!=null && comment.trim().length()>0) { 
				if (result.length()>0) { 
				    result.append(SEPARATOR).append(comment);
				} else { 
					result.append(comment);
				}
			}
		}
		return result.toString();
	}

	@Override
	public void addToComment(String comment) {
		logger.debug(comment);
		curationStep.addCurationComment(comment);
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
			if (services.length() > 0 ) { 
			   services.append(SEPARATOR).append(serviceName);
			} else { 
			   services.append(serviceName);
			}
		}
		
	}

	@Override
	public String getServiceName() {
		return services.toString();
	}

}

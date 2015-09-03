package org.filteredpush.kuration.interfaces;

import java.util.List;

import org.filteredpush.kuration.util.CurationStatus;

/**
 * Interface for classes that are able to curate data objects, making assertions about
 * their curation status, along with comments, services consulted and a provenance
 * log. 
 * 
 * ICurationService is the key interface used to obtain results in FP-Akka actors from their 
 * internal implementations in FP-KurationServices.
 * 
 * @author Lei Dou
 * @author chicoreus
 *
 */
public interface ICurationService {
	
	/**
	 * TODO: Used in Kepler, not used in FP-Akka?  Assess.
	 * 
	 * @return the provenance log created by the curation class.
	 */
    public List<List> getLog();
    
    /**
     * Turn on/off the use of caching by the curation service. 
     * 
     * @param use
     */
    public void setUseCache(boolean use);
    /**
     * Get the assertions that the curation service has made about the curated
     * data object.
     * 
     * @return a delimited list of assertions made about the curated data.
     */
	public String getComment();		
    /**
     * Add a comment to the list of comments for validation of a record 
     * by the implemented service.  Adds an appropriate comment separator.
     * 
     * @param aComment comment to add to the current list of comments.
     */
	public void addToComment(String comment);
	
	public CurationStatus getCurationStatus();
	public void setCurationStatus(CurationStatus newStatus);
    /**
     * Add a service to the list of invoked services for validation of a record.
     * Adds an appropriate separator.
     * 
     * @param aServiceName comment to add to the current list of comments.
     */
	public void addToServiceName(String serviceName);
	/**
	 * Get the list of invoked services.
	 * 
	 * TODO: Needs to be reworked - implementation overloaded, return value contains both
	 * list of key value pairs of changed values from some services as well as the list 
	 * of external services consulted.
	 * 
	 * @return a delimited list of services invoked by the curation service
	 */
	public String getServiceName();	
}

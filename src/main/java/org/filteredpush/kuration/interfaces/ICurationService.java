package org.filteredpush.kuration.interfaces;

import java.util.List;

import org.filteredpush.kuration.util.CurationStatus;

public interface ICurationService {
    public List<List> getLog();
    public void setUseCache(boolean use);
	public String getComment();		
	public void addToComment(String comment);
	public CurationStatus getCurationStatus();
	public String getServiceName();	
}

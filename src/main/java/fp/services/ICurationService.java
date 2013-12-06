package fp.services;

import fp.util.CurationStatus;

import java.util.List;

public interface ICurationService {
    public List<List> getLog();
    public void setUseCache(boolean use);
	public String getComment();		
	public CurationStatus getCurationStatus();
	public String getServiceName();	
}

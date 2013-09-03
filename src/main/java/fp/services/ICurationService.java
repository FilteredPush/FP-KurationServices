package fp.services;

import fp.util.CurationStatus;

public interface ICurationService {
    public void setUseCache(boolean use);
	public String getComment();		
	public CurationStatus getCurationStatus();
	public String getServiceName();	
}

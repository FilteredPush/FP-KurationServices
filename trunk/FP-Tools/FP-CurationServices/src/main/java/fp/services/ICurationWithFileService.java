package fp.services;

import fp.util.CurationException;

public interface ICurationWithFileService  extends ICurationService {
    public void setCacheFile(String file) throws CurationException;
  	public void flushCacheFile() throws CurationException;
}

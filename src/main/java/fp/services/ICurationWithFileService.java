package fp.services;

import fp.util.CurrationException;

public interface ICurationWithFileService  extends ICurationService {
    public void setCacheFile(String file) throws CurrationException;
  	public void flushCacheFile() throws CurrationException;
}

package org.filteredpush.kuration.interfaces;

import org.filteredpush.kuration.util.CurationException;

public interface ICurationWithFileService  extends ICurationService {
    public void setCacheFile(String file) throws CurationException;
  	public void flushCacheFile() throws CurationException;
}

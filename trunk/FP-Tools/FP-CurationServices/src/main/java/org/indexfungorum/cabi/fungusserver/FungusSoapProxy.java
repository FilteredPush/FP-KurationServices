package org.indexfungorum.cabi.fungusserver;

public class FungusSoapProxy implements org.indexfungorum.cabi.fungusserver.FungusSoap {
  private String _endpoint = null;
  private org.indexfungorum.cabi.fungusserver.FungusSoap fungusSoap = null;
  
  public FungusSoapProxy() {
    _initFungusSoapProxy();
  }
  
  public FungusSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initFungusSoapProxy();
  }
  
  private void _initFungusSoapProxy() {
    try {
      fungusSoap = (new org.indexfungorum.cabi.fungusserver.FungusLocator()).getFungusSoap();
      if (fungusSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)fungusSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)fungusSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (fungusSoap != null)
      ((javax.xml.rpc.Stub)fungusSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.indexfungorum.cabi.fungusserver.FungusSoap getFungusSoap() {
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap;
  }
  
  public boolean isAlive() throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.isAlive();
  }
  
  public org.indexfungorum.cabi.fungusserver.NameSearchResponseNameSearchResult nameSearch(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.nameSearch(searchText, anywhereInText, maxNumber);
  }
  
  public org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult nameSearchDs(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.nameSearchDs(searchText, anywhereInText, maxNumber);
  }
  
  public org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult epithetSearch(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.epithetSearch(searchText, anywhereInText, maxNumber);
  }
  
  public org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult epithetSearchDs(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.epithetSearchDs(searchText, anywhereInText, maxNumber);
  }
  
  public org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult nameByKey(long nameKey) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.nameByKey(nameKey);
  }
  
  public org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult nameByKeyDs(long nameKey) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.nameByKeyDs(nameKey);
  }
  
  public java.lang.String nameFullByKey(java.lang.String nameLsid) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.nameFullByKey(nameLsid);
  }
  
  public org.indexfungorum.cabi.fungusserver.NameByKeyRDFResponseNameByKeyRDFResult nameByKeyRDF(java.lang.String nameLsid) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.nameByKeyRDF(nameLsid);
  }
  
  public org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult authorSearch(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.authorSearch(searchText, anywhereInText, maxNumber);
  }
  
  public org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult authorSearchDs(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.authorSearchDs(searchText, anywhereInText, maxNumber);
  }
  
  public org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyResponseNamesByCurrentKeyResult namesByCurrentKey(long currentKey) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.namesByCurrentKey(currentKey);
  }
  
  public org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult namesByCurrentKeyDs(long currentKey) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.namesByCurrentKeyDs(currentKey);
  }
  
  public org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult allUpdatedNames(java.lang.String startDate) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.allUpdatedNames(startDate);
  }
  
  public org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult updatedNamesInRange(java.lang.String rank, java.lang.String startDate, java.lang.String endDate) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.updatedNamesInRange(rank, startDate, endDate);
  }
  
  public org.indexfungorum.cabi.fungusserver.UpdatedNamesResponseUpdatedNamesResult updatedNames(java.lang.String rank, java.lang.String startDate) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.updatedNames(rank, startDate);
  }
  
  public org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult newNamesInRange(java.lang.String rank, java.lang.String startDate, java.lang.String endDate) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.newNamesInRange(rank, startDate, endDate);
  }
  
  public org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult newNames(java.lang.String rank, java.lang.String startDate) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.newNames(rank, startDate);
  }
  
  public org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult deprecatedNames(java.lang.String startDate) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.deprecatedNames(startDate);
  }
  
  public org.indexfungorum.cabi.fungusserver.DeprecatedNamesByRankResponseDeprecatedNamesByRankResult deprecatedNamesByRank(java.lang.String rank, java.lang.String startDate) throws java.rmi.RemoteException{
    if (fungusSoap == null)
      _initFungusSoapProxy();
    return fungusSoap.deprecatedNamesByRank(rank, startDate);
  }
  
  
}
/**
 * FungusSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public interface FungusSoap extends java.rmi.Remote {
    public boolean isAlive() throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.NameSearchResponseNameSearchResult nameSearch(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult nameSearchDs(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult epithetSearch(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult epithetSearchDs(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult nameByKey(long nameKey) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult nameByKeyDs(long nameKey) throws java.rmi.RemoteException;
    public java.lang.String nameFullByKey(java.lang.String nameLsid) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.NameByKeyRDFResponseNameByKeyRDFResult nameByKeyRDF(java.lang.String nameLsid) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult authorSearch(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult authorSearchDs(java.lang.String searchText, boolean anywhereInText, long maxNumber) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyResponseNamesByCurrentKeyResult namesByCurrentKey(long currentKey) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult namesByCurrentKeyDs(long currentKey) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult allUpdatedNames(java.lang.String startDate) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult updatedNamesInRange(java.lang.String rank, java.lang.String startDate, java.lang.String endDate) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.UpdatedNamesResponseUpdatedNamesResult updatedNames(java.lang.String rank, java.lang.String startDate) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult newNamesInRange(java.lang.String rank, java.lang.String startDate, java.lang.String endDate) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult newNames(java.lang.String rank, java.lang.String startDate) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult deprecatedNames(java.lang.String startDate) throws java.rmi.RemoteException;
    public org.indexfungorum.cabi.fungusserver.DeprecatedNamesByRankResponseDeprecatedNamesByRankResult deprecatedNamesByRank(java.lang.String rank, java.lang.String startDate) throws java.rmi.RemoteException;
}

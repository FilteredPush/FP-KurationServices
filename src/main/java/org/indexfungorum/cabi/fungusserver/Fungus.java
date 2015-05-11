/**
 * Fungus.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public interface Fungus extends javax.xml.rpc.Service {
    public java.lang.String getFungusSoapAddress();

    public org.indexfungorum.cabi.fungusserver.FungusSoap getFungusSoap() throws javax.xml.rpc.ServiceException;

    public org.indexfungorum.cabi.fungusserver.FungusSoap getFungusSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

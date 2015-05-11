/**
 * FungusLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class FungusLocator extends org.apache.axis.client.Service implements org.indexfungorum.cabi.fungusserver.Fungus {

    public FungusLocator() {
    }


    public FungusLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FungusLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for FungusSoap
    private java.lang.String FungusSoap_address = "http://www.indexfungorum.org/ixfwebservice/fungus.asmx";

    public java.lang.String getFungusSoapAddress() {
        return FungusSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FungusSoapWSDDServiceName = "FungusSoap";

    public java.lang.String getFungusSoapWSDDServiceName() {
        return FungusSoapWSDDServiceName;
    }

    public void setFungusSoapWSDDServiceName(java.lang.String name) {
        FungusSoapWSDDServiceName = name;
    }

    public org.indexfungorum.cabi.fungusserver.FungusSoap getFungusSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FungusSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFungusSoap(endpoint);
    }

    public org.indexfungorum.cabi.fungusserver.FungusSoap getFungusSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.indexfungorum.cabi.fungusserver.FungusSoapStub _stub = new org.indexfungorum.cabi.fungusserver.FungusSoapStub(portAddress, this);
            _stub.setPortName(getFungusSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setFungusSoapEndpointAddress(java.lang.String address) {
        FungusSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.indexfungorum.cabi.fungusserver.FungusSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.indexfungorum.cabi.fungusserver.FungusSoapStub _stub = new org.indexfungorum.cabi.fungusserver.FungusSoapStub(new java.net.URL(FungusSoap_address), this);
                _stub.setPortName(getFungusSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("FungusSoap".equals(inputPortName)) {
            return getFungusSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://Cabi/FungusServer/", "Fungus");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "FungusSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("FungusSoap".equals(portName)) {
            setFungusSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

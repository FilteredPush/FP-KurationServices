/**
 * AphiaNameServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.marinespecies.aphia.v1_0;

public class AphiaNameServiceLocator extends org.apache.axis.client.Service implements org.marinespecies.aphia.v1_0.AphiaNameService {

    public AphiaNameServiceLocator() {
    }


    public AphiaNameServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AphiaNameServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for AphiaNameServicePort
    private String AphiaNameServicePort_address = "http://www.marinespecies.org/aphia.php?p=soap";

    public String getAphiaNameServicePortAddress() {
        return AphiaNameServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private String AphiaNameServicePortWSDDServiceName = "AphiaNameServicePort";

    public String getAphiaNameServicePortWSDDServiceName() {
        return AphiaNameServicePortWSDDServiceName;
    }

    public void setAphiaNameServicePortWSDDServiceName(String name) {
        AphiaNameServicePortWSDDServiceName = name;
    }

    public org.marinespecies.aphia.v1_0.AphiaNameServicePortType getAphiaNameServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(AphiaNameServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAphiaNameServicePort(endpoint);
    }

    public org.marinespecies.aphia.v1_0.AphiaNameServicePortType getAphiaNameServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.marinespecies.aphia.v1_0.AphiaNameServiceBindingStub _stub = new org.marinespecies.aphia.v1_0.AphiaNameServiceBindingStub(portAddress, this);
            _stub.setPortName(getAphiaNameServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAphiaNameServicePortEndpointAddress(String address) {
        AphiaNameServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.marinespecies.aphia.v1_0.AphiaNameServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.marinespecies.aphia.v1_0.AphiaNameServiceBindingStub _stub = new org.marinespecies.aphia.v1_0.AphiaNameServiceBindingStub(new java.net.URL(AphiaNameServicePort_address), this);
                _stub.setPortName(getAphiaNameServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
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
        String inputPortName = portName.getLocalPart();
        if ("AphiaNameServicePort".equals(inputPortName)) {
            return getAphiaNameServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://aphia/v1.0", "AphiaNameService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://aphia/v1.0", "AphiaNameServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

if ("AphiaNameServicePort".equals(portName)) {
            setAphiaNameServicePortEndpointAddress(address);
        }
        else
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

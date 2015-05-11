/**
 * NewNamesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class NewNamesResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult newNamesResult;

    public NewNamesResponse() {
    }

    public NewNamesResponse(
           org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult newNamesResult) {
           this.newNamesResult = newNamesResult;
    }


    /**
     * Gets the newNamesResult value for this NewNamesResponse.
     * 
     * @return newNamesResult
     */
    public org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult getNewNamesResult() {
        return newNamesResult;
    }


    /**
     * Sets the newNamesResult value for this NewNamesResponse.
     * 
     * @param newNamesResult
     */
    public void setNewNamesResult(org.indexfungorum.cabi.fungusserver.NewNamesResponseNewNamesResult newNamesResult) {
        this.newNamesResult = newNamesResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NewNamesResponse)) return false;
        NewNamesResponse other = (NewNamesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.newNamesResult==null && other.getNewNamesResult()==null) || 
             (this.newNamesResult!=null &&
              this.newNamesResult.equals(other.getNewNamesResult())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getNewNamesResult() != null) {
            _hashCode += getNewNamesResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NewNamesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NewNamesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newNamesResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NewNamesResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NewNamesResponse>NewNamesResult"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

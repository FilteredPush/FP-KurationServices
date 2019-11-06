/**
 * NameFullByKeyResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class NameFullByKeyResponse  implements java.io.Serializable {
    private java.lang.String nameFullByKeyResult;

    public NameFullByKeyResponse() {
    }

    public NameFullByKeyResponse(
           java.lang.String nameFullByKeyResult) {
           this.nameFullByKeyResult = nameFullByKeyResult;
    }


    /**
     * Gets the nameFullByKeyResult value for this NameFullByKeyResponse.
     * 
     * @return nameFullByKeyResult
     */
    public java.lang.String getNameFullByKeyResult() {
        return nameFullByKeyResult;
    }


    /**
     * Sets the nameFullByKeyResult value for this NameFullByKeyResponse.
     * 
     * @param nameFullByKeyResult to set
     */
    public void setNameFullByKeyResult(java.lang.String nameFullByKeyResult) {
        this.nameFullByKeyResult = nameFullByKeyResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NameFullByKeyResponse)) return false;
        NameFullByKeyResponse other = (NameFullByKeyResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nameFullByKeyResult==null && other.getNameFullByKeyResult()==null) || 
             (this.nameFullByKeyResult!=null &&
              this.nameFullByKeyResult.equals(other.getNameFullByKeyResult())));
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
        if (getNameFullByKeyResult() != null) {
            _hashCode += getNameFullByKeyResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NameFullByKeyResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameFullByKeyResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nameFullByKeyResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameFullByKeyResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     * @return typeDesc metadata
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /*
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

    /*
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

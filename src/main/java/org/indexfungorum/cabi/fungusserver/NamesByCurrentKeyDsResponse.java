/**
 * NamesByCurrentKeyDsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class NamesByCurrentKeyDsResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult namesByCurrentKeyDsResult;

    public NamesByCurrentKeyDsResponse() {
    }

    public NamesByCurrentKeyDsResponse(
           org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult namesByCurrentKeyDsResult) {
           this.namesByCurrentKeyDsResult = namesByCurrentKeyDsResult;
    }


    /**
     * Gets the namesByCurrentKeyDsResult value for this NamesByCurrentKeyDsResponse.
     * 
     * @return namesByCurrentKeyDsResult
     */
    public org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult getNamesByCurrentKeyDsResult() {
        return namesByCurrentKeyDsResult;
    }


    /**
     * Sets the namesByCurrentKeyDsResult value for this NamesByCurrentKeyDsResponse.
     * 
     * @param namesByCurrentKeyDsResult
     */
    public void setNamesByCurrentKeyDsResult(org.indexfungorum.cabi.fungusserver.NamesByCurrentKeyDsResponseNamesByCurrentKeyDsResult namesByCurrentKeyDsResult) {
        this.namesByCurrentKeyDsResult = namesByCurrentKeyDsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NamesByCurrentKeyDsResponse)) return false;
        NamesByCurrentKeyDsResponse other = (NamesByCurrentKeyDsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.namesByCurrentKeyDsResult==null && other.getNamesByCurrentKeyDsResult()==null) || 
             (this.namesByCurrentKeyDsResult!=null &&
              this.namesByCurrentKeyDsResult.equals(other.getNamesByCurrentKeyDsResult())));
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
        if (getNamesByCurrentKeyDsResult() != null) {
            _hashCode += getNamesByCurrentKeyDsResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NamesByCurrentKeyDsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NamesByCurrentKeyDsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("namesByCurrentKeyDsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NamesByCurrentKeyDsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NamesByCurrentKeyDsResponse>NamesByCurrentKeyDsResult"));
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

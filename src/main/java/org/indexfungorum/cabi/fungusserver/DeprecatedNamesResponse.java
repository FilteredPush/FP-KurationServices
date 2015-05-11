/**
 * DeprecatedNamesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class DeprecatedNamesResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult deprecatedNamesResult;

    public DeprecatedNamesResponse() {
    }

    public DeprecatedNamesResponse(
           org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult deprecatedNamesResult) {
           this.deprecatedNamesResult = deprecatedNamesResult;
    }


    /**
     * Gets the deprecatedNamesResult value for this DeprecatedNamesResponse.
     * 
     * @return deprecatedNamesResult
     */
    public org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult getDeprecatedNamesResult() {
        return deprecatedNamesResult;
    }


    /**
     * Sets the deprecatedNamesResult value for this DeprecatedNamesResponse.
     * 
     * @param deprecatedNamesResult
     */
    public void setDeprecatedNamesResult(org.indexfungorum.cabi.fungusserver.DeprecatedNamesResponseDeprecatedNamesResult deprecatedNamesResult) {
        this.deprecatedNamesResult = deprecatedNamesResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DeprecatedNamesResponse)) return false;
        DeprecatedNamesResponse other = (DeprecatedNamesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.deprecatedNamesResult==null && other.getDeprecatedNamesResult()==null) || 
             (this.deprecatedNamesResult!=null &&
              this.deprecatedNamesResult.equals(other.getDeprecatedNamesResult())));
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
        if (getDeprecatedNamesResult() != null) {
            _hashCode += getDeprecatedNamesResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DeprecatedNamesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">DeprecatedNamesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deprecatedNamesResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "DeprecatedNamesResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>DeprecatedNamesResponse>DeprecatedNamesResult"));
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

/**
 * AllUpdatedNamesResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class AllUpdatedNamesResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult allUpdatedNamesResult;

    public AllUpdatedNamesResponse() {
    }

    public AllUpdatedNamesResponse(
           org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult allUpdatedNamesResult) {
           this.allUpdatedNamesResult = allUpdatedNamesResult;
    }


    /**
     * Gets the allUpdatedNamesResult value for this AllUpdatedNamesResponse.
     * 
     * @return allUpdatedNamesResult
     */
    public org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult getAllUpdatedNamesResult() {
        return allUpdatedNamesResult;
    }


    /**
     * Sets the allUpdatedNamesResult value for this AllUpdatedNamesResponse.
     * 
     * @param allUpdatedNamesResult
     */
    public void setAllUpdatedNamesResult(org.indexfungorum.cabi.fungusserver.AllUpdatedNamesResponseAllUpdatedNamesResult allUpdatedNamesResult) {
        this.allUpdatedNamesResult = allUpdatedNamesResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AllUpdatedNamesResponse)) return false;
        AllUpdatedNamesResponse other = (AllUpdatedNamesResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.allUpdatedNamesResult==null && other.getAllUpdatedNamesResult()==null) || 
             (this.allUpdatedNamesResult!=null &&
              this.allUpdatedNamesResult.equals(other.getAllUpdatedNamesResult())));
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
        if (getAllUpdatedNamesResult() != null) {
            _hashCode += getAllUpdatedNamesResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AllUpdatedNamesResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">AllUpdatedNamesResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allUpdatedNamesResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AllUpdatedNamesResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>AllUpdatedNamesResponse>AllUpdatedNamesResult"));
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

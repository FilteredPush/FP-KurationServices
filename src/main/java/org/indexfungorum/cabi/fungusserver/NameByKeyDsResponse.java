/**
 * NameByKeyDsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class NameByKeyDsResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult nameByKeyDsResult;

    public NameByKeyDsResponse() {
    }

    public NameByKeyDsResponse(
           org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult nameByKeyDsResult) {
           this.nameByKeyDsResult = nameByKeyDsResult;
    }


    /**
     * Gets the nameByKeyDsResult value for this NameByKeyDsResponse.
     * 
     * @return nameByKeyDsResult
     */
    public org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult getNameByKeyDsResult() {
        return nameByKeyDsResult;
    }


    /**
     * Sets the nameByKeyDsResult value for this NameByKeyDsResponse.
     * 
     * @param nameByKeyDsResult
     */
    public void setNameByKeyDsResult(org.indexfungorum.cabi.fungusserver.NameByKeyDsResponseNameByKeyDsResult nameByKeyDsResult) {
        this.nameByKeyDsResult = nameByKeyDsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NameByKeyDsResponse)) return false;
        NameByKeyDsResponse other = (NameByKeyDsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nameByKeyDsResult==null && other.getNameByKeyDsResult()==null) || 
             (this.nameByKeyDsResult!=null &&
              this.nameByKeyDsResult.equals(other.getNameByKeyDsResult())));
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
        if (getNameByKeyDsResult() != null) {
            _hashCode += getNameByKeyDsResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NameByKeyDsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameByKeyDsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nameByKeyDsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameByKeyDsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameByKeyDsResponse>NameByKeyDsResult"));
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

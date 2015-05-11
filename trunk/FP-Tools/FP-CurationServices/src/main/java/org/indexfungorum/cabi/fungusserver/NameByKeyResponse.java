/**
 * NameByKeyResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class NameByKeyResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult nameByKeyResult;

    public NameByKeyResponse() {
    }

    public NameByKeyResponse(
           org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult nameByKeyResult) {
           this.nameByKeyResult = nameByKeyResult;
    }


    /**
     * Gets the nameByKeyResult value for this NameByKeyResponse.
     * 
     * @return nameByKeyResult
     */
    public org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult getNameByKeyResult() {
        return nameByKeyResult;
    }


    /**
     * Sets the nameByKeyResult value for this NameByKeyResponse.
     * 
     * @param nameByKeyResult
     */
    public void setNameByKeyResult(org.indexfungorum.cabi.fungusserver.NameByKeyResponseNameByKeyResult nameByKeyResult) {
        this.nameByKeyResult = nameByKeyResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NameByKeyResponse)) return false;
        NameByKeyResponse other = (NameByKeyResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nameByKeyResult==null && other.getNameByKeyResult()==null) || 
             (this.nameByKeyResult!=null &&
              this.nameByKeyResult.equals(other.getNameByKeyResult())));
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
        if (getNameByKeyResult() != null) {
            _hashCode += getNameByKeyResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NameByKeyResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameByKeyResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nameByKeyResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameByKeyResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameByKeyResponse>NameByKeyResult"));
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

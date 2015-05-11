/**
 * NameSearchDsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class NameSearchDsResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult nameSearchDsResult;

    public NameSearchDsResponse() {
    }

    public NameSearchDsResponse(
           org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult nameSearchDsResult) {
           this.nameSearchDsResult = nameSearchDsResult;
    }


    /**
     * Gets the nameSearchDsResult value for this NameSearchDsResponse.
     * 
     * @return nameSearchDsResult
     */
    public org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult getNameSearchDsResult() {
        return nameSearchDsResult;
    }


    /**
     * Sets the nameSearchDsResult value for this NameSearchDsResponse.
     * 
     * @param nameSearchDsResult
     */
    public void setNameSearchDsResult(org.indexfungorum.cabi.fungusserver.NameSearchDsResponseNameSearchDsResult nameSearchDsResult) {
        this.nameSearchDsResult = nameSearchDsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NameSearchDsResponse)) return false;
        NameSearchDsResponse other = (NameSearchDsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.nameSearchDsResult==null && other.getNameSearchDsResult()==null) || 
             (this.nameSearchDsResult!=null &&
              this.nameSearchDsResult.equals(other.getNameSearchDsResult())));
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
        if (getNameSearchDsResult() != null) {
            _hashCode += getNameSearchDsResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NameSearchDsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NameSearchDsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nameSearchDsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NameSearchDsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NameSearchDsResponse>NameSearchDsResult"));
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

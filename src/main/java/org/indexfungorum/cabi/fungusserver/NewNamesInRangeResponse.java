/**
 * NewNamesInRangeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class NewNamesInRangeResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult newNamesInRangeResult;

    public NewNamesInRangeResponse() {
    }

    public NewNamesInRangeResponse(
           org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult newNamesInRangeResult) {
           this.newNamesInRangeResult = newNamesInRangeResult;
    }


    /**
     * Gets the newNamesInRangeResult value for this NewNamesInRangeResponse.
     * 
     * @return newNamesInRangeResult
     */
    public org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult getNewNamesInRangeResult() {
        return newNamesInRangeResult;
    }


    /**
     * Sets the newNamesInRangeResult value for this NewNamesInRangeResponse.
     * 
     * @param newNamesInRangeResult to set
     */
    public void setNewNamesInRangeResult(org.indexfungorum.cabi.fungusserver.NewNamesInRangeResponseNewNamesInRangeResult newNamesInRangeResult) {
        this.newNamesInRangeResult = newNamesInRangeResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof NewNamesInRangeResponse)) return false;
        NewNamesInRangeResponse other = (NewNamesInRangeResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.newNamesInRangeResult==null && other.getNewNamesInRangeResult()==null) || 
             (this.newNamesInRangeResult!=null &&
              this.newNamesInRangeResult.equals(other.getNewNamesInRangeResult())));
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
        if (getNewNamesInRangeResult() != null) {
            _hashCode += getNewNamesInRangeResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(NewNamesInRangeResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">NewNamesInRangeResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("newNamesInRangeResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "NewNamesInRangeResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>NewNamesInRangeResponse>NewNamesInRangeResult"));
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

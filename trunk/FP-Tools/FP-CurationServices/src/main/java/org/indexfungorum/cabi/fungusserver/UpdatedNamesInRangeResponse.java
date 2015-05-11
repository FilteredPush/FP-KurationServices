/**
 * UpdatedNamesInRangeResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class UpdatedNamesInRangeResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult updatedNamesInRangeResult;

    public UpdatedNamesInRangeResponse() {
    }

    public UpdatedNamesInRangeResponse(
           org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult updatedNamesInRangeResult) {
           this.updatedNamesInRangeResult = updatedNamesInRangeResult;
    }


    /**
     * Gets the updatedNamesInRangeResult value for this UpdatedNamesInRangeResponse.
     * 
     * @return updatedNamesInRangeResult
     */
    public org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult getUpdatedNamesInRangeResult() {
        return updatedNamesInRangeResult;
    }


    /**
     * Sets the updatedNamesInRangeResult value for this UpdatedNamesInRangeResponse.
     * 
     * @param updatedNamesInRangeResult
     */
    public void setUpdatedNamesInRangeResult(org.indexfungorum.cabi.fungusserver.UpdatedNamesInRangeResponseUpdatedNamesInRangeResult updatedNamesInRangeResult) {
        this.updatedNamesInRangeResult = updatedNamesInRangeResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UpdatedNamesInRangeResponse)) return false;
        UpdatedNamesInRangeResponse other = (UpdatedNamesInRangeResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.updatedNamesInRangeResult==null && other.getUpdatedNamesInRangeResult()==null) || 
             (this.updatedNamesInRangeResult!=null &&
              this.updatedNamesInRangeResult.equals(other.getUpdatedNamesInRangeResult())));
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
        if (getUpdatedNamesInRangeResult() != null) {
            _hashCode += getUpdatedNamesInRangeResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UpdatedNamesInRangeResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">UpdatedNamesInRangeResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("updatedNamesInRangeResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "UpdatedNamesInRangeResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>UpdatedNamesInRangeResponse>UpdatedNamesInRangeResult"));
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

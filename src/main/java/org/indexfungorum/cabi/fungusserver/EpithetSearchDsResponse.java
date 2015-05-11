/**
 * EpithetSearchDsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class EpithetSearchDsResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult epithetSearchDsResult;

    public EpithetSearchDsResponse() {
    }

    public EpithetSearchDsResponse(
           org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult epithetSearchDsResult) {
           this.epithetSearchDsResult = epithetSearchDsResult;
    }


    /**
     * Gets the epithetSearchDsResult value for this EpithetSearchDsResponse.
     * 
     * @return epithetSearchDsResult
     */
    public org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult getEpithetSearchDsResult() {
        return epithetSearchDsResult;
    }


    /**
     * Sets the epithetSearchDsResult value for this EpithetSearchDsResponse.
     * 
     * @param epithetSearchDsResult
     */
    public void setEpithetSearchDsResult(org.indexfungorum.cabi.fungusserver.EpithetSearchDsResponseEpithetSearchDsResult epithetSearchDsResult) {
        this.epithetSearchDsResult = epithetSearchDsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EpithetSearchDsResponse)) return false;
        EpithetSearchDsResponse other = (EpithetSearchDsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.epithetSearchDsResult==null && other.getEpithetSearchDsResult()==null) || 
             (this.epithetSearchDsResult!=null &&
              this.epithetSearchDsResult.equals(other.getEpithetSearchDsResult())));
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
        if (getEpithetSearchDsResult() != null) {
            _hashCode += getEpithetSearchDsResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EpithetSearchDsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">EpithetSearchDsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("epithetSearchDsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "EpithetSearchDsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>EpithetSearchDsResponse>EpithetSearchDsResult"));
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

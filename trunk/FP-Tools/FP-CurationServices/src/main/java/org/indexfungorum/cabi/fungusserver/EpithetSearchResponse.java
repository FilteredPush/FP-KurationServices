/**
 * EpithetSearchResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class EpithetSearchResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult epithetSearchResult;

    public EpithetSearchResponse() {
    }

    public EpithetSearchResponse(
           org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult epithetSearchResult) {
           this.epithetSearchResult = epithetSearchResult;
    }


    /**
     * Gets the epithetSearchResult value for this EpithetSearchResponse.
     * 
     * @return epithetSearchResult
     */
    public org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult getEpithetSearchResult() {
        return epithetSearchResult;
    }


    /**
     * Sets the epithetSearchResult value for this EpithetSearchResponse.
     * 
     * @param epithetSearchResult
     */
    public void setEpithetSearchResult(org.indexfungorum.cabi.fungusserver.EpithetSearchResponseEpithetSearchResult epithetSearchResult) {
        this.epithetSearchResult = epithetSearchResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EpithetSearchResponse)) return false;
        EpithetSearchResponse other = (EpithetSearchResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.epithetSearchResult==null && other.getEpithetSearchResult()==null) || 
             (this.epithetSearchResult!=null &&
              this.epithetSearchResult.equals(other.getEpithetSearchResult())));
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
        if (getEpithetSearchResult() != null) {
            _hashCode += getEpithetSearchResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EpithetSearchResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">EpithetSearchResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("epithetSearchResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "EpithetSearchResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>EpithetSearchResponse>EpithetSearchResult"));
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

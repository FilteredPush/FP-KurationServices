/**
 * AuthorSearchDsResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class AuthorSearchDsResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult authorSearchDsResult;

    public AuthorSearchDsResponse() {
    }

    public AuthorSearchDsResponse(
           org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult authorSearchDsResult) {
           this.authorSearchDsResult = authorSearchDsResult;
    }


    /**
     * Gets the authorSearchDsResult value for this AuthorSearchDsResponse.
     * 
     * @return authorSearchDsResult
     */
    public org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult getAuthorSearchDsResult() {
        return authorSearchDsResult;
    }


    /**
     * Sets the authorSearchDsResult value for this AuthorSearchDsResponse.
     * 
     * @param authorSearchDsResult
     */
    public void setAuthorSearchDsResult(org.indexfungorum.cabi.fungusserver.AuthorSearchDsResponseAuthorSearchDsResult authorSearchDsResult) {
        this.authorSearchDsResult = authorSearchDsResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AuthorSearchDsResponse)) return false;
        AuthorSearchDsResponse other = (AuthorSearchDsResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.authorSearchDsResult==null && other.getAuthorSearchDsResult()==null) || 
             (this.authorSearchDsResult!=null &&
              this.authorSearchDsResult.equals(other.getAuthorSearchDsResult())));
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
        if (getAuthorSearchDsResult() != null) {
            _hashCode += getAuthorSearchDsResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AuthorSearchDsResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">AuthorSearchDsResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorSearchDsResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AuthorSearchDsResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>AuthorSearchDsResponse>AuthorSearchDsResult"));
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

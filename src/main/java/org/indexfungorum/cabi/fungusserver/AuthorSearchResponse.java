/**
 * AuthorSearchResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class AuthorSearchResponse  implements java.io.Serializable {
    private org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult authorSearchResult;

    public AuthorSearchResponse() {
    }

    public AuthorSearchResponse(
           org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult authorSearchResult) {
           this.authorSearchResult = authorSearchResult;
    }


    /**
     * Gets the authorSearchResult value for this AuthorSearchResponse.
     * 
     * @return authorSearchResult
     */
    public org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult getAuthorSearchResult() {
        return authorSearchResult;
    }


    /**
     * Sets the authorSearchResult value for this AuthorSearchResponse.
     * 
     * @param authorSearchResult
     */
    public void setAuthorSearchResult(org.indexfungorum.cabi.fungusserver.AuthorSearchResponseAuthorSearchResult authorSearchResult) {
        this.authorSearchResult = authorSearchResult;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AuthorSearchResponse)) return false;
        AuthorSearchResponse other = (AuthorSearchResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.authorSearchResult==null && other.getAuthorSearchResult()==null) || 
             (this.authorSearchResult!=null &&
              this.authorSearchResult.equals(other.getAuthorSearchResult())));
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
        if (getAuthorSearchResult() != null) {
            _hashCode += getAuthorSearchResult().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AuthorSearchResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">AuthorSearchResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authorSearchResult");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AuthorSearchResult"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">>AuthorSearchResponse>AuthorSearchResult"));
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

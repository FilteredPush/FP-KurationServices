/**
 * AuthorSearchDs.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.indexfungorum.cabi.fungusserver;

public class AuthorSearchDs  implements java.io.Serializable {
    private java.lang.String searchText;

    private boolean anywhereInText;

    private long maxNumber;

    public AuthorSearchDs() {
    }

    public AuthorSearchDs(
           java.lang.String searchText,
           boolean anywhereInText,
           long maxNumber) {
           this.searchText = searchText;
           this.anywhereInText = anywhereInText;
           this.maxNumber = maxNumber;
    }


    /**
     * Gets the searchText value for this AuthorSearchDs.
     * 
     * @return searchText
     */
    public java.lang.String getSearchText() {
        return searchText;
    }


    /**
     * Sets the searchText value for this AuthorSearchDs.
     * 
     * @param searchText
     */
    public void setSearchText(java.lang.String searchText) {
        this.searchText = searchText;
    }


    /**
     * Gets the anywhereInText value for this AuthorSearchDs.
     * 
     * @return anywhereInText
     */
    public boolean isAnywhereInText() {
        return anywhereInText;
    }


    /**
     * Sets the anywhereInText value for this AuthorSearchDs.
     * 
     * @param anywhereInText
     */
    public void setAnywhereInText(boolean anywhereInText) {
        this.anywhereInText = anywhereInText;
    }


    /**
     * Gets the maxNumber value for this AuthorSearchDs.
     * 
     * @return maxNumber
     */
    public long getMaxNumber() {
        return maxNumber;
    }


    /**
     * Sets the maxNumber value for this AuthorSearchDs.
     * 
     * @param maxNumber
     */
    public void setMaxNumber(long maxNumber) {
        this.maxNumber = maxNumber;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AuthorSearchDs)) return false;
        AuthorSearchDs other = (AuthorSearchDs) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.searchText==null && other.getSearchText()==null) || 
             (this.searchText!=null &&
              this.searchText.equals(other.getSearchText()))) &&
            this.anywhereInText == other.isAnywhereInText() &&
            this.maxNumber == other.getMaxNumber();
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
        if (getSearchText() != null) {
            _hashCode += getSearchText().hashCode();
        }
        _hashCode += (isAnywhereInText() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += new Long(getMaxNumber()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AuthorSearchDs.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://Cabi/FungusServer/", ">AuthorSearchDs"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("searchText");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "SearchText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("anywhereInText");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "AnywhereInText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://Cabi/FungusServer/", "MaxNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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

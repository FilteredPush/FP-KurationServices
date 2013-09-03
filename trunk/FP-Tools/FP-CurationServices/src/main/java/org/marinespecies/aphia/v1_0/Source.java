/**
 * Source.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.marinespecies.aphia.v1_0;

public class Source  implements java.io.Serializable {
    private int source_id;

    private String use;

    private String reference;

    private String page;

    private String url;

    private String link;

    private String fulltext;

    public Source() {
    }

    public Source(
           int source_id,
           String use,
           String reference,
           String page,
           String url,
           String link,
           String fulltext) {
           this.source_id = source_id;
           this.use = use;
           this.reference = reference;
           this.page = page;
           this.url = url;
           this.link = link;
           this.fulltext = fulltext;
    }


    /**
     * Gets the source_id value for this Source.
     *
     * @return source_id
     */
    public int getSource_id() {
        return source_id;
    }


    /**
     * Sets the source_id value for this Source.
     *
     * @param source_id
     */
    public void setSource_id(int source_id) {
        this.source_id = source_id;
    }


    /**
     * Gets the use value for this Source.
     *
     * @return use
     */
    public String getUse() {
        return use;
    }


    /**
     * Sets the use value for this Source.
     *
     * @param use
     */
    public void setUse(String use) {
        this.use = use;
    }


    /**
     * Gets the reference value for this Source.
     *
     * @return reference
     */
    public String getReference() {
        return reference;
    }


    /**
     * Sets the reference value for this Source.
     *
     * @param reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }


    /**
     * Gets the page value for this Source.
     *
     * @return page
     */
    public String getPage() {
        return page;
    }


    /**
     * Sets the page value for this Source.
     *
     * @param page
     */
    public void setPage(String page) {
        this.page = page;
    }


    /**
     * Gets the url value for this Source.
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }


    /**
     * Sets the url value for this Source.
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }


    /**
     * Gets the link value for this Source.
     *
     * @return link
     */
    public String getLink() {
        return link;
    }


    /**
     * Sets the link value for this Source.
     *
     * @param link
     */
    public void setLink(String link) {
        this.link = link;
    }


    /**
     * Gets the fulltext value for this Source.
     *
     * @return fulltext
     */
    public String getFulltext() {
        return fulltext;
    }


    /**
     * Sets the fulltext value for this Source.
     *
     * @param fulltext
     */
    public void setFulltext(String fulltext) {
        this.fulltext = fulltext;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof Source)) return false;
        Source other = (Source) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            this.source_id == other.getSource_id() &&
            ((this.use==null && other.getUse()==null) ||
             (this.use!=null &&
              this.use.equals(other.getUse()))) &&
            ((this.reference==null && other.getReference()==null) ||
             (this.reference!=null &&
              this.reference.equals(other.getReference()))) &&
            ((this.page==null && other.getPage()==null) ||
             (this.page!=null &&
              this.page.equals(other.getPage()))) &&
            ((this.url==null && other.getUrl()==null) ||
             (this.url!=null &&
              this.url.equals(other.getUrl()))) &&
            ((this.link==null && other.getLink()==null) ||
             (this.link!=null &&
              this.link.equals(other.getLink()))) &&
            ((this.fulltext==null && other.getFulltext()==null) ||
             (this.fulltext!=null &&
              this.fulltext.equals(other.getFulltext())));
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
        _hashCode += getSource_id();
        if (getUse() != null) {
            _hashCode += getUse().hashCode();
        }
        if (getReference() != null) {
            _hashCode += getReference().hashCode();
        }
        if (getPage() != null) {
            _hashCode += getPage().hashCode();
        }
        if (getUrl() != null) {
            _hashCode += getUrl().hashCode();
        }
        if (getLink() != null) {
            _hashCode += getLink().hashCode();
        }
        if (getFulltext() != null) {
            _hashCode += getFulltext().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Source.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://aphia/v1.0", "Source"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source_id");
        elemField.setXmlName(new javax.xml.namespace.QName("", "source_id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("use");
        elemField.setXmlName(new javax.xml.namespace.QName("", "use"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reference");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("page");
        elemField.setXmlName(new javax.xml.namespace.QName("", "page"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("url");
        elemField.setXmlName(new javax.xml.namespace.QName("", "url"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("link");
        elemField.setXmlName(new javax.xml.namespace.QName("", "link"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fulltext");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fulltext"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

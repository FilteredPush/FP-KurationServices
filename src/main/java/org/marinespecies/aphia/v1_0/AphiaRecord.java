/**
 * AphiaRecord.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.marinespecies.aphia.v1_0;

public class AphiaRecord  implements java.io.Serializable {
    private int aphiaID;

    private String url;

    private String scientificname;

    private String authority;

    private String rank;

    private String status;

    private int valid_AphiaID;

    private String valid_name;

    private String valid_authority;

    private String kingdom;

    private String phylum;

    private String _class;

    private String order;

    private String family;

    private String genus;

    private String citation;

    private String lsid;

    public AphiaRecord() {
    }

    public AphiaRecord(
           int aphiaID,
           String url,
           String scientificname,
           String authority,
           String rank,
           String status,
           int valid_AphiaID,
           String valid_name,
           String valid_authority,
           String kingdom,
           String phylum,
           String _class,
           String order,
           String family,
           String genus,
           String citation,
           String lsid) {
           this.aphiaID = aphiaID;
           this.url = url;
           this.scientificname = scientificname;
           this.authority = authority;
           this.rank = rank;
           this.status = status;
           this.valid_AphiaID = valid_AphiaID;
           this.valid_name = valid_name;
           this.valid_authority = valid_authority;
           this.kingdom = kingdom;
           this.phylum = phylum;
           this._class = _class;
           this.order = order;
           this.family = family;
           this.genus = genus;
           this.citation = citation;
           this.lsid = lsid;
    }


    /**
     * Gets the aphiaID value for this AphiaRecord.
     *
     * @return aphiaID
     */
    public int getAphiaID() {
        return aphiaID;
    }


    /**
     * Sets the aphiaID value for this AphiaRecord.
     *
     * @param aphiaID
     */
    public void setAphiaID(int aphiaID) {
        this.aphiaID = aphiaID;
    }


    /**
     * Gets the url value for this AphiaRecord.
     *
     * @return url
     */
    public String getUrl() {
        return url;
    }


    /**
     * Sets the url value for this AphiaRecord.
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }


    /**
     * Gets the scientificname value for this AphiaRecord.
     *
     * @return scientificname
     */
    public String getScientificname() {
        return scientificname;
    }


    /**
     * Sets the scientificname value for this AphiaRecord.
     *
     * @param scientificname
     */
    public void setScientificname(String scientificname) {
        this.scientificname = scientificname;
    }


    /**
     * Gets the authority value for this AphiaRecord.
     *
     * @return authority
     */
    public String getAuthority() {
        return authority;
    }


    /**
     * Sets the authority value for this AphiaRecord.
     *
     * @param authority
     */
    public void setAuthority(String authority) {
        this.authority = authority;
    }


    /**
     * Gets the rank value for this AphiaRecord.
     *
     * @return rank
     */
    public String getRank() {
        return rank;
    }


    /**
     * Sets the rank value for this AphiaRecord.
     *
     * @param rank
     */
    public void setRank(String rank) {
        this.rank = rank;
    }


    /**
     * Gets the status value for this AphiaRecord.
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this AphiaRecord.
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * Gets the valid_AphiaID value for this AphiaRecord.
     *
     * @return valid_AphiaID
     */
    public int getValid_AphiaID() {
        return valid_AphiaID;
    }


    /**
     * Sets the valid_AphiaID value for this AphiaRecord.
     *
     * @param valid_AphiaID
     */
    public void setValid_AphiaID(int valid_AphiaID) {
        this.valid_AphiaID = valid_AphiaID;
    }


    /**
     * Gets the valid_name value for this AphiaRecord.
     *
     * @return valid_name
     */
    public String getValid_name() {
        return valid_name;
    }


    /**
     * Sets the valid_name value for this AphiaRecord.
     *
     * @param valid_name
     */
    public void setValid_name(String valid_name) {
        this.valid_name = valid_name;
    }


    /**
     * Gets the valid_authority value for this AphiaRecord.
     *
     * @return valid_authority
     */
    public String getValid_authority() {
        return valid_authority;
    }


    /**
     * Sets the valid_authority value for this AphiaRecord.
     *
     * @param valid_authority
     */
    public void setValid_authority(String valid_authority) {
        this.valid_authority = valid_authority;
    }


    /**
     * Gets the kingdom value for this AphiaRecord.
     *
     * @return kingdom
     */
    public String getKingdom() {
        return kingdom;
    }


    /**
     * Sets the kingdom value for this AphiaRecord.
     *
     * @param kingdom
     */
    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }


    /**
     * Gets the phylum value for this AphiaRecord.
     *
     * @return phylum
     */
    public String getPhylum() {
        return phylum;
    }


    /**
     * Sets the phylum value for this AphiaRecord.
     *
     * @param phylum
     */
    public void setPhylum(String phylum) {
        this.phylum = phylum;
    }


    /**
     * Gets the _class value for this AphiaRecord.
     *
     * @return _class
     */
    public String get_class() {
        return _class;
    }


    /**
     * Sets the _class value for this AphiaRecord.
     *
     * @param _class
     */
    public void set_class(String _class) {
        this._class = _class;
    }


    /**
     * Gets the order value for this AphiaRecord.
     *
     * @return order
     */
    public String getOrder() {
        return order;
    }


    /**
     * Sets the order value for this AphiaRecord.
     *
     * @param order
     */
    public void setOrder(String order) {
        this.order = order;
    }


    /**
     * Gets the family value for this AphiaRecord.
     *
     * @return family
     */
    public String getFamily() {
        return family;
    }


    /**
     * Sets the family value for this AphiaRecord.
     *
     * @param family
     */
    public void setFamily(String family) {
        this.family = family;
    }


    /**
     * Gets the genus value for this AphiaRecord.
     *
     * @return genus
     */
    public String getGenus() {
        return genus;
    }


    /**
     * Sets the genus value for this AphiaRecord.
     *
     * @param genus
     */
    public void setGenus(String genus) {
        this.genus = genus;
    }


    /**
     * Gets the citation value for this AphiaRecord.
     *
     * @return citation
     */
    public String getCitation() {
        return citation;
    }


    /**
     * Sets the citation value for this AphiaRecord.
     *
     * @param citation
     */
    public void setCitation(String citation) {
        this.citation = citation;
    }


    /**
     * Gets the lsid value for this AphiaRecord.
     *
     * @return lsid
     */
    public String getLsid() {
        return lsid;
    }


    /**
     * Sets the lsid value for this AphiaRecord.
     *
     * @param lsid
     */
    public void setLsid(String lsid) {
        this.lsid = lsid;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof AphiaRecord)) return false;
        AphiaRecord other = (AphiaRecord) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            this.aphiaID == other.getAphiaID() &&
            ((this.url==null && other.getUrl()==null) ||
             (this.url!=null &&
              this.url.equals(other.getUrl()))) &&
            ((this.scientificname==null && other.getScientificname()==null) ||
             (this.scientificname!=null &&
              this.scientificname.equals(other.getScientificname()))) &&
            ((this.authority==null && other.getAuthority()==null) ||
             (this.authority!=null &&
              this.authority.equals(other.getAuthority()))) &&
            ((this.rank==null && other.getRank()==null) ||
             (this.rank!=null &&
              this.rank.equals(other.getRank()))) &&
            ((this.status==null && other.getStatus()==null) ||
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            this.valid_AphiaID == other.getValid_AphiaID() &&
            ((this.valid_name==null && other.getValid_name()==null) ||
             (this.valid_name!=null &&
              this.valid_name.equals(other.getValid_name()))) &&
            ((this.valid_authority==null && other.getValid_authority()==null) ||
             (this.valid_authority!=null &&
              this.valid_authority.equals(other.getValid_authority()))) &&
            ((this.kingdom==null && other.getKingdom()==null) ||
             (this.kingdom!=null &&
              this.kingdom.equals(other.getKingdom()))) &&
            ((this.phylum==null && other.getPhylum()==null) ||
             (this.phylum!=null &&
              this.phylum.equals(other.getPhylum()))) &&
            ((this._class==null && other.get_class()==null) ||
             (this._class!=null &&
              this._class.equals(other.get_class()))) &&
            ((this.order==null && other.getOrder()==null) ||
             (this.order!=null &&
              this.order.equals(other.getOrder()))) &&
            ((this.family==null && other.getFamily()==null) ||
             (this.family!=null &&
              this.family.equals(other.getFamily()))) &&
            ((this.genus==null && other.getGenus()==null) ||
             (this.genus!=null &&
              this.genus.equals(other.getGenus()))) &&
            ((this.citation==null && other.getCitation()==null) ||
             (this.citation!=null &&
              this.citation.equals(other.getCitation()))) &&
            ((this.lsid==null && other.getLsid()==null) ||
             (this.lsid!=null &&
              this.lsid.equals(other.getLsid())));
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
        _hashCode += getAphiaID();
        if (getUrl() != null) {
            _hashCode += getUrl().hashCode();
        }
        if (getScientificname() != null) {
            _hashCode += getScientificname().hashCode();
        }
        if (getAuthority() != null) {
            _hashCode += getAuthority().hashCode();
        }
        if (getRank() != null) {
            _hashCode += getRank().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        _hashCode += getValid_AphiaID();
        if (getValid_name() != null) {
            _hashCode += getValid_name().hashCode();
        }
        if (getValid_authority() != null) {
            _hashCode += getValid_authority().hashCode();
        }
        if (getKingdom() != null) {
            _hashCode += getKingdom().hashCode();
        }
        if (getPhylum() != null) {
            _hashCode += getPhylum().hashCode();
        }
        if (get_class() != null) {
            _hashCode += get_class().hashCode();
        }
        if (getOrder() != null) {
            _hashCode += getOrder().hashCode();
        }
        if (getFamily() != null) {
            _hashCode += getFamily().hashCode();
        }
        if (getGenus() != null) {
            _hashCode += getGenus().hashCode();
        }
        if (getCitation() != null) {
            _hashCode += getCitation().hashCode();
        }
        if (getLsid() != null) {
            _hashCode += getLsid().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AphiaRecord.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://aphia/v1.0", "AphiaRecord"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aphiaID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "AphiaID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("url");
        elemField.setXmlName(new javax.xml.namespace.QName("", "url"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scientificname");
        elemField.setXmlName(new javax.xml.namespace.QName("", "scientificname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("authority");
        elemField.setXmlName(new javax.xml.namespace.QName("", "authority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rank");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rank"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valid_AphiaID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "valid_AphiaID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valid_name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "valid_name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("valid_authority");
        elemField.setXmlName(new javax.xml.namespace.QName("", "valid_authority"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("kingdom");
        elemField.setXmlName(new javax.xml.namespace.QName("", "kingdom"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("phylum");
        elemField.setXmlName(new javax.xml.namespace.QName("", "phylum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("_class");
        elemField.setXmlName(new javax.xml.namespace.QName("", "class"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("order");
        elemField.setXmlName(new javax.xml.namespace.QName("", "order"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("family");
        elemField.setXmlName(new javax.xml.namespace.QName("", "family"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("genus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "genus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("citation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "citation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lsid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lsid"));
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

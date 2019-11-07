/**
 * AphiaRecord.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.marinespecies.aphia.v1_0;

public class AphiaRecord  implements java.io.Serializable {

/* Current (2019 Nov 6) elements in the AphiaRecord in the WSDL (for v1.0, which keeps changing without a version number change).
<xsd:all>
<xsd:element name="AphiaID" type="xsd:int"/>
<xsd:element name="url" type="xsd:string"/>
<xsd:element name="scientificname" type="xsd:string"/>
<xsd:element name="authority" type="xsd:string"/>
<xsd:element name="taxonRankID" type="xsd:int"/>
<xsd:element name="rank" type="xsd:string"/>
<xsd:element name="status" type="xsd:string"/>
<xsd:element name="unacceptreason" type="xsd:string"/>
<xsd:element name="valid_AphiaID" type="xsd:int"/>
<xsd:element name="valid_name" type="xsd:string"/>
<xsd:element name="valid_authority" type="xsd:string"/>
<xsd:element name="parentNameUsageID" type="xsd:int"/>
<xsd:element name="kingdom" type="xsd:string"/>
<xsd:element name="phylum" type="xsd:string"/>
<xsd:element name="class" type="xsd:string"/>
<xsd:element name="order" type="xsd:string"/>
<xsd:element name="family" type="xsd:string"/>
<xsd:element name="genus" type="xsd:string"/>
<xsd:element name="citation" type="xsd:string"/>
<xsd:element name="lsid" type="xsd:string"/>
<xsd:element name="isMarine" type="xsd:int"/>
<xsd:element name="isBrackish" type="xsd:int"/>
<xsd:element name="isFreshwater" type="xsd:int"/>
<xsd:element name="isTerrestrial" type="xsd:int"/>
<xsd:element name="isExtinct" type="xsd:int"/>
<xsd:element name="match_type" type="xsd:string"/>
<xsd:element name="modified" type="xsd:string"/>
</xsd:all>
*/

    private int aphiaID;
    private java.lang.String url;
    private java.lang.String scientificname;
    private java.lang.String authority;
    private java.lang.String rank;
    private java.lang.Integer taxonRankID;  // new element, but API version still 1.0
    private java.lang.String status;
    private java.lang.String unacceptreason;
    private int valid_AphiaID;
    private java.lang.String valid_name;
    private java.lang.String valid_authority;
    private java.lang.Integer parentNameUsageID;  // new element, but API version still 1.0
    private java.lang.String kingdom;
    private java.lang.String phylum;
    private java.lang.String _class;
    private java.lang.String order;
    private java.lang.String family;
    private java.lang.String genus;
    private java.lang.String citation;
    private java.lang.String lsid;
    private int isMarine;
    private int isBrackish;
    private int isFreshwater;
    private int isTerrestrial;
    private int isExtinct;
    private java.lang.String match_type;
    private java.lang.String modified;

    public AphiaRecord() {
    }

    public AphiaRecord(
           int aphiaID,
           java.lang.String url,
           java.lang.String scientificname,
           java.lang.String authority,
           java.lang.String rank,
           java.lang.Integer taxonRankID,
           java.lang.Integer parentNameUsageID,
           java.lang.String status,
           java.lang.String unacceptreason,
           int valid_AphiaID,
           java.lang.String valid_name,
           java.lang.String valid_authority,
           java.lang.String kingdom,
           java.lang.String phylum,
           java.lang.String _class,
           java.lang.String order,
           java.lang.String family,
           java.lang.String genus,
           java.lang.String citation,
           java.lang.String lsid,
           int isMarine,
           int isBrackish,
           int isFreshwater,
           int isTerrestrial,
           int isExtinct,
           java.lang.String match_type,
           java.lang.String modified) {
           this.aphiaID = aphiaID;
           this.url = url;
           this.scientificname = scientificname;
           this.authority = authority;
           this.rank = rank;
           this.taxonRankID = taxonRankID;
           this.parentNameUsageID = parentNameUsageID;
           this.status = status;
           this.unacceptreason = unacceptreason;
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
           this.isMarine = isMarine;
           this.isBrackish = isBrackish;
           this.isFreshwater = isFreshwater;
           this.isTerrestrial = isTerrestrial;
           this.isExtinct = isExtinct;
           this.match_type = match_type;
           this.modified = modified;
    }


    /**
     * Gets the aphiaID value for this AphiaRecord.
     * 
     * @return the aphiaID 
     */
    public int getAphiaID() {
        return aphiaID;
    }


    /**
     * Sets the aphiaID value for this AphiaRecord.
     * 
     * @param aphiaID to set
     */
    public void setAphiaID(int aphiaID) {
        this.aphiaID = aphiaID;
    }


    /**
     * Gets the url value for this AphiaRecord.
     * 
     * @return url
     */
    public java.lang.String getUrl() {
        return url;
    }


    /**
     * Sets the url value for this AphiaRecord.
     * 
     * @param url to set
     */
    public void setUrl(java.lang.String url) {
        this.url = url;
    }


    /**
     * Gets the scientificname value for this AphiaRecord.
     * 
     * @return scientificname
     */
    public java.lang.String getScientificname() {
        return scientificname;
    }


    /**
     * Sets the scientificname value for this AphiaRecord.
     * 
     * @param scientificname to set
     */
    public void setScientificname(java.lang.String scientificname) {
        this.scientificname = scientificname;
    }


    /**
     * Gets the authority value for this AphiaRecord.
     * 
     * @return authority
     */
    public java.lang.String getAuthority() {
        return authority;
    }


    /**
     * Sets the authority value for this AphiaRecord.
     * 
     * @param authority to set
     */
    public void setAuthority(java.lang.String authority) {
        this.authority = authority;
    }


    /**
     * Gets the rank value for this AphiaRecord.
     * 
     * @return rank
     */
    public java.lang.String getRank() {
        return rank;
    }

    public java.lang.Integer getTaxonRankID() {
        return taxonRankID;
    }

    /**
     * Sets the rank value for this AphiaRecord.
     * 
     * @param rank to set
     */
    public void setRank(java.lang.String rank) {
        this.rank = rank;
    }

    public void setTaxonRankID(java.lang.Integer taxonRankID) {
        this.taxonRankID = taxonRankID;
    }

    public java.lang.Integer getParentNameUsageID() {
        return parentNameUsageID;
    }
    public void setParentNameUsageID(java.lang.Integer parentNameUsageID) {
        this.parentNameUsageID = parentNameUsageID;
    }
    /**
     * Gets the status value for this AphiaRecord.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this AphiaRecord.
     * 
     * @param status to set
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the unacceptreason value for this AphiaRecord.
     * 
     * @return unacceptreason
     */
    public java.lang.String getUnacceptreason() {
        return unacceptreason;
    }


    /**
     * Sets the unacceptreason value for this AphiaRecord.
     * 
     * @param unacceptreason to set
     */
    public void setUnacceptreason(java.lang.String unacceptreason) {
        this.unacceptreason = unacceptreason;
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
     * @param valid_AphiaID to set
     */
    public void setValid_AphiaID(int valid_AphiaID) {
        this.valid_AphiaID = valid_AphiaID;
    }


    /**
     * Gets the valid_name value for this AphiaRecord.
     * 
     * @return valid_name
     */
    public java.lang.String getValid_name() {
        return valid_name;
    }


    /**
     * Sets the valid_name value for this AphiaRecord.
     * 
     * @param valid_name to set
     */
    public void setValid_name(java.lang.String valid_name) {
        this.valid_name = valid_name;
    }


    /**
     * Gets the valid_authority value for this AphiaRecord.
     * 
     * @return valid_authority
     */
    public java.lang.String getValid_authority() {
        return valid_authority;
    }


    /**
     * Sets the valid_authority value for this AphiaRecord.
     * 
     * @param valid_authority to set
     */
    public void setValid_authority(java.lang.String valid_authority) {
        this.valid_authority = valid_authority;
    }


    /**
     * Gets the kingdom value for this AphiaRecord.
     * 
     * @return kingdom
     */
    public java.lang.String getKingdom() {
        return kingdom;
    }


    /**
     * Sets the kingdom value for this AphiaRecord.
     * 
     * @param kingdom to set
     */
    public void setKingdom(java.lang.String kingdom) {
        this.kingdom = kingdom;
    }


    /**
     * Gets the phylum value for this AphiaRecord.
     * 
     * @return phylum 
     */
    public java.lang.String getPhylum() {
        return phylum;
    }


    /**
     * Sets the phylum value for this AphiaRecord.
     * 
     * @param phylum to set
     */
    public void setPhylum(java.lang.String phylum) {
        this.phylum = phylum;
    }


    /**
     * Gets the _class value for this AphiaRecord.
     * 
     * @return _class
     */
    public java.lang.String get_class() {
        return _class;
    }


    /**
     * Sets the _class value for this AphiaRecord.
     * 
     * @param _class to set
     */
    public void set_class(java.lang.String _class) {
        this._class = _class;
    }


    /**
     * Gets the order value for this AphiaRecord.
     * 
     * @return order
     */
    public java.lang.String getOrder() {
        return order;
    }


    /**
     * Sets the order value for this AphiaRecord.
     * 
     * @param order to set
     */
    public void setOrder(java.lang.String order) {
        this.order = order;
    }


    /**
     * Gets the family value for this AphiaRecord.
     * 
     * @return family
     */
    public java.lang.String getFamily() {
        return family;
    }


    /**
     * Sets the family value for this AphiaRecord.
     * 
     * @param family to set
     */
    public void setFamily(java.lang.String family) {
        this.family = family;
    }


    /**
     * Gets the genus value for this AphiaRecord.
     * 
     * @return genus
     */
    public java.lang.String getGenus() {
        return genus;
    }


    /**
     * Sets the genus value for this AphiaRecord.
     * 
     * @param genus to set 
     */
    public void setGenus(java.lang.String genus) {
        this.genus = genus;
    }


    /**
     * Gets the citation value for this AphiaRecord.
     * 
     * @return citation
     */
    public java.lang.String getCitation() {
        return citation;
    }


    /**
     * Sets the citation value for this AphiaRecord.
     * 
     * @param citation to set
     */
    public void setCitation(java.lang.String citation) {
        this.citation = citation;
    }


    /**
     * Gets the lsid value for this AphiaRecord.
     * 
     * @return lsid
     */
    public java.lang.String getLsid() {
        return lsid;
    }


    /**
     * Sets the lsid value for this AphiaRecord.
     * 
     * @param lsid to set
     */
    public void setLsid(java.lang.String lsid) {
        this.lsid = lsid;
    }


    /**
     * Gets the isMarine value for this AphiaRecord.
     * 
     * @return isMarine
     */
    public int getIsMarine() {
        return isMarine;
    }


    /**
     * Sets the isMarine value for this AphiaRecord.
     * 
     * @param isMarine to set
     */
    public void setIsMarine(int isMarine) {
        this.isMarine = isMarine;
    }


    /**
     * Gets the isBrackish value for this AphiaRecord.
     * 
     * @return isBrackish
     */
    public int getIsBrackish() {
        return isBrackish;
    }


    /**
     * Sets the isBrackish value for this AphiaRecord.
     * 
     * @param isBrackish to set
     */
    public void setIsBrackish(int isBrackish) {
        this.isBrackish = isBrackish;
    }


    /**
     * Gets the isFreshwater value for this AphiaRecord.
     * 
     * @return isFreshwater
     */
    public int getIsFreshwater() {
        return isFreshwater;
    }


    /**
     * Sets the isFreshwater value for this AphiaRecord.
     * 
     * @param isFreshwater to set
     */
    public void setIsFreshwater(int isFreshwater) {
        this.isFreshwater = isFreshwater;
    }


    /**
     * Gets the isTerrestrial value for this AphiaRecord.
     * 
     * @return isTerrestrial
     */
    public int getIsTerrestrial() {
        return isTerrestrial;
    }


    /**
     * Sets the isTerrestrial value for this AphiaRecord.
     * 
     * @param isTerrestrial to set
     */
    public void setIsTerrestrial(int isTerrestrial) {
        this.isTerrestrial = isTerrestrial;
    }


    /**
     * Gets the isExtinct value for this AphiaRecord.
     * 
     * @return isExtinct
     */
    public int getIsExtinct() {
        return isExtinct;
    }


    /**
     * Sets the isExtinct value for this AphiaRecord.
     * 
     * @param isExtinct to set
     */
    public void setIsExtinct(int isExtinct) {
        this.isExtinct = isExtinct;
    }


    /**
     * Gets the match_type value for this AphiaRecord.
     * 
     * @return match_type
     */
    public java.lang.String getMatch_type() {
        return match_type;
    }


    /**
     * Sets the match_type value for this AphiaRecord.
     * 
     * @param match_type to set
     */
    public void setMatch_type(java.lang.String match_type) {
        this.match_type = match_type;
    }


    /**
     * Gets the modified value for this AphiaRecord.
     * 
     * @return modified
     */
    public java.lang.String getModified() {
        return modified;
    }


    /**
     * Sets the modified value for this AphiaRecord.
     * 
     * @param modified to set
     */
    public void setModified(java.lang.String modified) {
        this.modified = modified;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
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
            ((this.taxonRankID==null && other.getTaxonRankID()==null) || 
             (this.taxonRankID!=null &&
              this.taxonRankID.equals(other.getTaxonRankID()))) &&
            ((this.parentNameUsageID==null && other.getParentNameUsageID()==null) || 
             (this.parentNameUsageID!=null &&
              this.parentNameUsageID.equals(other.getParentNameUsageID()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.unacceptreason==null && other.getUnacceptreason()==null) || 
             (this.unacceptreason!=null &&
              this.unacceptreason.equals(other.getUnacceptreason()))) &&
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
              this.lsid.equals(other.getLsid()))) &&
            this.isMarine == other.getIsMarine() &&
            this.isBrackish == other.getIsBrackish() &&
            this.isFreshwater == other.getIsFreshwater() &&
            this.isTerrestrial == other.getIsTerrestrial() &&
            this.isExtinct == other.getIsExtinct() &&
            ((this.match_type==null && other.getMatch_type()==null) || 
             (this.match_type!=null &&
              this.match_type.equals(other.getMatch_type()))) &&
            ((this.modified==null && other.getModified()==null) || 
             (this.modified!=null &&
              this.modified.equals(other.getModified())));
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
        if (getUnacceptreason() != null) {
            _hashCode += getUnacceptreason().hashCode();
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
        _hashCode += getIsMarine();
        _hashCode += getIsBrackish();
        _hashCode += getIsFreshwater();
        _hashCode += getIsTerrestrial();
        _hashCode += getIsExtinct();
        if (getMatch_type() != null) {
            _hashCode += getMatch_type().hashCode();
        }
        if (getModified() != null) {
            _hashCode += getModified().hashCode();
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
        elemField.setFieldName("taxonRankID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "taxonRankID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentNameUsageID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "parentNameUsageID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("unacceptreason");
        elemField.setXmlName(new javax.xml.namespace.QName("", "unacceptreason"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isMarine");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isMarine"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isBrackish");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isBrackish"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isFreshwater");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isFreshwater"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isTerrestrial");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isTerrestrial"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isExtinct");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isExtinct"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("match_type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "match_type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modified");
        elemField.setXmlName(new javax.xml.namespace.QName("", "modified"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /*
     * Return type metadata object
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

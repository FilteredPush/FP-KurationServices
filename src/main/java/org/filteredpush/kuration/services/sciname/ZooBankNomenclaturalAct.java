/**
 * ZooBankNomenclaturalAct.java
 *
 * Copyright 2015 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.filteredpush.kuration.services.sciname;

import org.json.simple.JSONObject;


/**
 * Data object for Nomenclatural Acts returned by ZooBank's service.
 * 
 * // Documentation at: http://zoobank.org/Api
 * // example call http://zoobank.org/NomenclaturalActs.json/Pseudanthias_carlsoni
 * 
 * @author mole
 *
 */
public class ZooBankNomenclaturalAct {
	
	/*
     
    http://zoobank.org/NomenclaturalActs.json/Pseudanthias_carlsoni
	 
	[{"tnuuuid":"","OriginalReferenceUUID":"427D7953-E8FC-41E8-BEA7-8AE644E6DE77",
"protonymuuid": "6EA8BB2A-A57B-47C1-953E-042D8CD8E0E2", "label": "carlsoni Randall & Pyle 2001", 
"value": "carlsoni Randall & Pyle 2001",
"lsid":"urn:lsid:zoobank.org:act:6EA8BB2A-A57B-47C1-953E-042D8CD8E0E2",
"parentname":"","namestring":"carlsoni","rankgroup":"Species","usageauthors":"",
"taxonnamerankid":"","parentusageuuid":"",
"cleanprotonym" : "Pseudanthias carlsoni Randall & Pyle, 2001","NomenclaturalCode":"ICZN"}]		  
		 
	 */
	
	private String tnuuuid;
	private String originalReferenceUUID;
	private String protonymUUID;
	private String label;
	private String value;
	private String lsid;
	private String parentname;
	private String namestring;
	private String rankgroup;
	private String usageauthors;
	private String taxonnameRankId;
	private String parentUsageUUID;
	private String cleanprotonym;
	private String nomenclaturalCode;
	
	public ZooBankNomenclaturalAct(JSONObject jsonTNU) { 
		tnuuuid = jsonTNU.get("tnuuuid").toString();
		originalReferenceUUID = jsonTNU.get("OriginalReferenceUUID").toString();
		protonymUUID = jsonTNU.get("protonymuuid").toString();
		label = jsonTNU.get("label").toString();
		value = jsonTNU.get("value").toString();
		lsid = jsonTNU.get("lsid").toString();
		parentname = jsonTNU.get("parentname").toString();
		namestring = jsonTNU.get("namestring").toString();
		rankgroup = jsonTNU.get("rankgroup").toString();
		usageauthors = jsonTNU.get("usageauthors").toString();
		taxonnameRankId = jsonTNU.get("taxonnamerankid").toString();
		parentUsageUUID = jsonTNU.get("parentusageuuid").toString();
		cleanprotonym = jsonTNU.get("cleanprotonym").toString();
		nomenclaturalCode = jsonTNU.get("NomenclaturalCode").toString();
	}

	/**
	 * @return the tnuuuid
	 */
	public String getTnuuuid() {
		return tnuuuid;
	}

	/**
	 * @param tnuuuid the tnuuuid to set
	 */
	public void setTnuuuid(String tnuuuid) {
		this.tnuuuid = tnuuuid;
	}

	/**
	 * @return the originalReferenceUUID
	 */
	public String getOriginalReferenceUUID() {
		return originalReferenceUUID;
	}

	/**
	 * @param originalReferenceUUID the originalReferenceUUID to set
	 */
	public void setOriginalReferenceUUID(String originalReferenceUUID) {
		this.originalReferenceUUID = originalReferenceUUID;
	}

	/**
	 * @return the protonymUUID
	 */
	public String getProtonymUUID() {
		return protonymUUID;
	}

	/**
	 * @param protonymUUID the protonymUUID to set
	 */
	public void setProtonymUUID(String protonymUUID) {
		this.protonymUUID = protonymUUID;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the lsid
	 */
	public String getLsid() {
		return lsid;
	}

	/**
	 * @param lsid the lsid to set
	 */
	public void setLsid(String lsid) {
		this.lsid = lsid;
	}

	/**
	 * @return the parentname
	 */
	public String getParentname() {
		return parentname;
	}

	/**
	 * @param parentname the parentname to set
	 */
	public void setParentname(String parentname) {
		this.parentname = parentname;
	}

	/**
	 * @return the namestring
	 */
	public String getNamestring() {
		return namestring;
	}

	/**
	 * @param namestring the namestring to set
	 */
	public void setNamestring(String namestring) {
		this.namestring = namestring;
	}

	/**
	 * @return the rankgroup
	 */
	public String getRankgroup() {
		return rankgroup;
	}

	/**
	 * @param rankgroup the rankgroup to set
	 */
	public void setRankgroup(String rankgroup) {
		this.rankgroup = rankgroup;
	}

	/**
	 * @return the usageauthors
	 */
	public String getUsageauthors() {
		return usageauthors;
	}

	/**
	 * @param usageauthors the usageauthors to set
	 */
	public void setUsageauthors(String usageauthors) {
		this.usageauthors = usageauthors;
	}

	/**
	 * @return the taxonnameRankId
	 */
	public String getTaxonnameRankId() {
		return taxonnameRankId;
	}

	/**
	 * @param taxonnameRankId the taxonnameRankId to set
	 */
	public void setTaxonnameRankId(String taxonnameRankId) {
		this.taxonnameRankId = taxonnameRankId;
	}

	/**
	 * @return the parentUsageUUID
	 */
	public String getParentUsageUUID() {
		return parentUsageUUID;
	}

	/**
	 * @param parentUsageUUID the parentUsageUUID to set
	 */
	public void setParentUsageUUID(String parentUsageUUID) {
		this.parentUsageUUID = parentUsageUUID;
	}

	/**
	 * @return the cleanprotonym
	 */
	public String getCleanprotonym() {
		return cleanprotonym;
	}

	/**
	 * @param cleanprotonym the cleanprotonym to set
	 */
	public void setCleanprotonym(String cleanprotonym) {
		this.cleanprotonym = cleanprotonym;
	}

	/**
	 * @return the nomenclaturalCode
	 */
	public String getNomenclaturalCode() {
		return nomenclaturalCode;
	}

	/**
	 * @param nomenclaturalCode the nomenclaturalCode to set
	 */
	public void setNomenclaturalCode(String nomenclaturalCode) {
		this.nomenclaturalCode = nomenclaturalCode;
	}

}

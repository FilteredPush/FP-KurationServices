/** 
 * NameUsage.java 
 * 
 * Copyright 2014 Global Biodiversity Information Facility (GBIF)
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
package edu.harvard.mcz.nametools;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.api.model.common.LinneanClassification;
import org.gbif.api.util.ClassificationUtils;
import org.gbif.api.vocabulary.Rank;
import org.gbif.nameparser.NameParser;
import org.gbif.nameparser.UnparsableException;
import org.json.simple.JSONObject;
import org.marinespecies.aphia.v1_0.AphiaRecord;
import org.supercsv.encoder.CsvEncoder;
import org.supercsv.encoder.DefaultCsvEncoder;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.AlwaysQuoteMode;
import org.supercsv.quote.QuoteMode;
import org.supercsv.util.CsvContext;

/**
 * Representation of a usage of a scientific name, suitable for validation of names against 
 * authoritative sources.  Derived from an object that can handle NameUsage serializations 
 * returned from GBIF's API, with extensions to support similar data objects returned by 
 * the WoRMS aphia API, and by IndexFungorum, along with extensions to support metadata about 
 * an original pre-validation record and the comparision between pre- and post- validation 
 * records.  Derived in part from the GBIF api class of the same name.
 * 
 * Has responsibility for NameUsage data objects, serialization of those objects, comparison 
 * between values (e.g. authorships) between name usages, and assertion of constants describing
 * the nature of such comparisons.  Needs re-engineering.
 * 
 * @author mole
 *
 * $Id: NameUsage.java 469 2015-03-27 20:32:26Z mole $ 
 */
public class NameUsage implements LinneanClassification {
	
	private static final Log log = LogFactory.getLog(NameUsage.class);
	
	private int key;  // GBIF key
	private int acceptedKey;  // GBIF pointer to accepted name record
	private String datasetKey;  // GBIF dataset
	private int parentKey;  // GBIF pointer to parent record in taxonomic heirarchy
	private String parent;
	private String acceptedName;  
	private String scientificName;
	private String canonicalName;
	private String authorship;  // authorship string to accompany the scientificName 
	private String taxonomicStatus;
	private String rank;
	private String kingdom;  // classification
	private String phylum;   // classification
	private String tclass;   // classification
	private String order;    // classification
	private String family;   // classification
	private String genus;    // classification
	private String subgenus; // classification
	private String species;  // classification, the binomial
	private int numDescendants;
	private String sourceID;
	private String link;
	private String sourceAuthority;  // Aphia metadata
	private String unacceptReason;   // Aphia metadata
	private String guid;             // GUID for the name usage
	
	private String matchDescription;  // metadata, description of the match between this name usage annd the original
	private double authorshipStringSimilarity;
	private double scientificNameStringSimilarity;
	
	private int inputDbPK;  // Original database primary key for input
	private String originalScientificName;  
	private String originalAuthorship;
	
	protected CsvPreference prefs;
	protected CsvEncoder encoder;
	protected CsvContext briefContext = null;
	
	protected AuthorNameComparator authorComparator;
	
	public NameUsage() { 
		authorComparator = new ICZNAuthorNameComparator(.75d,.5d);
	}
	
	/**
	 * Construct a NameUsage instance with a given source authority
	 * and authorship comparator.
	 * 
	 * @param sourceAuthority the source authority for the name usage.
	 * @param authorNameComparator the comparator to use when making comparisons
	 * of authors with this name usage.
	 */
	public NameUsage(String sourceAuthority, AuthorNameComparator authorNameComparator) {
		this.authorComparator = authorNameComparator;
		this.sourceAuthority = sourceAuthority;
	}
	
	/**
	 * Construct a NameUsage instance from an AphiaRecord instance,
	 * assumes that the source authority is WoRMS.
	 * 
	 * @param record an AphiaRecord from WoRMS.
	 */
	public NameUsage(AphiaRecord record) {
		authorComparator = new ICZNAuthorNameComparator(.75d,.5d);
		this.setSourceAuthority("WoRMS (World Register of Marine Species)");
		this.setScientificName(record.getScientificname());
		this.setRank(record.getRank());
		this.setAuthorship(record.getAuthority());
		this.setAcceptedName(record.getValid_name());
		this.setKingdom(record.getKingdom());
		this.setPhylum(record.getPhylum());
		this.setTclass(record.get_class());
		this.setOrder(record.getOrder());
		this.setFamily(record.getFamily());
		this.setGenus(record.getGenus());
		this.setGuid("urn:lsid:marinespecies.org:taxname:" + Integer.toString(record.getAphiaID()));
		this.setTaxonomicStatus(record.getStatus());
		this.setUnacceptReason(record.getUnacceptreason());
	}
	
	/**
	 * Construct a NameUsage instance from a gbif checklistbank NameUsage instance.
	 * 
	 * @param record a gbif Checklist API NameUsage.
	 * @see org.gbif.api.model.checklistbank.NameUsage
	 */
	public NameUsage(org.gbif.api.model.checklistbank.NameUsage record) {
		if (record.getDatasetKey().equals(GBIFDataSource.KEY_GBIFBACKBONE)) { 
		    this.setSourceAuthority("GBIF Backbone Taxonomy");
		} else { 
			this.setSourceAuthority("GBIF Dataset " + record.getDatasetKey());
		}
		this.setScientificName(record.getScientificName());
		this.setRank(record.getRank().getMarker());
		this.setAuthorship(record.getAuthorship());
		this.setAcceptedName(record.getAccepted());
		this.setKingdom(record.getKingdom());
		this.setPhylum(record.getPhylum());
		this.setTclass(record.getClazz());
		this.setOrder(record.getOrder());
		this.setFamily(record.getFamily());
		this.setGenus(record.getGenus());		
	}
	
	
	/**
	 * Return the value associated with a key from a JSON object, or an empty string if 
	 * the key is not matched.
	 * 
	 * @param json JSONObject to check for key-value pair
	 * @param key the key for which to find the value for.
	 * @return String value or an empty string.
	 */
	public static String getValFromKey(JSONObject json, String key) { 
		if (json==null || json.get(key)==null) { 
			return "";
		} else { 
			return json.get(key).toString();
		}
	}
	
	public NameUsage(JSONObject json) { 
		if (json!=null) { 
			key = Integer.parseInt(getValFromKey(json,"key"));
			taxonomicStatus = getValFromKey(json,"taxonomicStatus");
			if (taxonomicStatus.equals("ACCEPTED")) { 
			    acceptedKey = Integer.parseInt(getValFromKey(json,"key"));
			    acceptedName = getValFromKey(json,"scientificName");
			} else { 
				try { 
			        acceptedKey = Integer.parseInt(getValFromKey(json,"acceptedKey"));
				} catch (NumberFormatException e) { 
					acceptedKey = 0;
				}
			    acceptedName = getValFromKey(json,"accepted");
			}
			datasetKey = getValFromKey(json,"datasetKey");
			try { 
			    parentKey = Integer.parseInt(getValFromKey(json,"parentKey"));
			} catch (NumberFormatException e) { 
				parentKey = 0;
			}
			numDescendants = Integer.parseInt(getValFromKey(json,"numDescendants"));
			parent = getValFromKey(json,"parent");
			scientificName = getValFromKey(json,"scientificName");
			canonicalName = getValFromKey(json,"canonicalName");
			authorship = getValFromKey(json,"authorship");
			rank = getValFromKey(json,"rank");
			kingdom = getValFromKey(json,"kingdom");
			phylum = getValFromKey(json,"phylum");
			tclass = getValFromKey(json,"clazz");
			order = getValFromKey(json,"order");
			family = getValFromKey(json,"family");
			genus = getValFromKey(json,"genus");
			sourceID = getValFromKey(json,"sourceId");
			link = getValFromKey(json,"link");
		}
	}
	
	public static String briefCsvHeaderLine() { 
		return "\"dbpk\",\"scientificName\",\"authorship\"," +
		       "\"guid\"," + 
		       "\"match\"," + 
		       "\"sciNameWas\"," + 
		       "\"authorWas\"," + 
		       "\"authorSimilarity\"" 
		       ;
	}
	
	protected void setupBriefCsvEncoding() { 
		QuoteMode quoteMode = new AlwaysQuoteMode();
		prefs = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE).useQuoteMode(quoteMode).build();
		encoder = new DefaultCsvEncoder();
		briefContext = new CsvContext(1,1,1);
	}
	
	
	public String toBriefCsvLine() { 
		if (briefContext==null) { 
			setupBriefCsvEncoding();
		}
		StringBuffer result = new StringBuffer();
		result.append(encoder.encode(Integer.toString(this.inputDbPK),briefContext,prefs)).append(",");
		result.append(encoder.encode(this.getScientificName(),briefContext,prefs)).append(",");
		result.append(encoder.encode(this.getAuthorship(),briefContext,prefs)).append(",");
		result.append(encoder.encode(this.getGuid(),briefContext,prefs)).append(",");
		result.append(encoder.encode(this.getMatchDescription(),briefContext,prefs)).append(",");
		result.append(encoder.encode(this.getOriginalScientificName(),briefContext,prefs)).append(",");
		result.append(encoder.encode(this.getOriginalAuthorship(),briefContext,prefs)).append(",");
		result.append(encoder.encode(Double.toString(this.getAuthorshipStringEditDistance()),briefContext,prefs));
		
		//result.append('"').append(this.inputDbPK).append("\",");
		//result.append('"').append(this.scientificName).append("\",");
		//result.append('"').append(this.getAuthorship()).append("\",");
		//result.append('"').append(this.getGuid()).append("\",");
		//result.append('"').append(this.getMatchDescription()).append("\",");
		//result.append('"').append(this.getOriginalAuthorship()).append("\",");
		//result.append('"').append(this.getAuthorshipStringEditDistance()).append("\"");
		
		return result.toString();
	}
	
	public static String csvHeaderLine() { 
		return "\"scientificName\",\"canonicalName\",\"authorship\"," +
				"\"taxonomicStatus\",\"acceptedName\",\"rank\"," +
				"\"kingdom\",\"phylum\",\"class\",\"order\",\"family\",\"genus\"," +
		        "\"key\",\"acceptedKey\",\"datasetKey\"," +
		        "\"parentKey\",\"parent\",\"childTaxaCount\",\"sourceid\",\"link\"" +
				"\n";
	}
	
	public String toCSVLine() { 
		StringBuffer result = new StringBuffer();
		result.append('"').append(scientificName).append("\",");
		result.append('"').append(canonicalName).append("\",");
		result.append('"').append(authorship).append("\",");
		result.append('"').append(taxonomicStatus).append("\",");
		result.append('"').append(acceptedName).append("\",");
		result.append('"').append(rank).append("\",");
		result.append('"').append(kingdom).append("\",");
		result.append('"').append(phylum).append("\",");
		result.append('"').append(tclass).append("\",");
		result.append('"').append(order).append("\",");
		result.append('"').append(family).append("\",");
		result.append('"').append(genus).append("\",");
		result.append(key).append(",");
		result.append(acceptedKey).append(",");
		result.append('"').append(datasetKey).append("\",");
		result.append(parentKey).append(",");
		result.append('"').append(parent).append("\",");
		result.append('"').append(numDescendants).append("\",");
		result.append('"').append(sourceID).append("\",");
		result.append('"').append(link).append("\",");
		
		result.append("\n");
		return result.toString();
	}
	
	public boolean toMCZbaseCsvWriter(ICsvListWriter listWriter) throws IOException { 
		boolean result = false;
		
		NameParser parser = new NameParser();
		ParsedName nameBits = null;
		try {
			nameBits = parser.parse(this.getScientificName());
		} catch (UnparsableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//"KINGDOM","PHYLUM","PHYLCLASS","PHYLORDER","FAMILY","GENUS",
		//"SPECIES", "AUTHOR_TEXT",
		//"SUBSPECIES", "INFRASPECIFIC_RANK", "INFRASPECIFIC_AUTHOR",
		//"VALID_CATALOG_TERM_FG","SOURCE_AUTHORITY",
		//"NOMENCLATURAL_CODE",
		//"TAXON_STATUS",
		//"TAXON_REMARKS"
		//"scientific_name"
		ArrayList<String> output = new ArrayList<String>();

		output.add(this.getKingdom());
		output.add(this.getPhylum());
		output.add(this.getTclass());
		output.add(this.getOrder());
		output.add(this.getFamily());
		output.add(this.getGenus());
		if ((this.getRank().equals("Subspecies") || this.getRank().equals("Species")) && nameBits!=null ) {
			output.add(nameBits.getSpecificEpithet());
		} else { 
			output.add(this.getScientificName());
		}
		if (this.getRank().equals("Subspecies") && !this.getKingdom().equals("Animalia")) {
			output.add("");
		} else { 
			// for all animals, put authorship in author_text
		    output.add(this.getAuthorship());  // authorship
		}
		if (this.getRank().equals("Subspecies") && nameBits !=null) { 
			output.add(nameBits.getInfraSpecificEpithet()); // infraspecific name
		} else { 
			output.add("");
		}
		
		if (this.getRank().equals("Subspecies") && nameBits !=null) { 
			if (nameBits.getRankMarker()==null) { 
			    output.add("");  // getting no "" for subspecies in worms.
			} else { 
			    output.add(nameBits.getRankMarker());  // infraspecific_rank
			}
		} else { 
			output.add("");
		}
		
		if (this.getRank().equals("Subspecies") && nameBits !=null && !this.getKingdom().equals("Animalia")) { 
			output.add(this.getAuthorship()); // infraspecific author for non-animals.
		} else { 
			// for animals authorship for all ranks goes in author text
			output.add("");
		}
		if (this.getTaxonomicStatus().equalsIgnoreCase("accepted")) { 
			output.add("1");  // valid_catalog_term_fg
		} else { 
			output.add("0");
		}
		output.add(this.getSourceAuthority());  // source authority
		if (this.getKingdom().equals("Animalia")) { 
			output.add("ICZN");  //  nomenclatural code  
		} else { 
			//TODO: Determine kingdom to code mappings for other roots in WoRMS
			output.add("ICNafp");	
		}
		output.add(this.getUnacceptReason()); //  taxon_status
		output.add(this.getGuid());  // put guid in taxon remarks
		output.add(this.getScientificName());
		
		listWriter.write(output);		
		
		return result;
	}

	/**
	 * Return the key that uniquely identifies this name usage 
	 * (within the datasource).
	 *
	 * @return the key
	 */
	public int getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(int key) {
		this.key = key;
	}

	/**
	 * @return the acceptedKey
	 */
	public int getAcceptedKey() {
		return acceptedKey;
	}

	/**
	 * @param acceptedKey the acceptedKey to set
	 */
	public void setAcceptedKey(int acceptedKey) {
		this.acceptedKey = acceptedKey;
	}

	/**
	 * @return the acceptedName
	 */
	public String getAcceptedName() {
		return acceptedName;
	}

	/**
	 * @param acceptedName the acceptedName to set
	 */
	public void setAcceptedName(String acceptedName) {
		this.acceptedName = acceptedName;
	}

	/**
	 * Returns the key of the checklist that "hosts" this name usage.
	 *
	 * @return the datasetKey
	 */
	public String getDatasetKey() {
		return datasetKey;
	}

	/**
	 * @param datasetKey the datasetKey to set
	 */
	public void setDatasetKey(String datasetKey) {
		this.datasetKey = datasetKey;
	}

	/**
	 * @return the parentKey
	 */
	public int getParentKey() {
		return parentKey;
	}

	/**
	 * @param parentKey the parentKey to set
	 */
	public void setParentKey(int parentKey) {
		this.parentKey = parentKey;
	}

	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * @return the scientificName
	 */
	public String getScientificName() {
		if (scientificName==null) { 
			return "";
		}
		return scientificName;
	}

	/**
	 * @param scientificName the scientificName to set
	 */
	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
		NameParser parser = new NameParser();
		try {
			ParsedName parse  = parser.parse(this.scientificName);
			if (this.species==null) {
				if (parse.getRank()!=null && !parse.getRank().higherThan(Rank.SPECIES)) { 
				   setSpecies(parse.getGenusOrAbove() + " " + parse.getSpecificEpithet());
				}
			} 
			if (this.genus==null) { 
				if (parse.getRank()!=null && !parse.getRank().higherThan(Rank.GENUS)) { 
					this.setGenus(parse.getGenusOrAbove());
				}
			}
			parse.getScientificName();
		} catch (UnparsableException e) {
			log.error(e.getMessage());
		}
		
	}

	/**
	 * @return the canonicalName
	 */
	public String getCanonicalName() {
		return canonicalName;
	}

	/**
	 * @param canonicalName the canonicalName to set
	 */
	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	/**
	 * @return the status
	 */
	public String getTaxonomicStatus() {
		return taxonomicStatus;
	}

	/**
	 * @param status the status to set
	 */
	public void setTaxonomicStatus(String status) {
		this.taxonomicStatus = status;
	}

	/**
	 * @return the numDescendants
	 */
	public int getNumDescendants() {
		return numDescendants;
	}

	/**
	 * @param numDescendants the numDescendants to set
	 */
	public void setNumDescendants(int numDescendants) {
		this.numDescendants = numDescendants;
	}

	/**
	 * @return the sourceID
	 */
	public String getSourceID() {
		return sourceID;
	}

	/**
	 * @param sourceID the sourceID to set
	 */
	public void setSourceID(String sourceID) {
		this.sourceID = sourceID;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return the rank
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}

	/**
	 * @return the kingdom or an empty string if kingdom is not set.
	 */
	public String getKingdom() {
		if (kingdom==null) { 
			return "";
		}
		return kingdom;
	}

	/**
	 * @param kingdom the kingdom to set
	 */
	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}

	/**
	 * @return the authorship
	 */
	public String getAuthorship() {
		if (authorship==null) { 
			return "";
		} else { 
		   return authorship;
		}
	}

	/**
	 * @param authorship the authorship to set
	 */
	public void setAuthorship(String authorship) {
		this.authorship = authorship;
	}

	/**
	 * @return the phylum
	 */
	public String getPhylum() {
		return phylum;
	}

	/**
	 * @param phylum the phylum to set
	 */
	public void setPhylum(String phylum) {
		this.phylum = phylum;
	}

	/**
	 * @return the tclass
	 */
	public String getTclass() {
		return tclass;
	}

	/**
	 * @param tclass the tclass to set
	 */
	public void setTclass(String tclass) {
		this.tclass = tclass;
	}

	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * @param family the family to set
	 */
	public void setFamily(String family) {
		this.family = family;
	}

	/**
	 * @return the genus
	 */
	public String getGenus() {
		return genus;
	}

	/**
	 * @param genus the genus to set
	 */
	public void setGenus(String genus) {
		this.genus = genus;
	}

	/**
	 * @return the sourceAuthority
	 */
	public String getSourceAuthority() {
		return sourceAuthority;
	}

	/**
	 * @param sourceAuthority the sourceAuthority to set
	 */
	public void setSourceAuthority(String sourceAuthority) {
		this.sourceAuthority = sourceAuthority;
	}

	/**
	 * @return the unacceptReason
	 */
	public String getUnacceptReason() {
		return unacceptReason;
	}

	/**
	 * @param unacceptReason the unacceptReason to set
	 */
	public void setUnacceptReason(String unacceptReason) {
		this.unacceptReason = unacceptReason;
	}

	/**
	 * @return the guid
	 */
	public String getGuid() {
		if (guid==null) { 
			return ""; 
		} else { 
		    return guid;
		}
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}

	/**
	 * @return the inputDbPK
	 */
	public int getInputDbPK() {
		return inputDbPK;
	}

	/**
	 * @param inputDbPK the inputDbPK to set
	 */
	public void setInputDbPK(int inputDbPK) {
		this.inputDbPK = inputDbPK;
	}

	/**
	 * @return the matchDescription
	 */
	public String getMatchDescription() {
		return matchDescription;
	}

	/**
	 * @param matchDescription the matchDescription to set
	 */
	public void setMatchDescription(String matchDescription) {
		this.matchDescription = matchDescription;
		if (this.matchDescription!=null) { 
			this.matchDescription = this.matchDescription.replace("<i>", "");
			this.matchDescription = this.matchDescription.replace("<\\i>", "");
		}
	}

	/**
	 * @return the authorshipStringSimilarity
	 */
	public double getAuthorshipStringEditDistance() {
		return authorshipStringSimilarity;
	}

	/**
	 * @param authorshipStringSimilarity the authorshipStringSimilarity to set
	 */
	public void setAuthorshipStringEditDistance(double authorshipStringEditDistance) {
		this.authorshipStringSimilarity = authorshipStringEditDistance;
	}

	/**
	 * @return the scientificNameStringSimilarity
	 */
	public double getScientificNameStringEditDistance() {
		return scientificNameStringSimilarity;
	}

	/**
	 * @param scientificNameStringSimilarity the scientificNameStringSimilarity to set
	 */
	public void setScientificNameStringEditDistance(
			double scientificNameStringEditDistance) {
		this.scientificNameStringSimilarity = scientificNameStringEditDistance;
	}

	/**
	 * @return the originalScientificName
	 */
	public String getOriginalScientificName() {
		if (this.originalScientificName==null) { 
			return "";
		}
		return originalScientificName;
	}

	/**
	 * @param originalScientificName the originalScientificName to set
	 */
	public void setOriginalScientificName(String originalScientificName) {
		this.originalScientificName = originalScientificName;
	}

	/**
	 * @return the originalAuthorship
	 */
	public String getOriginalAuthorship() {
		if (originalAuthorship==null) { 
			return "";
		}
		return originalAuthorship;
	}

	/**
	 * @param originalAuthorship the originalAuthorship to set
	 */
	public void setOriginalAuthorship(String originalAuthorship) {
		this.originalAuthorship = originalAuthorship;
	}

	/**
	 * @return the authorComparator
	 */
	public AuthorNameComparator getAuthorComparator() {
		return authorComparator;
	}

	/**
	 * @param authorComparator the authorComparator to set
	 */
	public void setAuthorComparator(AuthorNameComparator authorComparator) {
		this.authorComparator = authorComparator;
	}

	@Override
	public String getClazz() {
		return this.tclass;
	}

	@Override
	public void setClazz(String clazz) {
		this.tclass = clazz;
	}

	@Override
	public String getSpecies() {
		return null;
	}

	@Override
	public void setSpecies(String species) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSubgenus() {
		return this.subgenus;
	}

	@Override
	public void setSubgenus(String subgenus) {
		this.subgenus = subgenus;
	}

	@Override
	public String getHigherRank(Rank rank) {
		return ClassificationUtils.getHigherRank(this, rank);
	}
}

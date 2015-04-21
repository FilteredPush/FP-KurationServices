package edu.harvard.mcz.nametools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

public class NameUsage {
	
	private int key;
	private int acceptedKey;
	private String datasetKey;
	private int parentKey;
	private String parent;
	private String acceptedName;
	private String scientificName;
	private String canonicalName;
	private String authorship;
	private String taxonomicStatus;
	private String rank;
	private String kingdom;
	private String phylum;
	private String tclass;
	private String order;
	private String family;
	private String genus;
	private int numDescendants;
	private String sourceID;
	private String link;
    private boolean synonyms;
    
	private String guid;             // GUID for the name usage
	
	private String matchDescription;  // metadata, description of the match between this name usage annd the original
	private double authorshipStringSimilarity;
	private double scientificNameStringSimilarity;
	
	private int inputDbPK;  // Original database primary key for input
	private String originalScientificName;  
	private String originalAuthorship;    
	
	protected AuthorNameComparator authorComparator;
	
	public NameUsage() { 
		authorComparator = new ICZNAuthorNameComparator(.75d,.5d);
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
            synonyms = Boolean.parseBoolean(getValFromKey(json,"synonym"));
            fixAuthorship();
		}
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

	/**
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
		return scientificName;
	}

	/**
	 * @param scientificName the scientificName to set
	 */
	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
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
	 * @return the kingdom
	 */
	public String getKingdom() {
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
		return authorship;
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

    //insert by Tianhong
    public void setSynonyms (boolean synonyms){
        this.synonyms = synonyms;
    }

    public boolean getSynonyms(){
        return synonyms;
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
	}

	/**
	 * @return the authorshipStringSimilarity
	 */
	public double getAuthorshipStringSimilarity() {
		return authorshipStringSimilarity;
	}

	/**
	 * @param authorshipStringSimilarity the authorshipStringSimilarity to set
	 */
	public void setAuthorshipStringSimilarity(double authorshipStringSimilarity) {
		this.authorshipStringSimilarity = authorshipStringSimilarity;
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
	 * @return the originalScientificName
	 */
	public String getOriginalScientificName() {
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
		return originalAuthorship;
	}

	/**
	 * @param originalAuthorship the originalAuthorship to set
	 */
	public void setOriginalAuthorship(String originalAuthorship) {
		this.originalAuthorship = originalAuthorship;
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
	
	/**
	 * Fix certain known cases of errors in the formulation of an 
	 * authorship string, sensitive to relevant nomenclatural code.
	 * Remove authorship from scientific name if present.
	 */
	public void fixAuthorship() { 
		if (authorship!=null) { 
			if (scientificName != null && scientificName.contains(authorship)) { 
				scientificName.replace(authorship, "");
			    scientificName = scientificName.trim();
			}
			authorship = authorship.trim();
			if (kingdom.equals("Animalia")) { 
				if (ICZNAuthorNameComparator.containsParenthesies(authorship)) { 
					// Fix pathological case sometimes returned by COL: Author (year)
					// which should be (Author, year).
					
					//^([A-Za-z., ]+)[, ]*\(([0-9]{4})\)$
					Pattern p = Pattern.compile("^([A-Za-z., ]+)[, ]*\\(([0-9]{4})\\)$");
					Matcher matcher = p.matcher(authorship);
					if (matcher.matches()) { 
					   StringBuffer retval = new StringBuffer();
					   retval.append("(");
					   retval.append(matcher.group(1).trim());
					   retval.append(", ");
					   retval.append(matcher.group(2));
					   retval.append(")");
	 				   authorship = retval.toString().trim();
					}
				}
			}
		}
	}	
	
}

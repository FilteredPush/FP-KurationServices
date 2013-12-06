package fp.services;

import fp.util.CurationComment;
import fp.util.CurationStatus;
import fp.util.CurrationException;
import org.marinespecies.aphia.v1_0.AphiaNameServicePortTypeProxy;
import org.marinespecies.aphia.v1_0.AphiaRecord;

import java.io.*;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Provides support for scientific name validation against the WoRMS 
 * (World Register of Marine Species) Aphia web service. 
 * See: http://www.marinespecies.org/aphia.php?p=webservice
 * 
 * @author Lei Dou
 * @author Paul J. Morris
 *
 */
public class WoRMSService implements IScientificNameValidationService{
    private boolean useCache = false;

    public WoRMSService(){
	}

	public void validateScientificName(String scientificName, String author){
		validateScientificName(scientificName, author, "", "","","");
	}

	/**
	 * @param rank is ignored for this service.
	 */
	public void validateScientificName(String scientificName, String author, String rank, String kingdom, String phylum, String tclass){
		correctedScientificName = null;
		correctedAuthor = null;
		WoRMSlsid = null;
		comment = "";
		curationStatus = CurationComment.UNABLE_CURATED;

		//try to find information from the cached file
		//if failed, then access WoRMS Aphia service or even GNI service

		String key = constructKey(scientificName, author);		
		if(useCache && cachedScientificName.containsKey(key)){
			HashMap<String,String> cachedScientificNameInfo = cachedScientificName.get(key);

			String expAuthor = cachedScientificNameInfo.get("author");
			if(expAuthor.equals("")){
				//can't be found in either WoRMS or GNI
				comment = "Failed to find scientific name in both WoRMS and GNI.";
			}else if(expAuthor.equalsIgnoreCase(author)){
				correctedScientificName = scientificName;
				correctedAuthor = author;
				WoRMSlsid = constructIPNILSID(cachedScientificNameInfo.get("id")); 
				comment = "The scientific name and authorship are correct.";
				curationStatus = CurationComment.CORRECT;
			}else{
				correctedScientificName = scientificName;
				correctedAuthor = expAuthor;
				WoRMSlsid = constructIPNILSID(cachedScientificNameInfo.get("id")); 
				comment = "Updated the scientific name (including authorship) with term found in GNI which is from WoRMS and in the same lexicalgroup as the original term.";
				curationStatus = CurationComment.CURATED;
			}
		}else{
			//try to find it in WoRMS Aphia service			
			try{
				String source = "";
				String id = simpleNameSearch(scientificName, author);
				if(id == null){ 
					// Note that WoRMS also has a fuzzy matching service.  Could implement here.


					//access the GNI and try to get the name that is in the lexical group and from WoRMS
					Vector<String> resolvedNameInfo = resolveIPNINameInLexicalGroupFromGNI(scientificName);

					if(resolvedNameInfo == null || resolvedNameInfo.size()==0){
						//failed to find it in GNI						
						comment = "Can't find the scientific name and authorship by searching in IPNI and the lexical group from IPNI in GNI.";
					}else{
						//find it in GNI
						String resolvedScientificName = resolvedNameInfo.get(0);
						String resolvedScientificNameAuthorship = resolvedNameInfo.get(1);

						//searching for this name in IPNI again to get the IPNI LSID
						id = simpleNameSearch(resolvedScientificName, resolvedScientificNameAuthorship);
						if(id == null){
							//failed to find the name got from GNI in the IPNI
							comment = "Found name which is in the same lexical group as the searched scientific name and from IPNI but failed to find this name really in IPNI.";
						}else{
							//correct the wrong scientific name or author by searching in both IPNI and GNI
							correctedScientificName = resolvedScientificName;
							correctedAuthor = resolvedScientificNameAuthorship;
							WoRMSlsid = constructIPNILSID(id); 
							comment = "Updated the scientific name (including authorship) with term found in GNI which is from IPNI and in the same lexicalgroup as the original term.";
							curationStatus = CurationComment.CURATED;
							source = "IPNI/GNI";
						}
					}					
				}else{
					//get a match by searching in IPNI
					WoRMSlsid = constructIPNILSID(id); 
					correctedScientificName = scientificName;
					correctedAuthor = author;
					comment = "The scientific name and authorship are correct.";
					curationStatus = CurationComment.CORRECT;
					source = "IPNI";				
				}				

				//write newly found information into hashmap and later write into the cached file if it exists
				if(useCache){
					HashMap<String,String> cachedScientificNameInfo = new HashMap<String,String>();

					//					if(correctedAuthor == null){
					//						cachedScientificNameInfo.put("author", "");
					//					}else{
					//						cachedScientificNameInfo.put("author", correctedAuthor);
					//					}

					if(correctedAuthor == null){
						correctedAuthor = "";
					}
					cachedScientificNameInfo.put("author", correctedAuthor);

					//					if(id == null){
					//						cachedScientificNameInfo.put("id", "");
					//					}else{
					//						cachedScientificNameInfo.put("id", id);
					//					}

					if(id == null){
						id = "";						
					}	
					cachedScientificNameInfo.put("id", id);

					cachedScientificNameInfo.put("source", source);

					cachedScientificName.put(key,cachedScientificNameInfo);

					newFoundScientificName.add(scientificName);				
					newFoundScientificName.add(author);
					newFoundScientificName.add(correctedAuthor);
					newFoundScientificName.add(id);
					newFoundScientificName.add(source);					
				}
			}catch(CurrationException ex){
				comment = ex.getMessage();
				curationStatus = CurationComment.UNABLE_DETERMINE_VALIDITY;
				return;
			}
		}		
	}

	public CurationStatus getCurationStatus(){
		return curationStatus;
	}

	public String getCorrectedScientificName(){
		return correctedScientificName;
	}

	public String getCorrectedAuthor(){
		return correctedAuthor;
	}

	public String getLSID(){
		return WoRMSlsid;
	}

	public String getComment() {
		return comment;
	}	

	public void setCacheFile(String file) throws CurrationException{
		initializeCacheFile(file);		
		importFromCache();
        this.useCache = true;
	}

	public void flushCacheFile() throws CurrationException{
		if(cacheFile == null){
			return;
		}

		try {
			//output the newly found information into the cached file
			if(newFoundScientificName.size()>0){
				BufferedWriter writer  = new BufferedWriter(new FileWriter(cacheFile,true));
				for(int i=0;i<newFoundScientificName.size();i=i+5){
					String strLine = "";
					for(int j=i;j<i+5;j++){
						if(j>i){
							strLine = strLine + "\t" ;
						}
						strLine = strLine + newFoundScientificName.get(j);
					}
					writer.write(strLine+"\n");
				}	
				writer.close();
			}
		} catch (IOException e) {
			throw new CurrationException(getClass().getName()+" failed to write newly found scientific name information into cached file "+cacheFile.toString()+" since "+e.getMessage());
		}		
	}

    @Override
    public List<List> getLog() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setUseCache(boolean use) {
        this.useCache = use;
        cachedScientificName = new HashMap<String,HashMap<String,String>>();
        newFoundScientificName = new Vector<String>();
    }

    public String getServiceName() {
		return serviceName;
	}

	private void initializeCacheFile(String fileStr) throws CurrationException{
		cacheFile = new File(fileStr);

		if(!cacheFile.exists()){
			try {
				//If it's the first time to use the cached file and the file doesn't exist now, then create one 
				FileWriter writer = new FileWriter(fileStr);
				writer.close();
			} catch (IOException e) {
				throw new CurrationException(getClass().getName()+" failed since the specified data cache file of "+fileStr+" can't be opened successfully for "+e.getMessage());
			}			
		}

		if(!cacheFile.isFile()){
			throw new CurrationException(getClass().getName()+" failed since the specified data cache file "+fileStr+" is not a valid file.");
		}
	}

	private void importFromCache() throws CurrationException{
		cachedScientificName = new HashMap<String,HashMap<String,String>>();
		newFoundScientificName = new Vector<String>();

		//read
		try {
			BufferedReader cachedFileReader = new BufferedReader(new FileReader(cacheFile));
			String strLine = cachedFileReader.readLine();
			while(strLine!=null){
				String[] info = strLine.split(ColumnDelimiterInCacheFile,-1);
				if(info.length != 5){
					throw new CurrationException(getClass().getName()+" failed to import data from cached file since some information is missing at: "+strLine);
				}

				String taxon = info[0];
				String author = info[1];				
				String expAuthor = info[2];
				String id = info[3];
				String source = info[4];				

				HashMap<String,String> valueMap = new HashMap<String,String>();
				valueMap.put("author", expAuthor);
				valueMap.put("id", id);
				valueMap.put("source", source);

				cachedScientificName.put(constructKey(taxon,author), valueMap);

				strLine = cachedFileReader.readLine();
			}
			cachedFileReader.close();
		} catch (FileNotFoundException e) {
			//Since whether the file exist or not has been tested before, this exception should never be reached.
			throw new CurrationException(getClass().getName()+" failed to import data from cached file for "+e.getMessage());
		} catch (IOException e) {
			throw new CurrationException(getClass().getName()+" failed to import data from cached file for "+e.getMessage());
		}
	}

	private String constructKey(String taxon, String author){
		return taxon+" "+author;
	}

	private String constructIPNILSID(String id){
		return wormsLSIDPrefix+id;
	}

	/**
	 * Find a taxon name record in WoRMS.
	 * 
	 * @param taxon name to look for
	 * @param author authority to look for
	 * @return aphia id for the taxon
	 * @throws CurrationException
	 */
	private String simpleNameSearch(String taxon, String author) throws CurrationException{
		String id  = null;

		AphiaNameServicePortTypeProxy wormsService = new AphiaNameServicePortTypeProxy();

		boolean marineOnly = false;
		try {
			int taxonid = wormsService.getAphiaID(taxon, marineOnly);

			String foundId = Integer.toString(taxonid);
			AphiaRecord record = wormsService.getAphiaRecordByID(taxonid); 
			String foundTaxon = record.getScientificname();
			String foundAuthor = record.getAuthority();
			foundKingdom = record.getKingdom();
			foundPhylum = record.getPhylum();
			foundClass = record.get_class();
			foundOrder = record.getOrder();
			foundFamily = record.getFamily();

			if(foundTaxon.toLowerCase().equals(taxon.toLowerCase()) && author.toLowerCase().equals(foundAuthor.toLowerCase())){
				id = foundId;
			}

		} catch (NullPointerException ex) {
			// no match found
			id = null;
		} catch (RemoteException e) {
			throw new CurrationException("WoRMSService failed to access WoRMS Aphia service for " + taxon + ". " +e.getMessage());
		} 

		return id;
	}

	/**
	 * Special case of finding a name from an authoritative data source in the same lexical group as the supplied
	 * name in GNI's web services. 
	 * 
	 * @param scientificName
	 * @return
	 * @throws CurrationException
	 */
	private Vector<String> resolveIPNINameInLexicalGroupFromGNI(String scientificName) throws CurrationException {
		//get IPNI service Id at the first time 
		if(wormsSourceId == null){
			wormsSourceId = GNISupportingService.getGNIDataSourceID("WoRMS");
		}

		//If GNI doesn't support the data source of IPNI, then do nothing.
		if(wormsSourceId == null){
			return null;
		}

		//search name in GNI
		String nameFromIPNIInLexicalGroup = GNISupportingService.searchLexicalGroupInGNI(scientificName,wormsSourceId);
		if(nameFromIPNIInLexicalGroup == null){
			return null;
		}

		//parse name into scientific name and author by using the name parsing service in GNI
		return GNISupportingService.parseName(nameFromIPNIInLexicalGroup);
	}

	public String getFoundKingdom() {
		return foundKingdom;
	}

	public void setFoundKingdom(String foundKingdom) {
		this.foundKingdom = foundKingdom;
	}

	/**
	 * @return the foundPhylum
	 */
	public String getFoundPhylum() {
		return foundPhylum;
	}

	/**
	 * @param foundPhylum the foundPhylum to set
	 */
	public void setFoundPhylum(String foundPhylum) {
		this.foundPhylum = foundPhylum;
	}

	public String getFoundClass() {
		return foundClass;
	}

	public void setFoundClass(String foundClass) {
		this.foundClass = foundClass;
	}

	public String getFoundOrder() {
		return foundOrder;
	}

	public void setFoundOrder(String foundOrder) {
		this.foundOrder = foundOrder;
	}

	public String getFoundFamily() {
		return foundFamily;
	}

	public void setFoundFamily(String foundFamily) {
		this.foundFamily = foundFamily;
	}	

	private File cacheFile = null;
	private HashMap<String, HashMap<String,String>> cachedScientificName;
	private Vector<String> newFoundScientificName;
	private static final String ColumnDelimiterInCacheFile = "\t";

	private CurationStatus curationStatus;
	private String correctedScientificName = null;
	private String correctedAuthor = null;
	private String WoRMSlsid = null;	
	private String comment = "";

	private String foundKingdom = null;
	private String foundPhylum = null;
	private String foundClass = null;
	private String foundOrder = null;
	private String foundFamily = null;	

	private String wormsSourceId = null;	

	private final static String wormsLSIDPrefix = "urn:lsid:marinespecies.org:taxname:";

	private final String serviceName = "WoRMS";

}

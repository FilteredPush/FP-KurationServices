package org.filteredpush.kuration.services;

import org.filteredpush.kuration.interfaces.IFloweringTimeValidationService;
import org.filteredpush.kuration.util.CurationComment;
import org.filteredpush.kuration.util.CurationException;
import org.kurator.akka.data.CurationStep;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class FNAFloweringTimeService extends BaseCurationService implements IFloweringTimeValidationService {

    private boolean useCache;
    private String FNAFilePath = "/home/tianhong/Downloads/phenology.xml";
    
	private File cacheFile = null;

	private Vector<String> correctedFloweringTime;
	
	private HashMap<String,Vector<String>> authoritativeFloweringTimeMap = null; 
	private static final String ColumnDelimiterInCacheFile = "\t";
	
	private final String serviceName = "Authoritative Data from FNA";

	public FNAFloweringTimeService() { 
		initDate(new CurationStep("FNAFloweringTimeService: Not initialized properly.", new HashMap<String, String>()));
	}
	
	protected void initDate(CurationStep curationStep) { 
		initBase(curationStep);
	}
	
    public void setCacheFile(String file) throws CurationException {
        useCache = true;
		initializeCacheFile(file);
		//importFromCache();
        //Changed by thsong to use FNA dataset in xml
        importFNAData();
	}

	public void validateFloweringTime(String scientificName, String eventDate, String reproductiveState, String country, String kingdom) {
		validateFloweringTime(scientificName, eventDate, reproductiveState, country, kingdom, null, null);
	}
	
	public void validateFloweringTime(String scientificName, String eventDate, String reproductiveState, String country, String kingdom, String latitude, String longitude) {
		HashMap<String, String> initialValues = new HashMap<String, String>();
		initialValues.put("eventDate", eventDate);
		initialValues.put("scientificName", scientificName);
		initialValues.put("reproductive", reproductiveState);
		initialValues.put("country", country);
		initialValues.put("kingdom", kingdom);
		initialValues.put("latitude", latitude);
		initialValues.put("longitude", longitude);
		initDate(new CurationStep("Validate Collecting Event Date: check dwc:eventDate against reproductive state. ", initialValues));
		
		// TODO: fix to compare provided eventDate and reproductiveState with data from FNA
		
	    Vector<String> months = new Vector<String>();
		Vector<String> foundFloweringTime = null;
		if(authoritativeFloweringTimeMap != null && authoritativeFloweringTimeMap.containsKey(scientificName.toLowerCase())){
			foundFloweringTime = authoritativeFloweringTimeMap.get(scientificName.toLowerCase()); 
		}
		
		if(foundFloweringTime == null){
			setCurationStatus(CurationComment.UNABLE_DETERMINE_VALIDITY);
			addToComment("Can't find the flowering time of the "+scientificName+" in the current available phenology data from FNA.");
			correctedFloweringTime = null;
		}else{
			if(months==null || !months.containsAll(foundFloweringTime) || !foundFloweringTime.containsAll(months) ){
				setCurationStatus(CurationComment.UNABLE_CURATED);
				addToComment("Provided event date and flowering state is inconsistent with known flowering times for species according to FNA.");
			}else{
				setCurationStatus(CurationComment.CORRECT);
				addToComment("The event date and flowering state is consistent with authoritative data from FNA");
				correctedFloweringTime = months; 				
			}
		}
	}	
	
	public Vector<String> getCorrectedFloweringTime() {
		return correctedFloweringTime;
	}
	

	public void flushCacheFile() throws CurationException {
	}

    @Override
    public List<List> getLog() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setUseCache(boolean use) {
        this.useCache = use;
        authoritativeFloweringTimeMap = new HashMap<String,Vector<String>>();
    }

    public String getServiceName(){
		return serviceName;
	}

	private void initializeCacheFile(String fileStr) throws CurationException {
		cacheFile = new File(fileStr);

		if(!cacheFile.exists()){
			try {
				//If it's the first time to use the cached file and the file doesn't exist now, then create one
				FileWriter writer = new FileWriter(fileStr);
				writer.close();
			} catch (IOException e) {
				throw new CurationException(getClass().getName()+" failed since the specified data cache file of "+fileStr+" can't be opened successfully for "+e.getMessage());
			}
		}

		if(!cacheFile.isFile()){
			throw new CurationException(getClass().getName()+" failed since the specified data cache file "+fileStr+" is not a valid file.");
		}
	}

	private void importFromCache() throws CurationException {
		authoritativeFloweringTimeMap = new HashMap<String,Vector<String>>();

		try {
			BufferedReader phenologyFileReader = new BufferedReader(new FileReader(cacheFile));
			String strLine = phenologyFileReader.readLine();
			while(strLine!=null){
				String[] info = strLine.split(ColumnDelimiterInCacheFile,-1);
				if(info.length!=3){
					throw new CurationException(getClass().getName()+" failed since the authoritative file "+cacheFile.toString()+" is invalid at "+strLine);
				}
				String taxon = info[0].trim().toLowerCase();
				String floweringTime = info[1].trim();
				authoritativeFloweringTimeMap.put(taxon, getMonthVector(floweringTime));

				strLine = phenologyFileReader.readLine();
			}
		} catch (FileNotFoundException e) {
			throw new CurationException(getClass().getName()+" failed to find the phenology authoritative file "+cacheFile.toString()+" for "+e.getMessage());
		} catch (IOException e) {
			throw new CurationException(getClass().getName()+" failed to read the phenology authoritative file "+cacheFile.toString()+" for "+e.getMessage());
		}
	}

	private Vector<String> getMonthVector(String flowerTimeStr){
		Vector<String> monthVector = new Vector<String>();
		String [] monthArray = flowerTimeStr.split(";");		
		for(int i=0;i<monthArray.length;i++){
			monthVector.add(monthArray[i].trim());
		}
		return monthVector;
	}	

    private void importFNAData() {
        File fXmlFile = new File(FNAFilePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        Document doc = null;

        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("taxon");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                String genus, species, variety;
                genus =  species = variety = "";
                try{
                    genus = eElement.getElementsByTagName("genus").item(0).getNodeValue();
                } catch(Exception e){}
                try{
                    species = eElement.getElementsByTagName("species").item(0).getNodeValue();
                } catch(Exception e){}
                try{
                    variety = eElement.getElementsByTagName("variety").item(0).getNodeValue();
                } catch(Exception e){}

                String sciName = genus + " " + species + " var. " + variety;

                NodeList flowList = eElement.getElementsByTagName("flowering_time");
                Vector<String> monthVector = new Vector<String>();
                for (int i = 0; i < flowList.getLength(); i++){
                    //System.out.println(flowList.item(i).getTextContent());
                    monthVector.add(flowList.item(i).getNodeValue().trim());
                }

                authoritativeFloweringTimeMap.put(sciName, monthVector);
            }
        }
    }

	
}

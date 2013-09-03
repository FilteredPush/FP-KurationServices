package fp.util;

import fp.util.SpecimenRecord;
/*
import edu.umb.cs.filteredpush.client.ComparisonType;
import edu.umb.cs.filteredpush.client.GroupCondition;
import edu.umb.cs.filteredpush.client.QueryCondition;
import edu.umb.cs.filteredpush.client.QueryImpl;
*/

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import java.util.*;

public class FPQuery {
    /*
	public void clearConditions(){
		queryConditions.clear();
	}
	
	public void addCondition(String elementName, String comparisonType, double fuzzyValue, String value){
		QueryCondition qc = new QueryCondition();
		qc.setElementName(elementName);
		qc.setType(ComparisonType.valueOf(comparisonType));
		qc.setFuzzyValue(fuzzyValue);
		qc.setValue(value);		
		
		queryConditions.add(qc);
	}
	
	public LinkedList<SpecimenRecord> search() throws CurrationException {
        try{
			String[] fieldList = {"datasource","taxon","collector","collectornumber","year","locality","Catalognumber","id","state","country","county","day","month","latitude","longitude","family","author","collection_code"};	
			ArrayList<HashMap<String,String>> result = triageQuery(fieldList,queryConditions,"object");			
			
			LinkedList<SpecimenRecord> recordTokenList = new LinkedList<SpecimenRecord>();
			for(int i=0;i<result.size();i++){
				HashMap<String,String> r = result.get(i);
				
				LinkedHashMap<String,String> specimenRecord = new LinkedHashMap<String,String>();
				specimenRecord.put("CatalogNumber", r.get("Catalognumber"));
				specimenRecord.put("RecordedBy", r.get("collector"));
				specimenRecord.put("FieldNumber", r.get("collectornumber"));
				specimenRecord.put("YearCollected", r.get("year"));
				specimenRecord.put("MonthCollected", r.get("month"));
				specimenRecord.put("DayCollected", r.get("day"));
				specimenRecord.put("DecimalLatitude", r.get("latitude"));
				specimenRecord.put("DecimalLongitude", r.get("longitude"));
				specimenRecord.put("GeodeticDatum", "WGS84");//not from query
				specimenRecord.put("Country", r.get("country"));
				specimenRecord.put("StateProvince", r.get("state"));
				specimenRecord.put("County", r.get("county"));
				specimenRecord.put("Locality", r.get("locality"));
				specimenRecord.put("Family", r.get("family"));
				specimenRecord.put("ScientificName", r.get("taxon"));
				specimenRecord.put("ScientificNameAuthorship", r.get("author"));
				specimenRecord.put("ScientificNameAuthorship", r.get("author"));
				specimenRecord.put("ReproductiveCondition", "Flower:June;July;August;September;October;November");//not from query
				specimenRecord.put("InstitutionCode", r.get("datasource"));
				specimenRecord.put("CollectionCode", r.get("collection_code"));
				specimenRecord.put("DatasetName", "SPNHCDEMO");//not from query
				specimenRecord.put("Id", r.get("id"));
				
				recordTokenList.add(new SpecimenRecord(specimenRecord));
			}
			
			return recordTokenList;
			
		}catch(Exception ex){
			throw new CurrationException("search failed in SearchSpecimenRecord for: "+ex.getMessage());
		}
	}

	private ArrayList<HashMap<String,String>> triageQuery(String[] fieldList,
			List<QueryCondition> conditionList, String tableName) throws Exception {
		ArrayList<HashMap<String,String>> ret = new ArrayList<HashMap<String,String>>();
		QueryImpl query = new QueryImpl();
	    XMLInputFactory inputFactory = XMLInputFactory.newInstance();    
	    GroupCondition wrapper = new GroupCondition();
	    wrapper.setAggregationFun("List");
	    wrapper.setName("w");
	    wrapper.setFieldList(new String[0]);
		GroupCondition topGroup = new GroupCondition();
		wrapper.getSubgroups().add(topGroup);
		topGroup.setName("g");
		String[] field0 = {"id"};
		topGroup.setFieldList(field0);
		GroupCondition bottomGroup = new GroupCondition();
		bottomGroup.setName("Record");
		bottomGroup.setAggregationFun("List");
		bottomGroup.setFieldList(fieldList);
		topGroup.setAggregationFun("List");
		topGroup.getSubgroups().add(bottomGroup);
		Source src = query.query(fieldList, conditionList, tableName, wrapper);
		XMLEventReader readers = inputFactory.createXMLEventReader(src);				
		
		cpRecords(readers,ret);
		return ret;
	}
	
	private void cpRecords(XMLEventReader eventReader, ArrayList<HashMap<String,String>> records) throws Exception {
		while(eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();
			if(event.isStartElement()){
				if(event.asStartElement().getName().getLocalPart().equals("Record")){
					HashMap<String,String> r = getRecord(eventReader);
					records.add(r);
				}			
			}
		}		
	}	

    private  HashMap<String,String> getRecord(XMLEventReader eventReader) throws Exception {
    	HashMap<String,String> fv = new HashMap<String,String>();
    	String fieldName="",value="";
		while(eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();    	
			if(event.isStartElement() && !event.asStartElement().getName().getLocalPart().equals("Record")){
				fieldName = event.asStartElement().getName().getLocalPart();//getFieldName(eventReader);
				value = getValue(eventReader);
				fv.put(fieldName, value);
			}
			if(event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("Record"))
				break;
			
		}
		return fv;
	}  
	
	private  String getValue(XMLEventReader eventReader) throws XMLStreamException {
		String value ="";
		while(eventReader.hasNext()){
			XMLEvent event = eventReader.nextEvent();    	
			if(event.isCharacters()){
				value = event.asCharacters().getData();
			}
			if(event.isEndElement())
				break;
			
		}
		return value.replace("\n", "");	
	}	
	
	private List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
    */
}

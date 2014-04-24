package fp.util;

import java.util.HashMap;

public final class SpecimenRecordTypeConf {

    private final Integer lock = 1;

	private SpecimenRecordTypeConf() {
        synchronized (lock) {
		    initialize();
        }
	}

	private void initialize(){
        //System.out.println("Using build-in configuration.");
        String[][] bic = {
            {"CatalogNumber", "catalogNumber", "String"},      //
            {"RecordedBy", "recordedBy", "String"},             //
            //{"FieldNumber", "FieldNumber", "Integer"},
            {"YearCollected", "year", "Integer"},               //
            {"MonthCollected", "month", "Integer"},             //
            {"DayCollected", "day", "Integer"},                 //
            {"DecimalLatitude","decimalLatitude","Scalar"}, //
            {"DecimalLongitude","decimalLongitude","Scalar"}, //
            //{"GeodeticDatum","geodeticDatum","String"},       //
            {"Country","country","String"}, //
            {"StateProvince","stateProvince","String"},     //
            {"County","county","String"},             //
            {"Locality","locality","String"},                     //
            {"Family","family","String"},             //
            {"ScientificName","scientificName","String"},
            {"ScientificNameAuthorship","scientificNameAuthorship","String"}, //
            {"ReproductiveCondition","reproductiveCondition","String"},
            //{"InstitutionCode","InstitutionCode","String"},`
            {"CollectionCode","oaiId","String"},
           // {"DatasetName","datasetName","String"},
            {"Id", "id", "String"},
            {"IdentificationTaxon", "IdentificationTaxon", "String"},
            {"EventDate", "eventDate", "String"},
            {"Modified", "modified", "String"},
            {"StartDayOfYear", "startDayOfYear", "String"},
            {"VerbatimEventDate", "verbatimEventDate", "String"}

        };

        //Type[] typeArray = new Type[bic.length];
        String[] labelArray = new String[bic.length];
        for (int i = 0; i < bic.length; i++) {
            String[] bice = bic[i];
            String labelName = bice[0];
            String label = bice[1];
            String type = bice[2];
            labelMap.put(labelName,label);
            labelArray[i] = label;
            //typeArray[i] = resolveType(type);
        }
        //definedType = new RecordType(labelArray,typeArray);
	}
	
	private Object resolveType(String typeStr){
		if(typeStr.equalsIgnoreCase("string")){
			//return BaseType.STRING;
		}else if(typeStr.equalsIgnoreCase("integer")){
			//return BaseType.INT;
		}else if(typeStr.equalsIgnoreCase("long")){
			//return BaseType.LONG;
		}else if(typeStr.equalsIgnoreCase("double")){
			//return BaseType.DOUBLE;
		}else if(typeStr.equalsIgnoreCase("boolean")){
			//return BaseType.BOOLEAN;
		}else if(typeStr.equalsIgnoreCase("scalar")){
			//return BaseType.SCALAR;
		}
		return null;
	}
	
	public String getLabel(String name){
		return labelMap.get(name);
	}
	
	//public RecordType getType(){
	//	return definedType;
	//}

    public static SpecimenRecordTypeConf getInstance(){
        if (INSTANCE == null)
            INSTANCE = new SpecimenRecordTypeConf();
        return INSTANCE;
    }
	
	private static HashMap<String,String> labelMap = new HashMap<String,String>();
	//private static RecordType definedType = null;
	
	private static SpecimenRecordTypeConf INSTANCE = null;

	private static final long serialVersionUID = 1L;
}

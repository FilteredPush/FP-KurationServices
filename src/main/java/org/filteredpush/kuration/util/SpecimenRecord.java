package org.filteredpush.kuration.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gbif.dwc.record.DarwinCoreRecord;
import org.gbif.dwc.record.StarRecord;
import org.gbif.dwc.terms.Term;

/**
 * Created with IntelliJ IDEA.
 * User: cobalt
 * Date: 26.04.2013
 * Time: 16:57
 * 
 * TODO: replace with DarwinCoreRecord
 */
public class SpecimenRecord extends HashMap<String,String> {

	private static final long serialVersionUID = 8044353774894573869L;
	
	// TODO: Make lists of these so that MongoSummaryWriter can iterate and doesn't need to hard code them.
    public static final String SciName_Comment_Label = "scinComment";
    public static final String SciName_Status_Label = "scinStatus";
    public static final String SciName_Source_Label = "scinSource";
    public static final String date_Comment_Label = "dateComment";
    public static final String date_Status_Label = "dateStatus";
    public static final String date_Source_Label = "dateSource";
    public static final String geoRef_Comment_Label = "geoRefComment";
    public static final String geoRef_Status_Label = "geoRefStatus";
    public static final String geoRef_Source_Label = "geoRefSource";
    public static final String borRef_Comment_Label = "borComment";
    public static final String borRef_Status_Label = "borStatus";
    public static final String borRef_Source_Label = "borSource";

    public static final String Original_SciName_Label = "originalScientificName";
    public static final String Original_Authorship_Label = "origialScientificNameAuthorship";
    public static final String Original_EventDate_Label = "origialEventDate";
    public static final String Original_Latitude_Label = "origialDecimalLatitude";
    public static final String Original_Longitude_Label = "origialDecimalLongitude";
    public static final String Original_BasisOfRecord_Label = "originalBasisOfRecord";

    public static final String dwc_scientificName = "scientificName";
    public static final String dwc_scientificNameAuthorship = "scientificNameAuthorship";
    public static final String dwc_eventDate = "eventDate";
    public static final String dwc_decimalLatitude = "decimalLatitude";
    public static final String dwc_decimalLongitude = "decimalLongitude";
    public static final String dwc_basisOfRecord = "basisOfRecord";
    public static final String dwc_geodeticDatum = "geodeticDatum";

    public SpecimenRecord() {
        super();
    }

    /**
     * Create a SpecimenRecord instance from the core of a StarRecord.
     * 
     * @param dwcrecord from which to extract a map of core term names and values.
     */
    public SpecimenRecord(StarRecord dwcrecord) { 
    	super();
    	Iterator<Term> i = dwcrecord.core().terms().iterator();
    	while (i.hasNext()) { 
    		Term t = i.next();
    		this.put(t.simpleName(), dwcrecord.core().value(t));
    	}
    }
    
    public SpecimenRecord(Map<? extends String, ? extends String> map) {
        super(map);
    }

   public String prettyPrint() {
        String result = "";
        for (String item : this.keySet()){
            result = result + item + ": " + this.get(item) + ", ";
        }
        return result;
    }
}

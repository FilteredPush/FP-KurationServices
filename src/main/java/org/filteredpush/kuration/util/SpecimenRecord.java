package org.filteredpush.kuration.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cobalt
 * Date: 26.04.2013
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class SpecimenRecord extends HashMap<String,String> {

    public static final String SciName_Comment_Label = "scinComment";
    public static final String SciName_Status_Label = "scinStatus";
    public static final String SciName_Source_Label = "scinSource";
    public static final String date_Comment_Label = "dateComment";
    public static final String date_Status_Label = "dateStatus";
    public static final String date_Source_Label = "dateSource";
    public static final String geoRef_Comment_Label = "geoRefComment";
    public static final String geoRef_Status_Label = "geoRefStatus";
    public static final String geoRef_Source_Label = "geoRefSource";

    public static final String Original_SciName_Label = "originalScientificName";
    public static final String Original_Authorship_Label = "origialScientificNameAuthorship";
    public static final String Original_EventDate_Label = "origialEventDate";
    public static final String Original_Latitude_Label = "origialDecimalLatitude";
    public static final String Original_Longitude_Label = "origialDecimalLongitude";

    public static final String dwc_scientificName = "scientificName";
    public static final String dwc_scientificNameAuthorship = "scientificNameAuthorship";
    public static final String dwc_eventDate = "eventDate";
    public static final String dwc_decimalLatitude = "decimalLatitude";
    public static final String dwc_decimalLongitude = "decimalLongitude";

    public SpecimenRecord() {
        super();
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

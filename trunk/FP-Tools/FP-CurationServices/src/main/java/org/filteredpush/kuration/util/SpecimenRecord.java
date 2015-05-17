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

    public static final String Original_SciName_Label = "originalScientificName";
    public static final String Original_Authorship_Label = "origialScientificNameAuthorship";

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

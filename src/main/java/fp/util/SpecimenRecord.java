package fp.util;

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

    public SpecimenRecord() {
        super();
    }

    public SpecimenRecord(Map<? extends String, ? extends String> map) {
        super(map);
    }
}

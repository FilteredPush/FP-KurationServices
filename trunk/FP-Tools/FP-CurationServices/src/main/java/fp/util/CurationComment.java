package fp.util;

import java.util.LinkedHashMap;

public class CurationComment {

	public static CurationCommentType construct(CurationStatus status,String details,String source) {
		LinkedHashMap<String,String> info = new LinkedHashMap<String,String>();
		info.put("status", status.toString());
		info.put("details", details);
		info.put("source", source);
        return new CurationCommentType(info);
	}	
	
	public static CurationStatus CORRECT = new CurationStatus("Correct   ");
	public static CurationStatus CURATED = new CurationStatus("Curated   ");
    public static CurationStatus Filled_in = new CurationStatus("Filled in ");
	public static CurationStatus UNABLE_CURATED = new CurationStatus("NotCurated");
	public static CurationStatus UNABLE_DETERMINE_VALIDITY = new CurationStatus("!Validated");

}

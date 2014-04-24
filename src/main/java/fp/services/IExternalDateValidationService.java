package fp.services;

import org.joda.time.DateMidnight;

public interface IExternalDateValidationService extends ICurationWithFileService {
	 //internal
	public void validateDate(DateMidnight eventDate, String collector, String latitude, String longitude);

   // public DateMidnight getCorrectedDate();
}
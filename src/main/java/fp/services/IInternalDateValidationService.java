package fp.services;

import org.joda.time.DateMidnight;

public interface IInternalDateValidationService extends ICurationWithFileService {
	 //internal
	public void validateDate(String eventDate, String verbatimEventDate, String startDayOfYear, String year, String month, String day, String modified, String collector);

    public DateMidnight getCorrectedDate();
}
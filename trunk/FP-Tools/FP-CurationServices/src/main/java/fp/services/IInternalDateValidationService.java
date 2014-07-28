package fp.services;

public interface IInternalDateValidationService extends ICurationWithFileService {
	 //internal
	public void validateDate(String eventDate, String verbatimEventDate, String startDayOfYear, String year, String month, String day, String modified, String collector);

    public String getCorrectedDate();
}
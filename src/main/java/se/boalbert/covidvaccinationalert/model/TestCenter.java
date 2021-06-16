package se.boalbert.covidvaccinationalert.model;

public class TestCenter {
	String title;
	String hsaid;
	String municipalityName;
	String municipality;
	String urlBooking;
	String urlContactCard;
	String urlContactCardText;
	String testtype;
	Long timeslots;
	String updated;

	public TestCenter() {
	}

	public TestCenter(String title, String hsaid, String municipalityName, String municipality, String urlBooking, String urlContactCard, String urlContactCardText, String testtype, Long timeslots, String updated) {
		this.title = title;
		this.hsaid = hsaid;
		this.municipalityName = municipalityName;
		this.municipality = municipality;
		this.urlBooking = urlBooking;
		this.urlContactCard = urlContactCard;
		this.urlContactCardText = urlContactCardText;
		this.testtype = testtype;
		this.timeslots = timeslots;
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "TestCenter{" +
				"title='" + title + '\'' +
				", hsaid='" + hsaid + '\'' +
				", municipalityName='" + municipalityName + '\'' +
				", municipality='" + municipality + '\'' +
				", urlBooking='" + urlBooking + '\'' +
				", urlContactCard='" + urlContactCard + '\'' +
				", urlContactCardText='" + urlContactCardText + '\'' +
				", testtype='" + testtype + '\'' +
				", timeslots=" + timeslots +
				", updated='" + updated + '\'' +
				'}';
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHsaid() {
		return hsaid;
	}

	public void setHsaid(String hsaid) {
		this.hsaid = hsaid;
	}

	public String getMunicipalityName() {
		return municipalityName;
	}

	public void setMunicipalityName(String municipalityName) {
		this.municipalityName = municipalityName;
	}

	public String getMunicipality() {
		return municipality;
	}

	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}

	public String getUrlBooking() {
		return urlBooking;
	}

	public void setUrlBooking(String urlBooking) {
		this.urlBooking = urlBooking;
	}

	public String getUrlContactCard() {
		return urlContactCard;
	}

	public void setUrlContactCard(String urlContactCard) {
		this.urlContactCard = urlContactCard;
	}

	public String getUrlContactCardText() {
		return urlContactCardText;
	}

	public void setUrlContactCardText(String urlContactCardText) {
		this.urlContactCardText = urlContactCardText;
	}

	public String getTesttype() {
		return testtype;
	}

	public void setTesttype(String testtype) {
		this.testtype = testtype;
	}

	public Long getTimeslots() {
		return timeslots;
	}

	public void setTimeslots(Long timeslots) {
		this.timeslots = timeslots;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

}

package se.boalbert.covidvaccinationalert.model;

public class TestCenter {
	public String title;
	public String hsaid;
	public String municipalityName;
	public String municipality;
	public String urlBooking;
	public String urlContactCard;
	public String urlContactCardText;
	public String testtype;
	public Long timeslots;
	public String updated;

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

	public TestCenter(String title, String municipalityName, String urlBooking, Long timeSlots) {
		this.title = title;
		this.municipalityName = municipalityName;
		this.urlBooking = urlBooking;
		this.timeslots = timeSlots;
	}

	public String title() {
		return title;
	}


	public String hsaid() {
		return hsaid;
	}


	public String municipalityName() {
		return municipalityName;
	}


	public String municipality() {
		return municipality;
	}


	public String urlBooking() {
		return urlBooking;
	}


	public String urlContactCard() {
		return urlContactCard;
	}


	public String urlContactCardText() {
		return urlContactCardText;
	}


	public String getTesttype() {
		return testtype;
	}


	public Long getTimeslots() {
		return timeslots;
	}


	public String getUpdated() {
		return updated;
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

}

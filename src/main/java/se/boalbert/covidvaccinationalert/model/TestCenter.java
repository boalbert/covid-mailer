package se.boalbert.covidvaccinationalert.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	public String ageGroup;
	public String updated;

	public TestCenter() {
	}

	public TestCenter(String title, String hsaid, String municipalityName, String municipality, String urlBooking, String urlContactCard, String urlContactCardText, String testtype, Long timeslots, String ageGroup, String updated) {
		this.title = title;
		this.hsaid = hsaid;
		this.municipalityName = municipalityName;
		this.municipality = municipality;
		this.urlBooking = urlBooking;
		this.urlContactCard = urlContactCard;
		this.urlContactCardText = urlContactCardText;
		this.testtype = testtype;
		this.timeslots = timeslots;
		this.ageGroup = ageGroup;
		this.updated = updated;
	}

	public static List<TestCenter> mergeMapsAndReturnUniqueTestCenters(Map<String, TestCenter> restCenters, Map<String, TestCenter> scrapeCenters) {

		Map<String, TestCenter> merged = Stream.of(restCenters, scrapeCenters)
				.flatMap(map -> map.entrySet().stream())
				.collect(Collectors.toMap(
						Map.Entry :: getKey,
						Map.Entry :: getValue,
						(v1, v2) -> new TestCenter(v1.getTitle(), v1.getHsaid(), v1.getMunicipalityName(), v1.getMunicipality(), v1.getUrlBooking(), v1.getUrlContactCard(), v1.getUrlContactCardText(), v1.getTesttype(), v1.getTimeslots(), v2.getAgeGroup(), v1.getUpdated())
				));

		return new ArrayList<>(merged.values());
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

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public void setTimeslots(Long timeslots) {
		this.timeslots = timeslots;
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
				", ageGroup='" + ageGroup + '\'' +
				", updated='" + updated + '\'' +
				'}';
	}

}

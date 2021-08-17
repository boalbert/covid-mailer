package se.boalbert.covidvaccinationalert.service;

import se.boalbert.covidvaccinationalert.model.ListTestCenter;
import se.boalbert.covidvaccinationalert.model.TestCenter;

import java.util.List;
import java.util.Map;

public interface IRestClient {
	Map<String, TestCenter> convertDataFromApiCallToTestCenter();

	ListTestCenter getFullResponseFromApi();

	List<TestCenter> findTestCenterByHsaid(List<TestCenter> listTestCenters, String hsaid);

	List<TestCenter> findTestCentersByMunicipalityId(List<TestCenter> listTestCenters, String municipalityId);

	List<TestCenter> findTimeslotsByMunicipalityId(List<TestCenter> listCenters, String municipalityId);

	Map<String, TestCenter> findAllAvailableTimeSlots(Map<String, TestCenter> listCenters);

	Map<String, TestCenter> filterCentersByUpdated(Map<String, TestCenter> testCenterList);
}


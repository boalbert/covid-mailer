package se.boalbert.covidvaccinationalert.service;

import se.boalbert.covidvaccinationalert.model.ListTestCenter;
import se.boalbert.covidvaccinationalert.model.TestCenter;

import java.util.List;
import java.util.Map;

public interface IRestClient {
	Map<String, TestCenter> extractAllCenters();

	ListTestCenter getFullResponseFromApi();

	List<TestCenter> findByHsaid(List<TestCenter> listTestCenters, String hsaid);

	List<TestCenter> findByMunicipality(List<TestCenter> listTestCenters, String municipalityId);

	List<TestCenter> findAvailableTimeslotsByMunicipalityId(List<TestCenter> listCenters, String municipalityId);

	Map<String, TestCenter> findAllAvailableTimeSlots(Map<String, TestCenter> listCenters);

	Map<String, TestCenter> filterCentersByUpdated(Map<String, TestCenter> testCenterList);
}


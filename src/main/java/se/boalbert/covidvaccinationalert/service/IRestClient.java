package se.boalbert.covidvaccinationalert.service;

import se.boalbert.covidvaccinationalert.model.ListTestCenter;
import se.boalbert.covidvaccinationalert.model.TestCenter;

import java.util.List;

public interface IRestClient {
	List<TestCenter> extractAllCenters();

	ListTestCenter getFullResponseFromApi();

	List<TestCenter> findByHsaid(List<TestCenter> listTestCenters, String hsaid);

	List<TestCenter> findByMunicipality(List<TestCenter> listTestCenters, String municipalityId);

	List<TestCenter> findAvailableTimeslotsByMunicipalityId(List<TestCenter> listCenters, String municipalityId);

	List<TestCenter> findAllAvailableTimeSlots(List<TestCenter> listCenters);

	List<TestCenter> filterCentersByUpdated(List<TestCenter> testCenterList);
}


package se.boalbert.covidvaccinationalert.service;

import se.boalbert.covidvaccinationalert.model.TestCenter;

import java.util.Map;

public interface IRestClient {
	Map<String, TestCenter> convertDataFromApiCallToTestCenter();

	Map<String, TestCenter> findAllAvailableTimeSlots(Map<String, TestCenter> listCenters);

	Map<String, TestCenter> filterCentersByUpdated(Map<String, TestCenter> testCenterList);
}


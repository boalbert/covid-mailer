package se.boalbert.covidvaccinationalert.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;
import se.boalbert.covidvaccinationalert.model.ListTestCenter;
import se.boalbert.covidvaccinationalert.model.TestCenter;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RestClientTest {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(RestClientTest.class);

	private WebClient webClient = WebClient.create();
	RestClient restClient = new RestClient();


	@Test
	void retrieveVaccinationData() {
		ListTestCenter listTestCenter = restClient.getFullResponseFromApi();

		assertTrue(listTestCenter.getTestcenters().size() > 0);
	}

	@Test
	void allCenters() {
		List<TestCenter> testCenters = restClient.extractAllCenters();

		assertTrue(testCenters.size() > 0);
	}

	@Test
	void findByHsaid() {
		List<TestCenter> allTestCenters = restClient.extractAllCenters();
		List<TestCenter> hsaidCenter = restClient.findByHsaid(allTestCenters, "SE2321000131-E000000016395"); // HSAID for Scandinavium
		TestCenter testCenterScandinavium = hsaidCenter.get(0);

		assertEquals(testCenterScandinavium.getTitle(), "Närhälsan Scandinavium" );

	}

	@Test
	void findByMunicipality() {
		List<TestCenter> allTestCenters = restClient.extractAllCenters();
		// 1480 for Göteborg
		// 1487 for Vänersborg
		List<TestCenter> municipalityCenters = restClient.findByMunicipality(allTestCenters, "1487");

		assertTrue(municipalityCenters.stream().allMatch(testCenter -> testCenter.getMunicipalityName().equals("Vänersborg")));



	}

	@Test
	void findAvailableTimeslots() {
		List<TestCenter> allTestCenters = restClient.extractAllCenters();
		List<TestCenter> availableTimeSlots = restClient.findAvailableTimeslotsByMunicipalityId(allTestCenters, "1460");

		log.info(availableTimeSlots.toString());
	}

	@Test
	void sendOnlyNewlyUpdated() {

		HashSet<String> sentAlerts = new HashSet<>();

		List<TestCenter> allTestCenters = restClient.extractAllCenters();
		List<TestCenter> avalibleTimeSlots = restClient.findAllAvailableTimeSlots(allTestCenters);
		int i = 0;
		for(TestCenter testCenter : avalibleTimeSlots) {
			if(sentAlerts.add(testCenter.getUpdated())) {
				i++;
				log.info("True: " + i);
				log.info("Adding: {}", testCenter.getMunicipalityName());
				log.info("Updated: {}", testCenter.getUpdated());
			} else {
				log.info("False: " + i);
			}

		}

		for(TestCenter testCenter : avalibleTimeSlots) {
			if(sentAlerts.add(testCenter.getUpdated())) {
				i++;
				log.info("True: " + i);
				log.info("Adding: {}", testCenter.getMunicipalityName());
				log.info("Updated: {}", testCenter.getUpdated());
			} else {
				log.info("False: " + i);
			}

		}



	}
}
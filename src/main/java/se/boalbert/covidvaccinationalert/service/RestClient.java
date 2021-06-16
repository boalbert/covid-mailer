package se.boalbert.covidvaccinationalert.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.boalbert.covidvaccinationalert.model.ListTestCenter;
import se.boalbert.covidvaccinationalert.model.TestCenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestClient implements IRestClient {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(RestClient.class);

	@Value("${API_URI}")
	private String API_URI;

	@Value("${CLIENT_ID}")
	private String CLIENT_ID;

	@Value("${CLIENT_SECRET}")
	private String CLIENT_SECRET;

	private final HashSet<String> oldTimeSlots = new LinkedHashSet<>();

	/**
	 * @return list of all the testcenters inside ListTestCenter
	 */
	@Override
	public List<TestCenter> extractAllCenters() {
		ListTestCenter listTestCenter = getFullResponseFromApi();

		return listTestCenter.getTestcenters();
	}

	/**
	 * Send a request to the API with id and secret loaded from config
	 *
	 * @return the whole request mapped to ListTestCenter POJO
	 */
	@Override
	public ListTestCenter getFullResponseFromApi() {

		WebClient webClient = WebClient.create();

		try {
			return webClient.get().uri(API_URI)
					.headers(httpHeaders -> {
						httpHeaders.set("client_id", CLIENT_ID);
						httpHeaders.set("client_secret", CLIENT_SECRET);
					})
					.retrieve()
					.bodyToMono(ListTestCenter.class)
					.block();

		} catch (WebClientResponseException ex) {
			log.error("Error - Response Code: {} ", ex.getRawStatusCode());
			log.error("Error - Response Body: {} ", ex.getResponseBodyAsString());
			log.error("WebClientResponseException in getFullResponseFromApi(): ", ex);
			throw ex;

		} catch (WebClientRequestException ex) {
			log.error("Error - URI: {} ", ex.getUri());
			log.error("WebClientResponseException in retrieveVaccinationData(): ", ex);
			throw ex;
		}
	}

	/**
	 * @param listTestCenters list of all centers
	 * @param hsaid           - unique identifier for each vaccination center
	 * @return list of matching centers
	 */
	@Override
	public List<TestCenter> findByHsaid(List<TestCenter> listTestCenters, String hsaid) {

		List<TestCenter> listFilteredByHsaid = listTestCenters.stream()
				.filter(testCenter -> testCenter.getHsaid().equals(hsaid))
				.collect(Collectors.toList());

		if (listFilteredByHsaid.size() > 0) {
			log.info("Matching Center: " + listFilteredByHsaid);
		}

		return listFilteredByHsaid;
	}

	/**
	 * Returns a list of centers matching municipality
	 * Göteborg = 1480
	 * Vänersborg = 1487
	 *
	 * @param listTestCenters list of all centers
	 * @param municipalityId  id of municipalitu (kommun)
	 * @return list of centers in municipality
	 */
	@Override
	public List<TestCenter> findByMunicipality(List<TestCenter> listTestCenters, String municipalityId) {

		List<TestCenter> listFilteredByMunicipality = listTestCenters.stream()
				.filter(testCenter -> testCenter.getMunicipality().equals(municipalityId))
				.collect(Collectors.toList());

		if (listFilteredByMunicipality.size() > 0) {
			log.info("Matching Centers in Municipality: " + listFilteredByMunicipality);
		}

		return listFilteredByMunicipality;
	}

	/**
	 * @param listCenters
	 * @param municipalityId
	 * @return
	 */
	@Override
	public List<TestCenter> findAvailableTimeslotsByMunicipalityId(List<TestCenter> listCenters, String municipalityId) {

		return listCenters.stream()
				.filter(testCenter -> testCenter.getTimeslots() != null)
				.filter(testCenter -> testCenter.getTimeslots() != 0)
				.filter(testCenter -> testCenter.getMunicipality().equals(municipalityId))
				.collect(Collectors.toList());
	}

	@Override
	public List<TestCenter> findAllAvailableTimeSlots(List<TestCenter> listCenters) {

		return listCenters.stream()
				.filter(testCenter -> testCenter.getTimeslots() != null)
				.filter(testCenter -> testCenter.getTimeslots() != 0)
				.collect(Collectors.toList());
	}

	/**
	 * Filters all open timeslots. Try to add the timeslot signature to HashSet 'sentAlerts'
	 * If it fails (not unique) alert it not sent.
	 * If it succeds it adds it to 'sentAlert' HashSet and then the timeslot will be sent out
	 *
	 * @param testCenterList
	 * @return list if new timeSlots
	 */
	public List<TestCenter> filterCentersByUpdated(List<TestCenter> testCenterList) {

		List<TestCenter> newTimeSlots = new ArrayList<>();

		String timeSlotSignature = "";

		for (TestCenter testCenter : testCenterList) {

			timeSlotSignature = testCenter.getHsaid() + testCenter.getUpdated();

			if (oldTimeSlots.add(timeSlotSignature)) {
				oldTimeSlots.add(timeSlotSignature);
				log.info(" +++ Adding timeSlot to oldAlerts HashSet: {}", timeSlotSignature);
				newTimeSlots.add(testCenter);
			} else {
				log.info(" --- Timeslot found found in oldAlerts HashSet: {}", timeSlotSignature);
			}
		}
		return newTimeSlots;
	}
}

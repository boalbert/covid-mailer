package se.boalbert.covidvaccinationalert.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.boalbert.covidvaccinationalert.model.ListTestCenter;
import se.boalbert.covidvaccinationalert.model.TestCenter;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RestClient implements IRestClient {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(RestClient.class);
	private final HashSet<String> oldTimeSlots = new LinkedHashSet<>();
	@Value("${API_URI}")
	private String API_URI;
	@Value("${CLIENT_ID}")
	private String CLIENT_ID;
	@Value("${CLIENT_SECRET}")
	private String CLIENT_SECRET;

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
	 * @return list of all the testcenters inside ListTestCenter
	 */
	@Override
	public Map<String, TestCenter> convertDataFromApiCallToTestCenter() {
		ListTestCenter listTestCenter = getFullResponseFromApi();

		return listTestCenter.getTestcenters().stream()
				.collect(Collectors.toMap(TestCenter :: getTitle, testCenter -> testCenter));

	}

	/**
	 * @param allTestCenters list of all centers
	 * @param hsaid           - unique identifier for each vaccination center
	 * @return list of matching centers
	 */
	@Override
	public List<TestCenter> findTestCenterByHsaid(List<TestCenter> allTestCenters, String hsaid) {

		List<TestCenter> listFilteredByHsaid = allTestCenters.stream()
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
	 * @param allTestCenters list of all centers
	 * @param municipalityId  id of municipalitu (kommun)
	 * @return list of centers in municipality
	 */
	@Override
	public List<TestCenter> findTestCentersByMunicipalityId(List<TestCenter> allTestCenters, String municipalityId) {

		List<TestCenter> listFilteredByMunicipality = allTestCenters.stream()
				.filter(testCenter -> testCenter.getMunicipality().equals(municipalityId))
				.collect(Collectors.toList());

		if (listFilteredByMunicipality.size() > 0) {
			log.info("Matching Centers in Municipality: " + listFilteredByMunicipality);
		}

		return listFilteredByMunicipality;
	}

	@Override
	public List<TestCenter> findTimeslotsByMunicipalityId(List<TestCenter> allTestCenters, String municipalityId) {

		return allTestCenters.stream()
				.filter(testCenter -> testCenter.getTimeslots() != null)
				.filter(testCenter -> testCenter.getTimeslots() != 0)
				.filter(testCenter -> testCenter.getMunicipality().equals(municipalityId))
				.collect(Collectors.toList());
	}

	@Override
	public Map<String, TestCenter> findAllAvailableTimeSlots(Map<String, TestCenter> allTestCenters) {

		return allTestCenters.entrySet().stream()
				.filter(map -> map.getValue().getTimeslots() != null)
				.filter(map -> map.getValue().getTimeslots() != 0)
				.collect(Collectors.toMap(Map.Entry :: getKey, Map.Entry :: getValue));
	}

	/**
	 * Filters all open timeslots. Try to add the timeslot signature to HashSet 'sentAlerts'
	 * If it fails (not unique) alert it not sent.
	 * If it succeds it adds it to 'SentAlert' HashSet and then the timeslot will be sent out
	 *
	 * @param testCenterList
	 * @return list if new timeSlots
	 */

	@Override
	public Map<String, TestCenter> filterCentersByUpdated(Map<String, TestCenter> testCenterList) {

		Map<String, TestCenter> newTimeSlots = new LinkedHashMap<>();

		String timeSlotSignature = "";

		for (Map.Entry<String, TestCenter> testCenter : testCenterList.entrySet()) {

			timeSlotSignature = testCenter.getValue().getHsaid() + testCenter.getValue().getUpdated();


			if (oldTimeSlots.add(timeSlotSignature)) {
				oldTimeSlots.add(timeSlotSignature);
				newTimeSlots.put(testCenter.getValue().getTitle(), testCenter.getValue());
			}
		}
		return newTimeSlots;
	}
}

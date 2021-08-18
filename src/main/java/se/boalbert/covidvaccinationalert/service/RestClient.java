package se.boalbert.covidvaccinationalert.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.boalbert.covidvaccinationalert.model.ListTestCenter;
import se.boalbert.covidvaccinationalert.model.TestCenter;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
	private ListTestCenter getFullResponseFromApi() {

		WebClient webClient = WebClient.create();

		try {
			return callApi(webClient);

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

	private ListTestCenter callApi(WebClient webClient) {
		return webClient.get().uri(API_URI)
				.headers(httpHeaders -> {
					httpHeaders.set("client_id", CLIENT_ID);
					httpHeaders.set("client_secret", CLIENT_SECRET);
				})
				.retrieve()
				.bodyToMono(ListTestCenter.class)
				.block();
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
	 * @return list if new timeSlots
	 */

	@Override
	public Map<String, TestCenter> filterCentersByUpdated(Map<String, TestCenter> testCenterList) {

		Map<String, TestCenter> newTimeSlots = new LinkedHashMap<>();

		for (Map.Entry<String, TestCenter> testCenter : testCenterList.entrySet()) {

			String newTimeSlotSignature = testCenter.getValue().getHsaid() + testCenter.getValue().getUpdated();

			if (oldTimeSlots.add(newTimeSlotSignature)) {
				markTimeSlotAsSentToRecipient(newTimeSlots, newTimeSlotSignature, testCenter);
			}
		}
		return newTimeSlots;
	}

	private void markTimeSlotAsSentToRecipient(Map<String, TestCenter> newTimeSlots, String timeSlotSignature, Map.Entry<String, TestCenter> testCenter) {
		oldTimeSlots.add(timeSlotSignature);
		newTimeSlots.put(testCenter.getValue().getTitle(), testCenter.getValue());
	}
}

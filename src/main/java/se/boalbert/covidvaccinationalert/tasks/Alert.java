package se.boalbert.covidvaccinationalert.tasks;

import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import se.boalbert.covidvaccinationalert.model.Recipient;
import se.boalbert.covidvaccinationalert.model.TestCenter;
import se.boalbert.covidvaccinationalert.service.IRestClient;
import se.boalbert.covidvaccinationalert.service.MailClient;
import se.boalbert.covidvaccinationalert.service.Scraper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static se.boalbert.covidvaccinationalert.controller.Recipients.recipients;

@Configuration
@EnableScheduling
public class Alert {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Alert.class);

	private final IRestClient restClient;
	private final MailClient mailClient;
	private final Scraper scraper;

	public Alert(IRestClient restClient, MailClient mailClient, Scraper scraper) {
		this.restClient = restClient;
		this.mailClient = mailClient;
		this.scraper = scraper;
	}

	@Scheduled(fixedRate = 60000, initialDelay = 5000) // 15 min
	public void runTask() {
		log.info(">>> runTask() called...");

		Map<String, TestCenter> restData = getUpdatedDataFromRestAPI();
		Map<String, TestCenter> scrapeData = getScrapedData();
		List<TestCenter> mergedData = mergeScrapedAndRestAPIData(restData, scrapeData);

		iterateRecipientsAndSendOutTimeSlotsMatchingMunicipality(mergedData);
	}

	private List<TestCenter> mergeScrapedAndRestAPIData(Map<String, TestCenter> restData, Map<String, TestCenter> scrapeData) {
		return TestCenter.mergeMapsAndReturnUniqueTestCenters(restData, scrapeData);
	}

	private Map<String, TestCenter> getScrapedData() {
		return scraper.scrapeBookingData();
	}

	private Map<String, TestCenter> getUpdatedDataFromRestAPI() {
		return restClient.filterCentersByUpdated(restClient.findAllAvailableTimeSlots(restClient.convertDataFromApiCallToTestCenter()));
	}

	private void iterateRecipientsAndSendOutTimeSlotsMatchingMunicipality(List<TestCenter> mergedData) {
		for (Recipient recipient : recipients) {

			List<TestCenter> matchingCenters = filterCentersByMunicipality(mergedData, recipient);

			sendMatchingTestCenterToRecipient(recipient, matchingCenters);

		}
	}

	private List<TestCenter> filterCentersByMunicipality(List<TestCenter> mergedData, Recipient recipient) {
		List<TestCenter> myCenters = mergedData.stream().filter(t -> t.getMunicipalityName().equals(recipient.municipality())).collect(Collectors.toList());
		return myCenters;
	}

	private void sendMatchingTestCenterToRecipient(Recipient recipient, List<TestCenter> myCenters) {
		mailClient.sendEmailToRecipient(mailClient.generateEmailToRecipient(myCenters, recipient.email(), recipient.municipality()));
	}

}

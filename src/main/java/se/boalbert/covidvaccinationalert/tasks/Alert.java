package se.boalbert.covidvaccinationalert.tasks;

import org.simplejavamail.api.email.Email;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import se.boalbert.covidvaccinationalert.model.Message;
import se.boalbert.covidvaccinationalert.model.Recipient;
import se.boalbert.covidvaccinationalert.model.TestCenter;
import se.boalbert.covidvaccinationalert.service.IRestClient;
import se.boalbert.covidvaccinationalert.service.MailClient;
import se.boalbert.covidvaccinationalert.service.Scraper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static se.boalbert.covidvaccinationalert.controller.Recipients.recipients;
import static se.boalbert.covidvaccinationalert.model.Message.createMessage;

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

		List<TestCenter> mergedData = mergeTestCenterLists();

		sendOpenSlotsToRecipients(mergedData);
	}

	private List<TestCenter> mergeTestCenterLists() {
		Map<String, TestCenter> restData = restClient.filterCentersByUpdated(restClient.findAllAvailableTimeSlots(restClient.convertDataFromApiCallToTestCenter()));
		Map<String, TestCenter> scrapeData = scraper.scrapeBookingData();
		return mergeScrapedAndRestAPIData(restData, scrapeData);
	}

	private void sendOpenSlotsToRecipients(List<TestCenter> mergedData) {
		for (Recipient recipient : recipients) {

			List<TestCenter> matchingCenters = filterCentersByMunicipality(mergedData, recipient);
			sendMatchingTestCenterToRecipient(recipient, matchingCenters);
		}
	}

	private List<TestCenter> mergeScrapedAndRestAPIData(Map<String, TestCenter> restData, Map<String, TestCenter> scrapeData) {
		return TestCenter.mergeMapsAndReturnUniqueTestCenters(restData, scrapeData);
	}

	private List<TestCenter> filterCentersByMunicipality(List<TestCenter> mergedData, Recipient recipient) {
		return mergedData.stream().
				filter(t -> t.getMunicipalityName()
						.equals(recipient.municipality()))
				.collect(Collectors.toList());
	}

	private void sendMatchingTestCenterToRecipient(Recipient recipient, List<TestCenter> matchingCenters) {
		if (matchingCenters.size() > 0) {
			Message message = createMessage(matchingCenters, recipient);
			Email email = mailClient.setupEmailBuilder(message);
			mailClient.sendEmailToRecipient(email);
		}
	}

}

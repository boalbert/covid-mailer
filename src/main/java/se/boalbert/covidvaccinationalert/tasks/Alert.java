package se.boalbert.covidvaccinationalert.tasks;

import org.simplejavamail.api.email.Email;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import se.boalbert.covidvaccinationalert.model.Message;
import se.boalbert.covidvaccinationalert.model.Recipient;
import se.boalbert.covidvaccinationalert.model.TestCenter;
import se.boalbert.covidvaccinationalert.service.MailClient;
import se.boalbert.covidvaccinationalert.service.RecipientsService;
import se.boalbert.covidvaccinationalert.service.RestClient;
import se.boalbert.covidvaccinationalert.service.Scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static se.boalbert.covidvaccinationalert.model.Message.createMessage;

@Configuration
@EnableScheduling
public class Alert {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Alert.class);

	private final long SEND_ALERT_INTERVAL = 60000;

	private final MailClient mailClient;
	private final RestClient restClient;
	private final RecipientsService recipientsService;
	private final Scraper scraper;

	public Alert(MailClient mailClient, RestClient restClient, RecipientsService recipientsService, Scraper scraper) {
		this.mailClient = mailClient;
		this.restClient = restClient;
		this.recipientsService = recipientsService;
		this.scraper = scraper;
	}

	@Scheduled(fixedRate = SEND_ALERT_INTERVAL, initialDelay = 5000)
	public void runTask() {
		log.info(">>> Fetching TestCenters and mailing recipients...");

		Map<String, TestCenter> restTestCenters = restClient.getTestCentersFromRestAPI();
		Map<String, TestCenter> scrapedTestCenters = scraper.scrapeBookingData();
		List<TestCenter> uniqueTestCenters = mergeData(restTestCenters, scrapedTestCenters);

		List<Recipient> recipients = recipientsService.get();

		sendOpenSlotsToRecipients(uniqueTestCenters, recipients);

		log.info(">>> Waiting for {} minute(s)...", (SEND_ALERT_INTERVAL / 60 / 1000));
	}

	private List<TestCenter> mergeData(Map<String, TestCenter> restData, Map<String, TestCenter> scrapeData) {

		Map<String, TestCenter> merged = Stream.of(restData, scrapeData)
				.flatMap(map -> map.entrySet().stream())
				.collect(Collectors.toMap(
						Map.Entry :: getKey,
						Map.Entry :: getValue,
						(v1, v2) -> new TestCenter(v1.title(), v1.hsaid(), v1.municipalityName(), v1.municipality(), v1.urlBooking(), v1.urlContactCard(), v1.urlContactCardText(), v1.getTesttype(), v1.getTimeslots(), v1.getUpdated())
				));

		log.info("After merge:");
		log.info("- Unique TimeSlots: {}", merged.size());

		return new ArrayList<>(merged.values());
	}

	private void sendOpenSlotsToRecipients(List<TestCenter> mergedData, List<Recipient> recipients) {
		for (Recipient recipient : recipients) {

			List<TestCenter> matchingCenters = findMatchingTestCenters(mergedData, recipient);
			sendMatchingTestCenterToRecipient(recipient, matchingCenters);
		}
	}

	private List<TestCenter> findMatchingTestCenters(List<TestCenter> mergedData, Recipient recipient) {
		return matchMunicipality(mergedData, recipient);
	}

	private void sendMatchingTestCenterToRecipient(Recipient recipient, List<TestCenter> matchingCenters) {
		if (matchingCenters.size() > 0) {
			Message message = createMessage(matchingCenters, recipient);
			Email email = mailClient.setupEmailBuilder(message);
			mailClient.sendEmailToRecipient(email);

			log.info("- Sending {} centers to {}", matchingCenters.size(), recipient.email());
		}
	}

	private List<TestCenter> matchMunicipality(List<TestCenter> mergedData, Recipient recipient) {
		return mergedData.stream().
				filter(t -> t.municipalityName().equals(recipient.municipality()))
				.collect(Collectors.toList());
	}

}

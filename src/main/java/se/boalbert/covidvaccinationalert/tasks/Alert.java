package se.boalbert.covidvaccinationalert.tasks;

import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import se.boalbert.covidvaccinationalert.alert.MailClient;
import se.boalbert.covidvaccinationalert.alert.TweetClient;
import se.boalbert.covidvaccinationalert.model.TestCenter;
import se.boalbert.covidvaccinationalert.service.IRestClient;

import java.util.List;
import java.util.stream.Collectors;

import static se.boalbert.covidvaccinationalert.controller.RecipientController.recipientsGbg;
import static se.boalbert.covidvaccinationalert.controller.RecipientController.recipientsNodingeAle;

@Configuration
@EnableScheduling
public class Alert {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Alert.class);

	private final IRestClient restClient;
	private final MailClient mailClient;
	//TODO Move tweet-functionality to own project / service
	private final TweetClient tweetClient;

	public Alert(IRestClient restClient, MailClient mailClient, TweetClient tweetClient) {
		this.restClient = restClient;
		this.mailClient = mailClient;
		this.tweetClient = tweetClient;
	}

	// TODO Set schedule via environment-variables
	//	@Scheduled(fixedRate = 15000) // 15 sec
	//	@Scheduled(fixedRate = 30000) // 30 sec
//	@Scheduled(fixedRate = 900000, initialDelay = 5000) // 15 min

	// Every 15 minutes. Between 07-22. Every day. Every month. Every year.
	@Scheduled(cron =  "*/15 7-22 * * *", zone = "Europe/Stockholm")
	public void sendAlert() {
		log.info(">>> Running sendAlert...");
		// Incoming fresh data

		// Filter out open timeslots
		List<TestCenter> openTimeSlots = restClient.findAllAvailableTimeSlots(restClient.extractAllCenters());

		// Filter our timeslots which has already been mailed out
		List<TestCenter> newSlots = restClient.filterCentersByUpdated(openTimeSlots);

		// Filter timeslots based on Municipality
		// Two mailing-lists as of now
		List<TestCenter> availableInGothenburg = newSlots.stream().filter(center -> center.getMunicipalityName().equals("Göteborg")).collect(Collectors.toList());
		List<TestCenter> availableInNodingeAndAle = newSlots.stream().filter(center -> center.getMunicipalityName().equals("Nödinge") || center.getMunicipalityName().equals("Ale") || center.getMunicipalityName().equals("Göteborg")).collect(Collectors.toList());

		// Send for GBG
		mailClient.sendEmailToRecipientsBasedOnMunicipality(availableInGothenburg, "Göteborg", recipientsGbg);

		// Send for Nödinge / Ale
		mailClient.sendEmailToRecipientsBasedOnMunicipality(availableInNodingeAndAle, "Nödinge / Ale", recipientsNodingeAle);

		// Send tweets
		//		tweetClient.sendTweets(newSlots);
	}
}

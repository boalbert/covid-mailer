package se.boalbert.covidvaccinationalert.tasks;

import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import se.boalbert.covidvaccinationalert.model.TestCenter;
import se.boalbert.covidvaccinationalert.service.IRestClient;
import se.boalbert.covidvaccinationalert.service.MailClient;
import se.boalbert.covidvaccinationalert.service.Scraper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static se.boalbert.covidvaccinationalert.controller.Recipients.*;

@Configuration
@EnableScheduling
public class Alert {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Alert.class);

	private static Integer tempTimeSlotsGbg = 0;
	private static Integer tempTimeSlotsNodingeAle = 0;
	private static Integer tempTimeSlotsVbg = 0;

	private final IRestClient restClient;
	private final MailClient mailClient;
	private final Scraper scraper;

	public Alert(IRestClient restClient, MailClient mailClient, Scraper scraper) {
		this.restClient = restClient;
		this.mailClient = mailClient;
		this.scraper = scraper;
	}

	@Scheduled(fixedRate = 60000, initialDelay = 5000) // 15 min
	public void sendAlert() {
		log.info(">>> Running sendAlert...");
		// Incoming fresh data
		// Filter our timeslots which has already been mailed out
		Map<String, TestCenter> restDataOpenSlots = restClient.filterCentersByUpdated(restClient.findAllAvailableTimeSlots(restClient.convertDataFromApiCallToTestCenter()));

		// Scrape data
		Map<String, TestCenter> scrapeData = scraper.scrapeBookingData();

		// Merge Scraped data and API data
		List<TestCenter> mergedData = TestCenter.mergeMapsAndReturnUniqueTestCenters(restDataOpenSlots, scrapeData);

		// Filter out timeslots for each mailing-list

		// Nödinge / Ale
		List<TestCenter> openTimeSlotsNodingeAle = mergedData.stream().filter(
				testCenter -> testCenter.getMunicipalityName().equals("Göteborg")
						|| testCenter.getMunicipalityName().equals("Nödinge")
						|| testCenter.getMunicipalityName().equals("Ale")
						|| testCenter.getMunicipalityName().equals("Kungälv")
		).collect(Collectors.toList());

		// Vänersborg / Trollhättan
		List<TestCenter> openTimeSlotsVanersborgTrollhattan = mergedData.stream().filter(
				testCenter -> testCenter.getMunicipalityName().equals("Vänersborg")
						|| testCenter.getMunicipalityName().equals("Trollhättan")).collect(Collectors.toList());

		// Gothenburg
		List<TestCenter> openTimeSlotsGothenburg = mergedData.stream().filter(
				testCenter -> testCenter.getMunicipalityName().equals("Göteborg")).collect(Collectors.toList());

		// Send out emails

		recipientsGbg.add("andersson.albert@gmail.com");

		sendEmailGothenburg(openTimeSlotsGothenburg);

		sendEmailNodingeAle(openTimeSlotsNodingeAle);

		sendEmailVbg(openTimeSlotsVanersborgTrollhattan);
	}

	private void sendEmailGothenburg(List<TestCenter> openTimeSlotsGothenburg) {
		Integer updatedTimeSlotsGbg = openTimeSlotsGothenburg.stream()
				.reduce(0, (testCenterSlot, testCenter) -> Math.toIntExact(testCenterSlot + testCenter.getTimeslots()), Integer :: sum);

		// Send for GBG
		if (!updatedTimeSlotsGbg.equals(tempTimeSlotsGbg)) {
			tempTimeSlotsGbg = updatedTimeSlotsGbg;
			mailClient.sendEmailToRecipientsBasedOnMunicipality(openTimeSlotsGothenburg, "Göteborg", recipientsGbg);
		} else {
			log.info(">>> No updates for GBG:  {}", openTimeSlotsGothenburg.size());
		}
	}

	private void sendEmailNodingeAle(List<TestCenter> openTimeSlotsNodingeAle) {
		Integer updatedTimeSlotsNodingeAle = openTimeSlotsNodingeAle.stream()
				.reduce(0, (testCenterSlot, testCenter) -> Math.toIntExact(testCenterSlot + testCenter.getTimeslots()), Integer :: sum);

		// Send for Nödinge / Ale
		if (!updatedTimeSlotsNodingeAle.equals(tempTimeSlotsNodingeAle)) {
			tempTimeSlotsNodingeAle = updatedTimeSlotsNodingeAle;
			mailClient.sendEmailToRecipientsBasedOnMunicipality(openTimeSlotsNodingeAle, "Nödinge / Ale", recipientsNodingeAle);
		} else {
			log.info(">>> No updates for Nödinge / Ale:  {}", openTimeSlotsNodingeAle.size());
		}
	}

	private void sendEmailVbg(List<TestCenter> openTimeSlotsVanersborgTrollhattan) {
		Integer updatedTimeSlotsVbg = openTimeSlotsVanersborgTrollhattan.stream()
				.reduce(0, (testCenterSlot, testCenter) -> Math.toIntExact(testCenterSlot + testCenter.getTimeslots()), Integer :: sum);

		// Send for VBG / THN
		if (!updatedTimeSlotsVbg.equals(tempTimeSlotsVbg)) {
			tempTimeSlotsVbg = updatedTimeSlotsVbg;
			mailClient.sendEmailToRecipientsBasedOnMunicipality(openTimeSlotsVanersborgTrollhattan, "Vänersborg / Trollhättan", recipientsVanersborgTrollhattan);
		} else {
			log.info(">>> No updates for Trollhättan / VBG:  {}", openTimeSlotsVanersborgTrollhattan.size());
		}
	}
}

package se.boalbert.covidvaccinationalert.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import se.boalbert.covidvaccinationalert.model.TestCenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class Scraper {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Scraper.class);

	public Map<String, TestCenter> scrapeBookingData() {

		Map<String, TestCenter> scrapedTestCenters = new HashMap<>();

		try {
			Document htmlDocument = loadBookingWebsite();
			Elements testCenterDivs = findTestCenterDivs(htmlDocument);
			loopOverElementsAndInsertIntoHashMap(scrapedTestCenters, testCenterDivs);

		} catch (IOException ex) {
			log.error(">>> Error parsing document when scraping...");
			ex.printStackTrace();
		}
		return scrapedTestCenters;
	}

	private Document loadBookingWebsite() throws IOException {
		return Jsoup.connect("https://www.vgregion.se/ov/vaccinationstider/bokningsbara-tider/").get();
	}

	private Elements findTestCenterDivs(Document htmlDocument) {
		return htmlDocument.getElementsByClass("media-body");
	}

	private void loopOverElementsAndInsertIntoHashMap(Map<String, TestCenter> scrapedTestCenters, Elements testCenterDivs) {
		for (Element testCenter : testCenterDivs) {
			// Göteborg: Drive In Nötkärnan Slottskogen
			String heading = testCenter.select("h3").text();

			// https://formular.1177.se/etjanst/ad7ed879-138d-4cfd-ac94-83c0af422e44?externalApplication=COVID_SE2321000131-E000000016315
			String bookingLink = testCenter.select("a").first().attr("href");

			// (Mer än 500 lediga tider kommande 2 veckor)
			String availableSlots = testCenter.select("span").first().text();

			// Chop up text and prepare it for creation of object
			String title = extractTestCenterTitle(heading);
			String municipalityName = extractMunicipalityName(heading);
			Long timeSlots = extractAvailableTimeSlots(availableSlots);

			// Create Object object
			TestCenter newTestCenter = new TestCenter(title, municipalityName, bookingLink, timeSlots);

			insertTestCenters(scrapedTestCenters, heading, newTestCenter);
		}
	}

	public String extractTestCenterTitle(String heading) {
		String[] splitHeading = heading.split(":");
		return splitHeading[1].trim();
	}

	public String extractMunicipalityName(String heading) {
		String[] splitHeading = heading.split(":");
		return splitHeading[0].trim();
	}

	public Long extractAvailableTimeSlots(String openSlotsText) {
		// Split string to:
		// - (Mer än 500
		// - lediga tider kommande 2 veckor)"
		String[] splitAtWord = openSlotsText.split("lediga");
		// Take first part of the String, "(Mer än 500"
		// Replace everything that is not a number with "", i.e nothing
		// Returns "500"
		return Long.valueOf(splitAtWord[0].replaceAll("[^\\d]", ""));
	}

	private void insertTestCenters(Map<String, TestCenter> allSlots, String heading, TestCenter newTestCenter) {
		allSlots.put(extractTestCenterTitle(heading), newTestCenter);
	}
}

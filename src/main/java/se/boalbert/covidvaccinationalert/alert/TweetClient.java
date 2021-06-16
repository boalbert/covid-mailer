package se.boalbert.covidvaccinationalert.alert;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import se.boalbert.covidvaccinationalert.model.TestCenter;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.ArrayList;
import java.util.List;

@Component
public class TweetClient {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(TweetClient.class);

	public void sendTweets(List<TestCenter> availableTimeSlotsInMunicipality) {

		if (availableTimeSlotsInMunicipality.size() > 0) {
			Twitter twitter = TwitterFactory.getSingleton();
			Status status;

			List<String> tweets = createTweets(availableTimeSlotsInMunicipality);

			log.info("Found {} tweets, trying to send...", tweets.size());
			try {
				for (String tweet : tweets) {
					// Randomized timout to avoid getting blocked by twitter API
					// Not sure if necessary
					long timeOut = (long) (Math.random() * (25000 - 15000 + 1) + 15000);

					status = twitter.updateStatus(tweet);
					System.out.println("Send tweet: " + status.getCreatedAt() + " - " + status.getText());
					System.out.println("Waiting for " + (timeOut / 1000) + "s...");
					Thread.sleep(timeOut);
				}
			} catch (TwitterException | InterruptedException ex) {
				ex.printStackTrace();

			}
		} else {
			log.info(">>> 0 new timeslots found, not tweets to send.");
		}
	}

	public List<String> createTweets(List<TestCenter> testCenterList) {

		List<String> tweets = new ArrayList<>();

		for (TestCenter testCenter : testCenterList) {
			String tweet = """
					Kommun: %s
					Mottagning: %s
					Lediga tider: %s
					Boka: %s
										
					Uppdaterad: %s
					""".formatted(testCenter.getMunicipalityName(), testCenter.getTitle(), testCenter.getTimeslots(), testCenter.getUrlBooking(), testCenter.getUpdated());
			tweets.add(tweet);
		}

		return tweets;
	}
}

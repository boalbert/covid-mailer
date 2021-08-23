package se.boalbert.covidvaccinationalert.service;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import se.boalbert.covidvaccinationalert.model.Recipient;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecipientsService {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(RecipientsService.class);

	private final List<Recipient> recipients = new ArrayList<>();

	public List<Recipient> get() {

		log.info("Recipients: {}", recipients.size());
		return this.recipients;
	}

	public boolean add(Recipient recipient) {
		if (!recipients.contains(recipient)) {
			log.info("Recipient added: {}", recipient.toString());
			return recipients.add(recipient);
		}
		log.info("Failed to add, {} already exists.", recipient.toString());
		return false;
	}
}

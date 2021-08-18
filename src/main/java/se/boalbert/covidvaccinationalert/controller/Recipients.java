package se.boalbert.covidvaccinationalert.controller;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.boalbert.covidvaccinationalert.model.Recipient;

import java.util.ArrayList;
import java.util.List;

@RestController
public class Recipients {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Recipients.class);

	public static final List<Recipient> recipients = new ArrayList<>();

	@GetMapping("/recipients/")
	public List<Recipient> getRecipientList() {
		log.info("> GET /recipients/" + " called.");
		log.info(recipients.toString());
		return recipients;
	}

	@PostMapping("/recipients/")
	public String postRecipientObject(@RequestBody Recipient recipient) {

		log.info("POST /recipients/ called.");

		if (!recipients.contains(recipient)) {
			recipients.add(recipient);
			log.info(">> " + recipient + " added to collection.");
			return recipient.toString();
		} else {
			log.info(">> " + recipient + " already exists, not added.");
			return recipient + " already exists.";
		}
	}
}
package se.boalbert.covidvaccinationalert.controller;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;

@RestController
public class RecipientController {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(RecipientController.class);

	public static Collection<String> recipientsGbg = new ArrayList<>();
	public static Collection<String> recipientsNodingeAle = new ArrayList<>();

	//TODO Refactor - handle recipient-lists differently

	@GetMapping("/recipients/gbg/")
	public Collection<String> getRecipientsGbg() {
		log.info(">>> GET /recipients/gbg/");
		return recipientsGbg;
	}

	@GetMapping("/recipients/gbg/{email}")
	public String addRecipientGbg(@PathVariable String email) {
		log.info(">>> GET/POST - /recipients/gbg/{email}");
		if (recipientsGbg.contains(email)) {
			log.info(">>> Failed: '{}' already exists for GBG", email);
			return email + " - already exists for /recipients/gbg";
		} else {
			recipientsGbg.add(email);
			log.info(">>> Success: '{}' added", email);
			return email + " - added to /recipients/gbg";
		}
	}

	@DeleteMapping("/recipients/gbg/{email}")
	public String removeRecipientGbg(@PathVariable String email) {
		log.info(">>> DELETE - /recipients/gbg/{email}");
		if (recipientsGbg.contains(email)) {
			log.info(">>> Success: '{}' removed from /recipients/gbg", email);
			recipientsGbg.remove(email);
			return email + " - removed from /recipients/gbg";
		} else {
			log.info(">>> Failed: '{}' not found in /recipients/gbg", email);
			return email + " - not found in /recipients/gbg";
		}
	}
}
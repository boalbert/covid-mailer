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
	public static Collection<String> recipientsVanersborgTrollhattan = new ArrayList<>();

	//TODO Refactor - handle recipient-lists differently

	@GetMapping("/recipients/{municipality}/")
	public Collection<String> getRecipients(@PathVariable String municipality) {
		log.info(">>> GET /recipients/" + municipality);
		if (municipality.equalsIgnoreCase("gbg")) {
			return recipientsGbg;
		} else if (municipality.equalsIgnoreCase("nodinge")) {
			return recipientsNodingeAle;
		} else if (municipality.equalsIgnoreCase("vbg")) {
			return recipientsVanersborgTrollhattan;
		}

		return new ArrayList<>();
	}

	@GetMapping("/recipients/{municipality}/{email}")
	public String addRecipient(@PathVariable String municipality, @PathVariable String email) {
		log.info(">>> GET/POST - /recipients/{municipality}/{email}");

		if (municipality.equalsIgnoreCase("gbg")) {
			if (recipientsGbg.contains(email)) {
				log.info(">>> Failed: '{}' already exists for {}", email, municipality);
				return email + " - already exists for /recipients/gbg";
			} else {
				recipientsGbg.add(email);
				log.info(">>> Success: '{}' added to {}", email, municipality);
				return email + " - added to /recipients/" + municipality;
			}
		} else if (municipality.equalsIgnoreCase("nodinge")) {
			if (recipientsNodingeAle.contains(email)) {
				log.info(">>> Failed: '{}' already exists for {}", email, municipality);
				return email + " - already exists for /recipients/gbg";
			} else {
				recipientsNodingeAle.add(email);
				log.info(">>> Success: '{}' added to '{}'", email, municipality);
				return email + " - added to /recipients/" + municipality;
			}
		} else if (municipality.equalsIgnoreCase("vbg")) {
			if (recipientsVanersborgTrollhattan.contains(email)) {
				log.info(">>> Failed: '{}' already exists for {}", email, municipality);
				return email + " - already exists for /recipients/vbg";
			} else {
				recipientsVanersborgTrollhattan.add(email);
				log.info(">>> Success: '{}' added to '{}'", email, municipality);
				return email + " - added to /recipients/" + municipality;
			}
		}
		return "Failed to add " + email + " to " + municipality + " list";
	}

	@DeleteMapping("/recipients/{municipality}/{email}")
	public String removeRecipient(@PathVariable String municipality, @PathVariable String email) {
		log.info(">>> DELETE - /recipients/gbg/{email}");
		if (municipality.equalsIgnoreCase("gbg")) {
			if (recipientsGbg.contains(email)) {
				log.info(">>> Success: '{}' removed from /recipients/{}", email, municipality);
				recipientsGbg.remove(email);
				return email + " - removed from /recipients/" + municipality;
			} else {
				log.info(">>> Failed: '{}' not found in /recipients/{}", email, municipality);
				return email + " - not found in /recipients/" + municipality;
			}
		} else if (municipality.equalsIgnoreCase("nodinge")) {
			if (recipientsNodingeAle.contains(email)) {
				log.info(">>> Success: '{}' removed from /recipients/{}", email, municipality);
				recipientsNodingeAle.remove(email);
				return email + " - removed from /recipients/" + municipality;
			} else {
				log.info(">>> Failed: '{}' not found in /recipients/{}", email, municipality);
				return email + " - not found in /recipients/" + municipality;
			}
		} else if (municipality.equalsIgnoreCase("vbg")) {
			if (recipientsVanersborgTrollhattan.contains(email)) {
				log.info(">>> Success: '{}' removed from /recipients/{}", email, municipality);
				recipientsVanersborgTrollhattan.remove(email);
				return email + " - removed from /recipients/" + municipality;
			} else {
				log.info(">>> Failed: '{}' not found in /recipients/{}", email, municipality);
				return email + " - not found in /recipients/" + municipality;
			}
		}
		return "Failed to remove " + email + " from " + municipality + " list";
	}
}
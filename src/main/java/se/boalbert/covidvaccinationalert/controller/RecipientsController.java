package se.boalbert.covidvaccinationalert.controller;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.boalbert.covidvaccinationalert.model.Recipient;
import se.boalbert.covidvaccinationalert.service.RecipientsService;

import java.util.List;


@RestController
public class RecipientsController {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(RecipientsController.class);

	private final RecipientsService recipientsService;

	public RecipientsController(RecipientsService recipientsService) {
		this.recipientsService = recipientsService;
	}

	@GetMapping("/recipients/")
	public List<Recipient> getRecipients() {
		log.info("> GET /recipients/" + " called.");
		return recipientsService.get();
	}

	@PostMapping("/recipients/")
	public boolean postRecipient(@RequestBody Recipient recipient) {
		log.info("> POST /recipients/ called.");
		return recipientsService.add(recipient);
	}
}
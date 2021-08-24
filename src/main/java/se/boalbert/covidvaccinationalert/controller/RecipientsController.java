package se.boalbert.covidvaccinationalert.controller;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;
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
	private List<Recipient> getRecipients() {
		log.info("> GET /recipients/" + " called.");
		return recipientsService.get();
	}

	@PostMapping("/recipients/")
	private boolean postRecipient(@RequestBody Recipient recipient) {
		log.info("> POST /recipients/ called.");
		return recipientsService.add(recipient);
	}

	@DeleteMapping("/recipients/")
	private boolean deleteRecipient(@RequestBody Recipient recipient) {
		log.info("> DELETE /recipients/ called.");
		return recipientsService.remove(recipient);
	}
}
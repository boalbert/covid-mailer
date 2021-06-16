package se.boalbert.covidvaccinationalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static se.boalbert.covidvaccinationalert.controller.RecipientController.recipientsGbg;
import static se.boalbert.covidvaccinationalert.controller.RecipientController.recipientsNodingeAle;

@SpringBootApplication
public class CovidVaccinationAlertApplication {

	public static void main(String[] args) {
		SpringApplication.run(CovidVaccinationAlertApplication.class, args);
	}
}

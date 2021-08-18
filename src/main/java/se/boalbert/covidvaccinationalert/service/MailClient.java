package se.boalbert.covidvaccinationalert.service;

import org.simplejavamail.MailException;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se.boalbert.covidvaccinationalert.model.Recipient;
import se.boalbert.covidvaccinationalert.model.TestCenter;

import java.util.List;

@Component
public class MailClient {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(MailClient.class);

	@Value("${EMAIL_USERNAME}")
	private String EMAIL_USERNAME;

	@Value("${EMAIL_PASSWORD}")
	private String EMAIL_PASSWORD;

	@Value("${FROM_EMAIL}")
	private String FROM_EMAIL;

	@Value("${SMTP_HOST}")
	private String SMTP_HOST;

	@Value("${SMTP_PORT}")
	private int SMTP_PORT;

	@Value("${TRANSPORT_STRATEGY}")
	private String TRANSPORT_STRATEGY;

	public Email generateEmailToRecipient(List<TestCenter> openTimeSlots, Recipient recipient) {
		String content = createEmailContent(openTimeSlots, recipient.municipality());

		return setupEmailBuilder(content, recipient);
	}

	public String createEmailContent(List<TestCenter> testCenterList, String region) {
		StringBuilder stringBuilder = new StringBuilder();

		appendEmailHeader(region, stringBuilder);
		generateEmailBody(testCenterList, stringBuilder);
		appendEmailFooter(stringBuilder);

		return String.valueOf(stringBuilder);
	}

	public Email setupEmailBuilder(String content, Recipient recipient) {

		//TODO Update email in from-field
		return EmailBuilder.startingBlank()
				.from("From", FROM_EMAIL)
				.to("To", recipient.email())
				.withSubject("Ledig Vaccinationstid: " + recipient.municipality())
				.withHTMLText(content)
				.buildEmail();
	}

	private void appendEmailHeader(String region, StringBuilder stringBuilder) {
		String header = """
				<html>
					<body>
						<h1>Lediga tider i <u>%s</u> </h1><br>
						""".formatted(region);

		stringBuilder.append(header);
	}

	private void generateEmailBody(List<TestCenter> testCenterList, StringBuilder stringBuilder) {
		for (TestCenter testCenter : testCenterList) {
			String body = """
					           <p>
					               <b>Stad: </b> %s <br>
					               <b>Mottagning: </b> %s <br>
					               <b>Lediga tider: </b> %s <br>
					               <b>Boka tid: </b> <a href="%s"> Länk </a>
					               <br>
					               <hr>
					               <br>
					           </p>
					""".formatted(testCenter.getMunicipalityName(), testCenter.getTitle(), testCenter.getTimeslots(), testCenter.getUrlBooking());
			stringBuilder.append(body);
		}
	}

	private void appendEmailFooter(StringBuilder stringBuilder) {
		String footer = """
					</body>
				</html>
				""";

		stringBuilder.append(footer);
	}

	public void sendEmailToRecipient(Email email) {
		try {
			log.info("Sending Email: " + email.getRecipients());
			buildEmail().sendMail(email);
		} catch (MailException e) {
			e.printStackTrace();
		}
	}

	private Mailer buildEmail() {
		return MailerBuilder
				.withSMTPServer(SMTP_HOST, SMTP_PORT, EMAIL_USERNAME, EMAIL_PASSWORD)
				.withTransportStrategy(TransportStrategy.valueOf(TRANSPORT_STRATEGY))
				.buildMailer();
	}
}




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
import se.boalbert.covidvaccinationalert.model.Message;

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

	public Email setupEmailBuilder(Message message) {

		//TODO Update email in from-field
		return EmailBuilder.startingBlank()
				.from("From", FROM_EMAIL)
				.to("To", message.recipient().email())
				.withSubject("Ledig Vaccinationstid: " + message.recipient().municipality())
				.withHTMLText(message.content())
				.buildEmail();
	}

	public void sendEmailToRecipient(Email email) {
		try {
			buildMailer().sendMail(email);
		} catch (MailException e) {
			e.printStackTrace();
		}
	}

	private Mailer buildMailer() {
		return MailerBuilder
				.withSMTPServer(SMTP_HOST, SMTP_PORT, EMAIL_USERNAME, EMAIL_PASSWORD)
				.withTransportStrategy(TransportStrategy.valueOf(TRANSPORT_STRATEGY))
				.buildMailer();
	}
}




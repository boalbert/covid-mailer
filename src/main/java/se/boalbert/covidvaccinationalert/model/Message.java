package se.boalbert.covidvaccinationalert.model;

import java.util.List;

public record Message(
		Recipient recipient,
		String content
) {
	public static Message createMessage(List<TestCenter> openTimeSlots, Recipient recipient) {

		String content = Message.createContent(openTimeSlots, recipient);

		return new Message(recipient, content);

	}

	public static String createContent(List<TestCenter> testCenterList, Recipient recipient) {
		StringBuilder stringBuilder = new StringBuilder();

		appendHeader(recipient.municipality(), stringBuilder);
		createBody(testCenterList, stringBuilder);
		appendFooter(stringBuilder);

		return String.valueOf(stringBuilder);
	}

	public static void appendHeader(String region, StringBuilder stringBuilder) {
		String header = """
				<html>
					<body>
						<h1>Lediga tider i <u>%s</u> </h1><br>
						""".formatted(region);

		stringBuilder.append(header);
	}

	public static void createBody(List<TestCenter> testCenterList, StringBuilder stringBuilder) {
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

	public static void appendFooter(StringBuilder stringBuilder) {
		String footer = """
					</body>
				</html>
				""";

		stringBuilder.append(footer);
	}
}
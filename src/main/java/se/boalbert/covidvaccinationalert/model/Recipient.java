package se.boalbert.covidvaccinationalert.model;

public class Recipient {
	private final String email;
	private final String municipality;

	public Recipient(String email, String municipality) {
		this.email = email;
		this.municipality = municipality;
	}

	public String getEmail() {
		return email;
	}

	public String getMunicipality() {
		return municipality;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Recipient recipient = (Recipient) o;

		if (email != null ? !email.equals(recipient.email) : recipient.email != null) return false;
		return municipality != null ? municipality.equals(recipient.municipality) : recipient.municipality == null;
	}

	@Override
	public int hashCode() {
		int result = email != null ? email.hashCode() : 0;
		result = 31 * result + (municipality != null ? municipality.hashCode() : 0);
		return result;
	}
}

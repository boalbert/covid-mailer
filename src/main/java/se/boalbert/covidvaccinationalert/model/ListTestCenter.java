package se.boalbert.covidvaccinationalert.model;

import java.util.List;

public class ListTestCenter {

	private List<TestCenter> testcenters;
	private Long numberWeeks;

	public ListTestCenter(List<TestCenter> testcenters, Long numberWeeks) {
		this.testcenters = testcenters;
		this.numberWeeks = numberWeeks;
	}

	public ListTestCenter() {
	}

	public List<TestCenter> getTestcenters() {
		return this.testcenters;
	}

	public Long getNumberWeeks() {
		return this.numberWeeks;
	}

	public void setNumberWeeks(Long numberWeeks) {
		this.numberWeeks = numberWeeks;
	}

	public void setTestcenters(List<TestCenter> testcenters) {
		this.testcenters = testcenters;
	}

	public String toString() {
		return "ListTestCenter(testcenters=" + this.getTestcenters() + ", numberWeeks=" + this.getNumberWeeks() + ")";
	}
}

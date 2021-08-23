package se.boalbert.covidvaccinationalert.model;

import java.util.List;

public record ListTestCenter(List<TestCenter> testcenters,
		Long numberWeeks) {
}

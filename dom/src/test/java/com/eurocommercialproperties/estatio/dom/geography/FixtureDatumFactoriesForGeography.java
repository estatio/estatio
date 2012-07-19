package com.eurocommercialproperties.estatio.dom.geography;

import com.danhaywood.testsupport.coverage.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForGeography {

	public static FixtureDatumFactory<Country> countries() {
		Country nl = new Country();
		nl.setName("Netherlands");
		Country uk = new Country();
		uk.setName("United Kingdom");
		return new FixtureDatumFactory<Country>(Country.class, nl, uk);
	}

	public static FixtureDatumFactory<State> states() {

		Country uk = new Country();
		uk.setName("United Kingdom");

		State oxon = new State();
		oxon.setName("Oxon");
		oxon.setCountry(uk);
		State cambs = new State();
		cambs.setName("Cambs");
		oxon.setCountry(uk);
		
		return new FixtureDatumFactory<State>(State.class, oxon, cambs);
	}

}

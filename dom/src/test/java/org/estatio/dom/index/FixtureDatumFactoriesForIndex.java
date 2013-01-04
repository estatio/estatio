package org.estatio.dom.index;

import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;

import com.danhaywood.testsupport.coverage.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForIndex {

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

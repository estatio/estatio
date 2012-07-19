package com.eurocommercialproperties.estatio.dom.party;

import com.danhaywood.testsupport.coverage.PojoTester.FixtureDatumFactory;

public class FixtureDatumFactoriesForParty {

	public static FixtureDatumFactory<Party> parties() {
		Party party1 = new Party(){};
		party1.setReference("ref 1");
		Party party2 = new Party(){};
		party2.setReference("ref 2");
		return new FixtureDatumFactory<Party>(Party.class, party1, party2);
	}


}

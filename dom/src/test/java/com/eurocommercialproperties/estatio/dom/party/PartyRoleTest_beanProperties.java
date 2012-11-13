package com.eurocommercialproperties.estatio.dom.party;

import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;
import com.eurocommercialproperties.estatio.dom.FixtureDatumFactoriesForJoda;

public class PartyRoleTest_beanProperties {

	@Test
	public void test() {
		new PojoTester()
			.withFixture(FixtureDatumFactoriesForJoda.dates())
			.withFixture(FixtureDatumFactoriesForParty.parties())
			.exercise(new PartyRole(), FilterSet.excluding("container"));
	}

}

package org.estatio.dom.party;

import org.estatio.dom.FixtureDatumFactoriesForJoda;
import org.estatio.dom.party.PartyRole;
import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class PartyRoleTest_beanProperties {

	@Test
	public void test() {
		new PojoTester()
			.withFixture(FixtureDatumFactoriesForJoda.dates())
			.withFixture(FixtureDatumFactoriesForParty.parties())
			.exercise(new PartyRole(), FilterSet.excluding("container"));
	}

}

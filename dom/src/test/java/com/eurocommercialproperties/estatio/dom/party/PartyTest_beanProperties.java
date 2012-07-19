package com.eurocommercialproperties.estatio.dom.party;

import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class PartyTest_beanProperties {

	public static class PartyForTesting extends Party {}
	@Test
	public void test() {
		new PojoTester().exercise(new PartyForTesting(), FilterSet.excluding("container"));
	}

}

package com.eurocommercialproperties.estatio.dom.communicationchannel;

import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;
import com.eurocommercialproperties.estatio.dom.geography.FixtureDatumFactoriesForGeography;

public class PostalAddressTest_beanProperties {

	@Test
	public void test() {
		new PojoTester()
			.withFixture(FixtureDatumFactoriesForGeography.countries())
			.withFixture(FixtureDatumFactoriesForGeography.states())
			.exercise(new PostalAddress(), FilterSet.excluding("container", "states"));
	}

}

package org.estatio.dom.communicationchannel;

import org.estatio.dom.communicationchannel.PostalAddress;
import org.estatio.dom.geography.FixtureDatumFactoriesForGeography;
import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class PostalAddressTest_beanProperties {

	@Test
	public void test() {
		new PojoTester()
			.withFixture(FixtureDatumFactoriesForGeography.countries())
			.withFixture(FixtureDatumFactoriesForGeography.states())
			.exercise(new PostalAddress(), FilterSet.excluding("container", "states"));
	}

}

package com.eurocommercialproperties.estatio.dom.assets;

import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;
import com.eurocommercialproperties.estatio.dom.FixtureDatumFactoriesForApplib;
import com.eurocommercialproperties.estatio.dom.asset.Property;

public class PropertyTest_beanProperties {

	@Test
	public void test() {
		new PojoTester()
			.withFixture(FixtureDatumFactoriesForApplib.dates())
			.exercise(new Property(), FilterSet.excluding("container"));
	}

}

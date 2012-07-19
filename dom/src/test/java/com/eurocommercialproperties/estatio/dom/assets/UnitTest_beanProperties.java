package com.eurocommercialproperties.estatio.dom.assets;

import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;
import com.eurocommercialproperties.estatio.dom.asset.Unit;

public class UnitTest_beanProperties {

	@Test
	public void test() {
		new PojoTester()
			.withFixture(FixtureDatumFactoriesForAssets.properties())
			.exercise(new Unit(), FilterSet.excluding("container"));
	}

}

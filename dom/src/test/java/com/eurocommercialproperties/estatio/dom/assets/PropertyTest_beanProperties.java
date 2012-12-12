package com.eurocommercialproperties.estatio.dom.assets;

import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;
import com.eurocommercialproperties.estatio.dom.FixtureDatumFactoriesForJoda;
import com.eurocommercialproperties.estatio.dom.asset.Property;

public class PropertyTest_beanProperties {

	@Test
	public void test() {
		new PojoTester()
		    .withFixture(FixtureDatumFactoriesForAssets.properties())
		    .withFixture(FixtureDatumFactoriesForAssets.units())
		    .withFixture(FixtureDatumFactoriesForAssets.propertyActors())
			.withFixture(FixtureDatumFactoriesForJoda.dates())
			.exercise(new Property(), FilterSet.excluding("container","units","propertyActorsRepo", "properties"));
	}

}

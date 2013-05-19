package org.estatio.dom.asset;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

import org.junit.Test;

import org.estatio.dom.FixtureDatumFactoriesForJoda;
import org.estatio.dom.geography.FixtureDatumFactoriesForGeography;

public class PropertyTest_beanProperties {

	@Test
	public void test() {
		new PojoTester()
		    .withFixture(FixtureDatumFactoriesForFixedAssets.properties())
		    .withFixture(FixtureDatumFactoriesForFixedAssets.units())
		    .withFixture(FixtureDatumFactoriesForFixedAssets.propertyActors())
		    .withFixture(FixtureDatumFactoriesForFixedAssets.locations())
			.withFixture(FixtureDatumFactoriesForJoda.dates())
			.withFixture(FixtureDatumFactoriesForGeography.countries())
			.exercise(new Property(), FilterSet.excluding(
			        "container", 
			        "isisJdoSupport", 
			        "units",
			        "fixedAssetRolesService", 
			        "properties", 
			        "unitsRepo", 
			        "actors", 
			        "parties", 
			        "communicationChannels", 
			        "locationLookupService",
			        "partiesService",
			        "roles" ));
	}

}

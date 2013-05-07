package org.estatio.dom.assets;

import org.estatio.dom.asset.Unit;
import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class UnitTest_beanProperties {

	@Test
	public void test() {
		new PojoTester()
			.withFixture(FixtureDatumFactoriesForAssets.properties())
			.exercise(new Unit(), FilterSet.excluding("container", "isisJdoSupport", "leases", "communicationChannels", "countries", "locationLookupService", "location", "fixedAssetRolesService", "roles", "partiesService"));
	}

}

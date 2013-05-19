package org.estatio.dom.asset;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

import org.junit.Test;

public class UnitTest_beanProperties {

	@Test
	public void test() {
		new PojoTester()
			.withFixture(FixtureDatumFactoriesForFixedAssets.properties())
			.exercise(new Unit(), FilterSet.excluding("container", "isisJdoSupport", "leases", "communicationChannels", "countries", "locationLookupService", "location", "fixedAssetRolesService", "roles", "partiesService"));
	}

}

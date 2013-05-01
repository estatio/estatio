package org.estatio.dom.geography;

import org.estatio.dom.geography.State;
import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class StateTest_beanProperties {

	@Test
	public void test() {
		new PojoTester().withFixture(FixtureDatumFactoriesForGeography.countries()).exercise(new State(), FilterSet.excluding("container", "isisJdoSupport"));
	}


}

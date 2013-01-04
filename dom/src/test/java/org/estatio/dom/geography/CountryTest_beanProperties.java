package org.estatio.dom.geography;

import org.estatio.dom.geography.Country;
import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class CountryTest_beanProperties {

	@Test
	public void test() {
		new PojoTester().exercise(new Country(), FilterSet.excluding("container"));
	}

}

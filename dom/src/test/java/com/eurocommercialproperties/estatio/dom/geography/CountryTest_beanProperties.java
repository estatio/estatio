package com.eurocommercialproperties.estatio.dom.geography;

import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class CountryTest_beanProperties {

	@Test
	public void test() {
		new PojoTester().exercise(new Country(), FilterSet.excluding("container"));
	}

}

package com.eurocommercialproperties.estatio.dom.geography;

import org.junit.Test;

import com.danhaywood.testsupport.coverage.PojoTester;
import com.danhaywood.testsupport.coverage.PojoTester.FilterSet;

public class GeographyTest_beanProperties {

	public static class GeographyForTesting extends Geography {}
	
	@Test
	public void test() {
		new PojoTester().exercise(new GeographyForTesting(), FilterSet.excluding("container"));
	}

}

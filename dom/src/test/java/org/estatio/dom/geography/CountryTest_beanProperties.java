package org.estatio.dom.geography;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class CountryTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
		newPojoTester()
		    .exercise(new Country());
	}

}

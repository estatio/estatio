package org.estatio.dom.geography;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class StateTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Country.class))
	        .exercise(new State());
	}


}

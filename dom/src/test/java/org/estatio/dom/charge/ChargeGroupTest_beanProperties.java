package org.estatio.dom.charge;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class ChargeGroupTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
			.exercise(new ChargeGroup());
	}

}

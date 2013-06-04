package org.estatio.dom.charge;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.tax.Tax;

public class ChargeTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(ChargeGroup.class))
	        .withFixture(pojos(Tax.class))
			.exercise(new Charge());
	}

}

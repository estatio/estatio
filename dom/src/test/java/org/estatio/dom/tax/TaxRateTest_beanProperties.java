package org.estatio.dom.tax;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class TaxRateTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Tax.class))
	        .withFixture(pojos(TaxRate.class))
	        .exercise(new TaxRate());
	}

}

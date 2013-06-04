package org.estatio.dom.currency;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class CurrencyTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .exercise(new Currency());
	}

}

package org.estatio.dom.numerator;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class NumeratorTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .exercise(new Numerator());
	}

}

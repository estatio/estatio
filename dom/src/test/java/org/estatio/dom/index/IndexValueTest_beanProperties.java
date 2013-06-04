package org.estatio.dom.index;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class IndexValueTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(IndexBase.class))
	        .exercise(new IndexValue());
	}


}

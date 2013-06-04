package org.estatio.dom.index;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class IndexTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .exercise(new Index());
	}


}

package org.estatio.dom.tag;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class TagTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .exercise(new Tag());
	}

}

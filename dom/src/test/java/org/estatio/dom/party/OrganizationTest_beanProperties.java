package org.estatio.dom.party;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class OrganizationTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .exercise(new Organisation());
	}

}

package org.estatio.dom.party;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class PartyTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .exercise(new PartyForTesting());
	}

}

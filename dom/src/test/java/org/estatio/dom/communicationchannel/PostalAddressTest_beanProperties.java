package org.estatio.dom.communicationchannel;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;

public class PostalAddressTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
			.withFixture(pojos(Country.class))
			.withFixture(pojos(State.class))
			.exercise(new PostalAddress());
	}

}

package org.estatio.dom.financial;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyTest_beanProperties.PartyForTesting;

public class BankAccountTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Country.class))
	        .withFixture(pojos(Party.class, PartyForTesting.class))
	        .exercise(new BankAccount());
	}

}

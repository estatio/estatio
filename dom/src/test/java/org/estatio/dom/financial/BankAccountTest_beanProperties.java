package org.estatio.dom.financial;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.Lockable;
import org.estatio.dom.PojoTester.FixtureDatumFactory;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class BankAccountTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Country.class))
	        .withFixture(pojos(Party.class, PartyForTesting.class))
	        .withFixture(statii())
	        .exercise(new BankAccount());
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<Lockable> statii() {
        return new FixtureDatumFactory(Lockable.class, (Object[])org.estatio.dom.Status.values());
    }

}

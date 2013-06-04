package org.estatio.dom.invoice;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyTest_beanProperties.PartyForTesting;

public class InvoiceTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Party.class, PartyForTesting.class))
	        .withFixture(pojos(Currency.class))
	        .withFixture(pojos(InvoiceProvenance.class, InvoiceProvenanceForTesting.class))
	        .exercise(new Invoice());
	}


}

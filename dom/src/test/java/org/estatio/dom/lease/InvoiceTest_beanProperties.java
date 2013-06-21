package org.estatio.dom.lease;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceSource;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class InvoiceTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Party.class, PartyForTesting.class))
	        .withFixture(pojos(Currency.class))
	        // it's necessary to use an actual valid concrete class, because having 
	        // a test class (eg public class InvoiceSourceForTesting implements InvoiceSource {}) trips up DN.
	        // the reason is that the DN enhancer doesn't seem to enhance it
	        .withFixture(pojos(InvoiceSource.class, Lease.class))
	        .exercise(new Invoice());
	}


}

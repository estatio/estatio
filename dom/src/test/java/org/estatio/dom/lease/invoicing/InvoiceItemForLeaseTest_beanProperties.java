package org.estatio.dom.lease.invoicing;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.LeaseTermForTesting;
import org.estatio.dom.tax.Tax;
import org.junit.Ignore;
import org.junit.Test;

public class InvoiceItemForLeaseTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	@Ignore
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Tax.class))
	        .withFixture(pojos(Charge.class))
	        .withFixture(pojos(Invoice.class))
	        .withFixture(pojos(LeaseTermForTesting.class))
	        .exercise(new InvoiceItemForLease());
	}

}

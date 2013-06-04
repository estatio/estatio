package org.estatio.dom.lease.invoicing;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.tax.Tax;

public class InvoiceItemForLeaseTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Tax.class))
	        .withFixture(pojos(Charge.class))
	        .withFixture(pojos(Invoice.class))
	        .withFixture(pojos(LeaseTerm.class))
	        .exercise(new InvoiceItemForLease());
	}

}

package org.estatio.dom.lease.invoicing;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.Lockable;
import org.estatio.dom.PojoTester.FixtureDatumFactory;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForTesting;
import org.estatio.dom.tax.Tax;

import org.junit.Ignore;
import org.junit.Test;

public class InvoiceItemForLeaseTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Tax.class))
	        .withFixture(pojos(Charge.class))
	        .withFixture(pojos(Invoice.class))
	        .withFixture(pojos(LeaseTerm.class, LeaseTermForTesting.class))
            .withFixture(statii())
	        .exercise(new InvoiceItemForLease());
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<Lockable> statii() {
        return new FixtureDatumFactory(Lockable.class, (Object[])org.estatio.dom.Status.values());
    }

}

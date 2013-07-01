package org.estatio.dom.invoice;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.Lockable;
import org.estatio.dom.PojoTester.FixtureDatumFactory;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;

public class InvoiceItemTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Charge.class))
	        .withFixture(pojos(Tax.class))
	        .withFixture(pojos(Invoice.class))
            .withFixture(statii())
	        .exercise(new InvoiceItemForTesting());
	}


    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<Lockable> statii() {
        return new FixtureDatumFactory(Lockable.class, (Object[])org.estatio.dom.Status.values());
    }

}

package org.estatio.dom.invoice;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;

public class InvoiceItemTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Charge.class))
	        .withFixture(pojos(Tax.class))
	        .withFixture(pojos(Invoice.class))
	        .exercise(new InvoiceItemForTesting());
	}


}

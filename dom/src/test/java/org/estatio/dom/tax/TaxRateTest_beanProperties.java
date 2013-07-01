package org.estatio.dom.tax;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.Lockable;
import org.estatio.dom.PojoTester.FixtureDatumFactory;

public class TaxRateTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Tax.class))
	        .withFixture(pojos(TaxRate.class))
            .withFixture(statii())
	        .exercise(new TaxRate());
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<Lockable> statii() {
        return new FixtureDatumFactory(Lockable.class, (Object[])org.estatio.dom.Status.values());
    }

}

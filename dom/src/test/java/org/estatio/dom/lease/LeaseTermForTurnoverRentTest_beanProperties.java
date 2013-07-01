package org.estatio.dom.lease;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.Lockable;
import org.estatio.dom.PojoTester.FixtureDatumFactory;

public class LeaseTermForTurnoverRentTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(LeaseItem.class))
            .withFixture(pojos(LeaseTerm.class, LeaseTermForTesting.class))
            .withFixture(statii())
	        .exercise(new LeaseTermForTurnoverRent());
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<Lockable> statii() {
        return new FixtureDatumFactory(Lockable.class, (Object[])org.estatio.dom.lease.LeaseTermStatus.values());
    }

}

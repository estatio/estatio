package org.estatio.dom.lease;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.junit.Ignore;
import org.junit.Test;

public class LeaseTermForServiceChargeTest_beanProperties extends AbstractBeanPropertiesTest {

    @Ignore
	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(LeaseItem.class))
            .withFixture(pojos(LeaseTerm.class))
	        .withFixture(pojos(LeaseTermForTesting.class))
	        .exercise(new LeaseTermForServiceCharge());
	}

}

package org.estatio.dom.lease;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.junit.Ignore;
import org.junit.Test;

public class LeaseTermForServiceChargeTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(LeaseItem.class))
            .withFixture(pojos(LeaseTerm.class, LeaseTermForTesting.class))
	        .exercise(new LeaseTermForServiceCharge());
	}

}

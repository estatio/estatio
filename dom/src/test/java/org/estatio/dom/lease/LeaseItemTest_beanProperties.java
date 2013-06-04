package org.estatio.dom.lease;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.charge.Charge;

public class LeaseItemTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Charge.class))
	        .withFixture(pojos(Lease.class))
	        .exercise(new LeaseItem());
	}


}

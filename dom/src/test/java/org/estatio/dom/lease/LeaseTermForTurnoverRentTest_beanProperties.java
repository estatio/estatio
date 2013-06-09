package org.estatio.dom.lease;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.junit.Ignore;
import org.junit.Test;

public class LeaseTermForTurnoverRentTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	@Ignore
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(LeaseItem.class))
	        .withFixture(pojos(LeaseTerm.class))
	        .exercise(new LeaseTermForTurnoverRent());
	}

}

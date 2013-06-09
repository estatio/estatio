package org.estatio.dom.lease;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.junit.Ignore;
import org.junit.Test;

public class LeaseTermTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	@Ignore
	//FIXME: making the LeaseTerm abstract fails the test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(LeaseItem.class))
	        .withFixture(pojos(LeaseTermImpl.class))
	        .exercise(new LeaseTermImpl());
	}


}

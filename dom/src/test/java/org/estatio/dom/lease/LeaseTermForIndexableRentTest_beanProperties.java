package org.estatio.dom.lease;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.Lockable;
import org.estatio.dom.PojoTester.FilterSet;
import org.estatio.dom.PojoTester.FixtureDatumFactory;
import org.estatio.dom.index.Index;

import org.junit.Ignore;
import org.junit.Test;

public class LeaseTermForIndexableRentTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(LeaseItem.class))
	        .withFixture(pojos(LeaseTerm.class, LeaseTermForTesting.class))
	        .withFixture(pojos(Index.class))
            .withFixture(statii())
	        .exercise(new LeaseTermForIndexableRent(),
	                // TODO: bug in PojoTester; claims there's interference between 
	                // startDate and baseIndexStartDate, however are just the same datatype
	                FilterSet.excluding("baseIndexStartDate"));
	}

	
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<Lockable> statii() {
        return new FixtureDatumFactory(Lockable.class, (Object[])LeaseTermStatus.values());
    }

}

package org.estatio.dom.lease;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.PojoTester.FilterSet;
import org.estatio.dom.tag.Tag;

public class LeaseUnitTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(Lease.class))
	        .withFixture(pojos(UnitForLease.class))
	        .withFixture(pojos(Tag.class))
	        .exercise(new LeaseUnit(),
	                FilterSet.excluding("sector", "activity", "brand",
	                        // TODO: bug in PojoTester; claims there's interference between 
	                        // these fields, however are just the same datatype
	                        "sectorTag", "activityTag"));
	}

}

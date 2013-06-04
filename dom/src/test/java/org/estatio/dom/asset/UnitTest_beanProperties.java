package org.estatio.dom.asset;

import com.danhaywood.isis.wicket.gmap3.applib.Location;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class UnitTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
			.withFixture(pojos(Property.class))
			.withFixture(pojos(Location.class))
			.exercise(new Unit());
	}

}

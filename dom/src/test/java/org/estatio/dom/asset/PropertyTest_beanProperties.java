package org.estatio.dom.asset;

import com.danhaywood.isis.wicket.gmap3.applib.Location;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.Lockable;
import org.estatio.dom.PojoTester.FixtureDatumFactory;
import org.estatio.dom.geography.Country;

public class PropertyTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
		    .withFixture(pojos(Country.class))
		    .withFixture(pojos(Location.class))
            .withFixture(statii())
			.exercise(new Property());
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<Lockable> statii() {
        return new FixtureDatumFactory(Lockable.class, (Object[])org.estatio.dom.Status.values());
    }

}

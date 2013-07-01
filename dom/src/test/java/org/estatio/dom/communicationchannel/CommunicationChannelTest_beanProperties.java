package org.estatio.dom.communicationchannel;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.Lockable;
import org.estatio.dom.PojoTester.FixtureDatumFactory;

public class CommunicationChannelTest_beanProperties extends AbstractBeanPropertiesTest {

    @Test
    public void test() {
        newPojoTester()
            .withFixture(statii())
            .exercise(new CommunicationChannelForTesting());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<Lockable> statii() {
        return new FixtureDatumFactory(Lockable.class, (Object[])org.estatio.dom.Status.values());
    }

}

package org.estatio.dom.communicationchannel;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;

public class CommunicationChannelTest_beanProperties extends AbstractBeanPropertiesTest {

    @Test
    public void test() {
        newPojoTester()
            .exercise(new CommunicationChannelForTesting());
    }
    
}

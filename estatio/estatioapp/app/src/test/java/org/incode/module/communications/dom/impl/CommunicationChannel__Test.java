package org.incode.module.communications.dom.impl;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester;

public class CommunicationChannel__Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .exercise(new CommunicationChannelForTesting(), PojoTester.FilterSet.excluding("owner"));
        }
    }


    public static class Constructor_Test extends CommunicationChannel__Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }
    }


}
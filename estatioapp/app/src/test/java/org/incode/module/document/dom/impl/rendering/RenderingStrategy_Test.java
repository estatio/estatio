package org.incode.module.document.dom.impl.rendering;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

public class RenderingStrategy_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .exercise(new RenderingStrategyForTesting());
        }

    }

    public static class Constructor_Test extends RenderingStrategy_Test {


        @Ignore
        @Test
        public void happy_case() throws Exception {


        }

    }


    public static class NewRenderer_Test extends RenderingStrategy_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }

    }


}
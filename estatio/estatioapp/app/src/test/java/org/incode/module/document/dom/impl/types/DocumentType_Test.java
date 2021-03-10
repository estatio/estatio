package org.incode.module.document.dom.impl.types;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.docs.Document_Test;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

public class DocumentType_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .exercise(new DocumentTypeForTesting());
        }

    }


    public static class Constructor_Test extends Document_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }
    }



}
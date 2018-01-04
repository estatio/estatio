package org.incode.module.document.dom.impl.paperclips;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.docs.DocumentAbstractForTesting;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.FixtureDatumFactoriesForJoda;

public class Paperclip_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(FixtureDatumFactoriesForJoda.dateTimes())
                    .withFixture(pojos(DocumentAbstract.class, DocumentAbstractForTesting.class))
                    .exercise(new PaperclipForTesting());
        }

    }

    public static class Title_Test extends Paperclip_Test {


        @Ignore
        @Test
        public void when_no_role() throws Exception {


        }

        @Ignore
        @Test
        public void when_has_a_role() throws Exception {


        }
    }


    public static class DocumentDate_Test extends Paperclip_Test {

        @Ignore
        @Test
        public void when_document() throws Exception {

        }

        @Ignore
        @Test
        public void when_document_template() throws Exception {

        }

    }


}
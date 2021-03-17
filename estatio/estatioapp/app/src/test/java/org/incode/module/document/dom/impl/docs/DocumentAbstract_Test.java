package org.incode.module.document.dom.impl.docs;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeForTesting;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.FixtureDatumFactoriesForApplib;

public class DocumentAbstract_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(FixtureDatumFactoriesForApplib.blobs())
                    .withFixture(FixtureDatumFactoriesForApplib.clobs())
                    .withFixture(pojos(DocumentType.class, DocumentTypeForTesting.class))
                    .exercise(new DocumentAbstractForTesting());
        }

    }


    public static class Constructor_Test extends DocumentAbstract_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }
    }

    public static class Blob_Test extends DocumentAbstract_Test {

        @Ignore
        @Test
        public void set_happy_case() throws Exception {

        }

        @Ignore
        @Test
        public void hidden_if_sort_is_not_a_blob() throws Exception {

        }

    }

    public static class Clob_Test extends DocumentAbstract_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }


        @Ignore
        @Test
        public void hidden_if_sort_is_not_a_clob() throws Exception {

        }
    }

    public static class TextData_Test extends DocumentAbstract_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }


        @Ignore
        @Test
        public void hidden_if_sort_is_not_a_text() throws Exception {

        }
    }


    public static class AsDataSource_Test extends Document_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }

    }


}
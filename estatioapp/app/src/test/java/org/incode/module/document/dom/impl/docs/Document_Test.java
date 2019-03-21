package org.incode.module.document.dom.impl.docs;

import java.util.Random;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.base.dom.MimeTypeData;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeForTesting;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.FixtureDatumFactoriesForJoda;

import static org.assertj.core.api.Assertions.assertThat;

public class Document_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(FixtureDatumFactoriesForJoda.dateTimes())
                    .withFixture(pojos(DocumentType.class, DocumentTypeForTesting.class))
                    .exercise(new Document());
        }

    }

    public static class Constructor_Test extends Document_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }
    }

    public static class Render_Test extends Document_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }
    }

    public static class Blob_Test extends Document_Test {

        @Ignore
        @Test
        public void set_happy_case() throws Exception {

        }

        @Test
        public void get_when_sort_blob() throws Exception {
            // given
            final Document document = new Document();
            final byte[] b = new byte[20];
            new Random().nextBytes(b);
            document.setBlobBytes(b);
            document.setMimeType(MimeTypeData.APPLICATION_PDF.asStr());
            document.setName("Foo doc");
            document.setSort(DocumentSort.BLOB);

            // when
            final Blob blob = document.getBlob();

            // then
            assertThat(blob).isNotNull();
        }

        @Test
        public void get_when_not_sort_blob() throws Exception {
            // given
            final Document document = new Document();
            document.setMimeType(MimeTypeData.APPLICATION_PDF.asStr());
            document.setName("Foo doc");
            document.setSort(DocumentSort.CLOB);

            // when
            final Blob blob = document.getBlob();

            // then
            assertThat(blob).isNull();
        }
    }

    public static class Clob_Test extends Document_Test {

        @Ignore
        @Test
        public void set_happy_case() throws Exception {

        }

        @Test
        public void get_when_sort_clob() throws Exception {
            // given
            final Document document = new Document();
            document.setClobChars("CLOBCHARS");
            document.setMimeType(MimeTypeData.APPLICATION_PDF.asStr());
            document.setName("Foo doc");
            document.setSort(DocumentSort.CLOB);

            // when
            final Clob clob = document.getClob();

            // then
            assertThat(clob).isNotNull();
        }

        @Test
        public void get_when_not_sort_clob() throws Exception {
            // given
            final Document document = new Document();
            document.setMimeType(MimeTypeData.APPLICATION_PDF.asStr());
            document.setName("Foo doc");
            document.setSort(DocumentSort.BLOB);

            // when
            final Clob clob = document.getClob();

            // then
            assertThat(clob).isNull();
        }
    }

    public static class ExternalUrl_Test extends Document_Test {

        @Ignore
        @Test
        public void hidden_if_sort_is_not_external() throws Exception {

        }

    }

    public static class AsChars_Test extends Document_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }

    }

    public static class AsBytes_Test extends Document_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }

    }

}
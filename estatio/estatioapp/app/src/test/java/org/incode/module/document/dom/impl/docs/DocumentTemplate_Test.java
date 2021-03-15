package org.incode.module.document.dom.impl.docs;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeForTesting;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.FixtureDatumFactoriesForApplib;

import org.estatio.module.invoice.dom.DocumentTypeData;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentTemplate_Test {

    DocumentTemplate template;


    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(FixtureDatumFactoriesForApplib.blobs())
                    .withFixture(pojos(DocumentType.class, DocumentTypeForTesting.class))
                    .exercise(new DocumentTemplateForTesting());
        }

    }


    public static class Constructor_Test extends Document_Test {

        @Test
        public void can_create_for_Blob() throws Exception {

        }

        @Test
        public void can_create_for_Clob() throws Exception {

        }

        @Test
        public void can_create_for_text() throws Exception {

        }

    }


    public static class Applicable_Test extends DocumentTemplate_Test {

        @Before
        public void setUp() throws Exception {
        }

        public static class Disable_Test extends Applicable_Test {

            @Ignore
            @Test
            public void xxx() throws Exception {

            }
        }

        public static class Choices_Test extends Applicable_Test {

            @Ignore
            @Test
            public void xxx() throws Exception {

            }
        }

        public static class Validate_Test extends Applicable_Test {

            @Ignore
            @Test
            public void xxx() throws Exception {

            }
        }

        public static class ActionInvocation_Test extends Applicable_Test {

            @Ignore
            @Test
            public void xxx() throws Exception {

            }
        }

        public static class Programmatic_Test extends Applicable_Test {

            @Ignore
            @Test
            public void creates_if_does_not_exist() throws Exception {

            }

            @Ignore
            @Test
            public void updates_if_does_exist() throws Exception {

            }
        }

    }

    public static class NotApplicable_Test extends DocumentTemplate_Test {

        @Before
        public void setUp() throws Exception {
        }

        public static class Disable_Test extends NotApplicable_Test {

            @Ignore
            @Test
            public void xxx() throws Exception {

            }
        }

        public static class Choices_Test extends NotApplicable_Test {

            @Ignore
            @Test
            public void xxx() throws Exception {

            }
        }

        public static class ActionInvocation_Test extends NotApplicable_Test {

            @Ignore
            @Test
            public void delegates_down_to_ApplicabilityRepository() throws Exception {

            }
        }

    }


    public static class AppliesTo_Test extends DocumentTemplate_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }

    }

    public static class NewBinder_Test extends DocumentTemplate_Test {

        @Ignore
        @Test
        public void for_domain_object_when_not_applicable_to_its_type() throws Exception {

        }

        @Ignore
        @Test
        public void for_domain_object_when_is_applicable_to_its_type() throws Exception {

        }

    }

    public static class NewBinding_Test extends DocumentTemplate_Test {

        /**
         * ie when not applicable to the type of the domain object.
         */
        @Ignore
        @Test
        public void throws_exception_when_no_binder() throws Exception {

        }

        @Ignore
        @Test
        public void creates_binding_from_binder_applicable_to_domain_objects_type() throws Exception {

        }

    }

    public static class AsChars_Test extends DocumentTemplate_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }

    }

    public static class AsBytes_Test extends DocumentTemplate_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {

        }

    }

    public static class Preview_Test extends DocumentTemplate_Test {

        @Ignore
        @Test
        public void throws_exception_if_rendering_strategy_cannot_preview_to_url() throws Exception {
        }

        @Ignore
        @Test
        public void input_bytes_to_output_bytes() throws Exception {
        }

        @Ignore
        @Test
        public void input_chars_to_output_bytes() throws Exception {
        }

        @Ignore
        @Test
        public void input_bytes_to_output_chars() throws Exception {
        }

        @Ignore
        @Test
        public void input_chars_to_output_chars() throws Exception {
        }


    }


    public static class CreateAndScheduleRender_Test extends DocumentTemplate_Test {

        @Ignore
        @Test
        public void creates_the_document_and_schedules_using_BackgroundService() throws Exception {

        }

    }

    public static class CreateAndRender_Test extends DocumentTemplate_Test {

        @Ignore
        @Test
        public void creates_the_document_and_renders_immediately() throws Exception {

        }

    }

    public static class CreateDocumentFromDataModel_Test extends DocumentTemplate_Test {

        @Ignore
        @Test
        public void use_name_rendering_strategy_to_name_and_delegates_to_DocumentRepository_to_create() throws Exception {

        }

        @Ignore
        @Test
        public void when_name_rendering_strategy_fails() throws Exception {

        }

    }

    public static class Render_Test extends DocumentTemplate_Test {

        @Ignore
        @Test
        public void input_bytes_to_output_bytes() throws Exception {
        }

        @Ignore
        @Test
        public void input_chars_to_output_bytes() throws Exception {
        }

        @Ignore
        @Test
        public void input_bytes_to_output_chars() throws Exception {
        }

        @Ignore
        @Test
        public void input_chars_to_output_chars() throws Exception {
        }

    }


    public static class FileSuffix_Test extends DocumentTemplate_Test {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        DocumentType mockDocumentType;

        @Before
        public void setUp() throws Exception {
            context.checking(new Expectations() {{
                allowing(mockDocumentType).getReference();
                will(returnValue(DocumentTypeData.INVOICE.getRef()));
            }});
            template = new DocumentTemplate(mockDocumentType, null, "/ITA", "pdf", false, null, null, null, null);
        }

        @Test
        public void ends_with_suffix() throws Exception {
            final String docName = template.withFileSuffix("foo.pdf");

            assertThat(docName).isEqualTo("foo.pdf");
        }

        @Test
        public void ends_with_no_suffix() throws Exception {
            final String docName = template.withFileSuffix("foo");

            assertThat(docName).isEqualTo("foo.pdf");
        }

        @Test
        public void ends_with_other_suffix() throws Exception {
            final String docName = template.withFileSuffix("foo.doc");

            assertThat(docName).isEqualTo("foo.doc.pdf");
        }

        @Test
        public void ends_with_dot() throws Exception {
            final String docName = template.withFileSuffix("foo...");
            assertThat(docName).isEqualTo("foo.pdf");
        }
    }
}
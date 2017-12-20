package org.incode.module.document.dom.impl.applicability;

import org.assertj.core.api.Java6Assertions;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateForTesting;

public class Applicability_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    DocumentTemplate mockDocumentTemplate;

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(DocumentTemplate.class, DocumentTemplateForTesting.class))
                    .exercise(new Applicability());
        }

    }

    public static class Constructor_Test extends Applicability_Test {

        @Test
        public void happy_case() throws Exception {

            // when
            final Applicability applicability =
                    new Applicability(mockDocumentTemplate,
                            SomeDomainObject.class, SomeRendererModelFactory.class, SomeAttachmentAdvisor.class);

            // then
            Java6Assertions.assertThat(applicability.getDocumentTemplate()).isEqualTo(mockDocumentTemplate);
            Java6Assertions.assertThat(applicability.getDomainClassName()).isEqualTo(SomeDomainObject.class.getName());
            Java6Assertions.assertThat(applicability.getRendererModelFactoryClassName()).isEqualTo(SomeRendererModelFactory.class.getName());
            Java6Assertions.assertThat(applicability.getAttachmentAdvisorClassName()).isEqualTo(SomeAttachmentAdvisor.class.getName());

        }
    }

}
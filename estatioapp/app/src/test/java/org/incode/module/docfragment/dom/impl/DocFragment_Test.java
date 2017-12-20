package org.incode.module.docfragment.dom.impl;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.services.wrapper.WrapperFactory;

public class DocFragment_Test {

    DocFragment domainObject;

    private static final String originalText = "The invoice should be paid by {this.dueDate}";

    @Before
    public void setUp() throws Exception {
        domainObject = new DocFragment("invoice.Invoice", "due", "/ITA", originalText);
    }

    public static class Name extends DocFragment_Test {

        @Test
        public void happyCase() throws Exception {
            // given
            Assertions.assertThat(domainObject.getTemplateText()).isEqualTo(originalText);

            // when
            String name = "The invoice should be paid by {this.dueDate}, within {this.paymentTerms} days";
            domainObject.setTemplateText(name);

            // then
            Assertions.assertThat(domainObject.getTemplateText()).isEqualTo(name);
        }
    }

}

package org.incode.module.document.dom.impl.docs;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class DocumentAbstract_downloadTextAsClob_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DocumentAbstract<?> mockDocumentAbstract;

    DocumentAbstract_downloadTextAsClob mixin;

    @Before
    public void setUp() throws Exception {
        // when
        mixin = new DocumentAbstract_downloadTextAsClob(mockDocumentAbstract);
    }


    public static class Hidden_Test extends DocumentAbstract_downloadTextAsClob_Test {

        @Ignore
        @Test
        public void hidden_if_sort_is_not_text() throws Exception {

        }
    }

    public static class ActionInvocation_Test extends DocumentAbstract_downloadTextAsClob_Test {

        @Ignore
        @Test
        public void creates_a_new_Clob_on_the_fly() throws Exception {

        }
    }


}
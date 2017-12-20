package org.incode.module.document.dom.impl.docs;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class DocumentAbstract_attachedTo_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DocumentAbstract<?> mockDocumentAbstract;

    DocumentAbstract_attachedTo mixin;

    @Before
    public void setUp() throws Exception {
        // when
        mixin = new DocumentAbstract_attachedTo(mockDocumentAbstract);
    }


    public static class ActionInvocation_Test extends DocumentAbstract_attachedTo_Test {

        @Ignore
        @Test
        public void delegates_to_PaperclipRepository() throws Exception {

        }
    }


}
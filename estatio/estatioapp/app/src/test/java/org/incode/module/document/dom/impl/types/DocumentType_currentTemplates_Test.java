package org.incode.module.document.dom.impl.types;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class DocumentType_currentTemplates_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DocumentType mockDocumentType;

    DocumentType_currentTemplates mixin;

    @Before
    public void setUp() throws Exception {
        // when
        mixin = new DocumentType_currentTemplates(mockDocumentType);
    }


   public static class ActionInvocation_Test extends DocumentType_currentTemplates_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }
    }


}
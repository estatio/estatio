package org.incode.module.document.dom.impl.docs;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class DocumentTemplate_updateText_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DocumentTemplate mockDocumentTemplate;

    DocumentTemplate_updateText mixin;

    @Before
    public void setUp() throws Exception {
        // when
        mixin = new DocumentTemplate_updateText(mockDocumentTemplate);
    }


    public static class Hide_Test extends DocumentTemplate_updateText_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }
    }

    public static class Default_Test extends DocumentTemplate_updateText_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }
    }

    public static class ActionInvocation_Test extends DocumentTemplate_updateText_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }
    }


}
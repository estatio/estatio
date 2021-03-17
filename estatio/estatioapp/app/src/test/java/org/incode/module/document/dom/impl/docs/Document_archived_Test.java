package org.incode.module.document.dom.impl.docs;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.dom.impl.docs.minio.Document_archived;

public class Document_archived_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Document mockDocument;

    Document_archived mixin;

    @Before
    public void setUp() throws Exception {
        // when
        mixin = new Document_archived(mockDocument);
    }

    public static class ActionInvocation_Test extends Document_archived_Test {

        @Ignore
        @Test
        public void xxx() throws Exception {

        }
    }


}
package org.incode.module.document.dom.impl.docs;

import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class Document_downloadExternalUrlAsBlob_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Document mockDocument;

    Document_downloadExternalUrlAsBlob mixin;

    @Before
    public void setUp() throws Exception {
        // when
        mixin = new Document_downloadExternalUrlAsBlob(mockDocument);
    }

    public static class HideTest extends Document_downloadExternalUrlAsBlob_Test {

        @Ignore
        @Test
        public void hidden_if_not_an_external_blob() throws Exception {

        }

    }

    public static class ActionInvocation_Test extends Document_downloadExternalUrlAsBlob_Test {

        @Ignore
        @Test
        public void delegates_to_UrlDownloadService() throws Exception {

        }
    }


}
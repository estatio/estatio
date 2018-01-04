package org.incode.module.document.dom.mixins;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class DocumentTemplateForAtPathService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    DocumentTemplateForAtPathService service;

    @Before
    public void setUp() throws Exception {
        service = new DocumentTemplateForAtPathService();
    }

    public static class DocumentTemplates_Test extends DocumentTemplateForAtPathService_Test {

        @Ignore
        @Test
        public void when_no_atPath_then_empty() throws Exception {


        }

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }

    }



}
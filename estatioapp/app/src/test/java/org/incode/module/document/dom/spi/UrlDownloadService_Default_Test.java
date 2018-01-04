package org.incode.module.document.dom.spi;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class UrlDownloadService_Default_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    UrlDownloadService.Default service;


    @Before
    public void setUp() throws Exception {
        service = new UrlDownloadService.Default();
    }

    public static class downloadAsBlob_Test extends UrlDownloadService_Default_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }
    }

    public static class downloadAsClob_Test extends UrlDownloadService_Default_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }
    }

}
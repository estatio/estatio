package org.incode.module.document.dom.spi;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.document.spi.minio.ExternalUrlDownloadService;

public class ExternalUrlDownloadService_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    ExternalUrlDownloadService service;


    @Before
    public void setUp() throws Exception {
        service = new ExternalUrlDownloadService();
    }

    public static class downloadAsBlob_Test extends ExternalUrlDownloadService_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }
    }

    public static class downloadAsClob_Test extends ExternalUrlDownloadService_Test {

        @Ignore
        @Test
        public void happy_case() throws Exception {


        }
    }

}
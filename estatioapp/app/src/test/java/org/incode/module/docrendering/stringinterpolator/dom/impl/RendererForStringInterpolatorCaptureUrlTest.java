package org.incode.module.docrendering.stringinterpolator.dom.impl;

import java.net.URL;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RendererForStringInterpolatorCaptureUrlTest {


    @Test
    public void previewCharsToBytes() throws Exception {

        final String urlStr = "http://ams-s-sql08/ReportServer/Pages/ReportViewer.aspx?/Estatio+(test)/Preliminary+Letter&id=58740&rs:Command=Render&rs:Format=PDF&Reference=&invoiceNumber=&atPath=";

        final URL url = new URL(urlStr);

        final String externalForm = url.toExternalForm();

        Assertions.assertThat(externalForm).isEqualTo(urlStr);

    }

}
package org.estatio.capex.dom.payment;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Arrays;

import com.google.common.io.Resources;

import org.apache.pdfbox.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

public class PdfStamperTest {

    @Ignore
    @Test
    public void firstPageOf() throws Exception {
        URL resource = Resources.getResource(PdfStamperTest.class, "sample-invoice.pdf");
        byte[] bytes = Resources.toByteArray(resource);

        byte[] firstPageBytes = new PdfStamper().firstPageOf(bytes,
                Arrays.asList(
                    "approved by: Joe Bloggs",
                    "approved on: 3-May-2017 14:15",
                    "doc barcode: 3013011234"
                ),Arrays.asList(
                    "debtor IBAN: FR12345678900000123",
                    "crdtor IBAN: FR99999912312399800",
                    "gross amt  : 12345.99"
                ),
                "http://www.google.com");

        IOUtils.copy(new ByteArrayInputStream(firstPageBytes), new FileOutputStream("x.pdf"));
    }

}

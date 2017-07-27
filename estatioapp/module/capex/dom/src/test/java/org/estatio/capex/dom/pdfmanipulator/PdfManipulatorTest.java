package org.estatio.capex.dom.pdfmanipulator;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Arrays;

import com.google.common.io.Resources;

import org.apache.pdfbox.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import org.isisaddons.module.pdfbox.dom.service.PdfBoxService;

public class PdfManipulatorTest {

    @Ignore
    @Test
    public void firstPageOf() throws Exception {

        URL resource = Resources.getResource(PdfManipulatorTest.class, "sample-invoice.pdf");
        byte[] bytes = Resources.toByteArray(resource);

        Stamp stamp = new Stamp(Arrays.asList(
                "approved by: Joe Bloggs",
                "approved on: 3-May-2017 14:15",
                "doc barcode: 3013011234"
        ), Arrays.asList(
                "debtor IBAN: FR12345678900000123",
                "crdtor IBAN: FR99999912312399800",
                "gross amt  : 12345.99"
        ), "http://www.google.com");

        final PdfManipulator pdfManipulator = new PdfManipulator();
        pdfManipulator.pdfBoxService = new PdfBoxService();


        //stamp = null;
        byte[] firstPageBytes = pdfManipulator.extractAndStamp(bytes, new ExtractSpec(3,1), stamp);

        IOUtils.copy(new ByteArrayInputStream(firstPageBytes), new FileOutputStream("x.pdf"));
    }

}

package org.estatio.module.capex.app;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class DocumentBarcodeService_overrideUserAtPathUsingDocumentName_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    DocumentBarcodeService documentBarcodeService;

    @Before
    public void setUp() throws Exception {
        documentBarcodeService = new DocumentBarcodeService();
    }
    @Test
    public void when_barcode_trump_atPath() throws Exception {

        // given
        String barcode = "6010012345.pdf";
        String userAtPath = "/FRA";

        // when
        String overriddenAtPath = documentBarcodeService.overrideUserAtPathUsingDocumentName(userAtPath, barcode);

        // then
        Assertions.assertThat(overriddenAtPath).isEqualTo("/BEL");

    }

    @Test
    public void when_barcode_and_atPath_in_alignment() throws Exception {

        // given
        String barcode = "1010012345.pdf";
        String userAtPath = "/FRA";

        // when
        String overriddenAtPath = documentBarcodeService.overrideUserAtPathUsingDocumentName(userAtPath, barcode);

        // then
        Assertions.assertThat(overriddenAtPath).isEqualTo("/FRA");

    }

    @Test
    public void when_not_barcode() throws Exception {

        // given
        String filename = "3625 GIG 679 226 - ASF srl.pdf";
        String userAtPath = "/ITA";

        // when
        String overriddenAtPath = documentBarcodeService.overrideUserAtPathUsingDocumentName(userAtPath, filename);

        // then
        Assertions.assertThat(overriddenAtPath).isEqualTo("/ITA");

    }

}
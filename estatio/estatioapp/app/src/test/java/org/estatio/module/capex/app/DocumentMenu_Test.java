package org.estatio.module.capex.app;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

public class DocumentMenu_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Test
    public void overrideUserAtPathUsingDocumentName_works_when_overriding() throws Exception {

        // given
        DocumentBarcodeService documentBarcodeService = new DocumentBarcodeService();
        String barcode = "6010012345.pdf";
        String userAtPath = "/FRA";

        // when
        String overriddenAtPath = documentBarcodeService.overrideUserAtPathUsingDocumentName(userAtPath, barcode);

        // then
        Assertions.assertThat(overriddenAtPath).isEqualTo("/BEL");

    }

    @Test
    public void overrideUserAtPathUsingDocumentName_works_when_not_overriding() throws Exception {

        // given
        DocumentBarcodeService documentBarcodeService = new DocumentBarcodeService();
        String barcode = "7010012345.pdf";
        String userAtPath = "/FRA";

        // when
        String overriddenAtPath = documentBarcodeService.overrideUserAtPathUsingDocumentName(userAtPath, barcode);

        // then
        Assertions.assertThat(overriddenAtPath).isEqualTo("/FRA");

    }

    @Test
    public void overrideUserAtPathUsingDocumentName_works_when_not_barcode() throws Exception {

        // given
        DocumentBarcodeService documentBarcodeService = new DocumentBarcodeService();
        String filename = "3625 GIG 679 226 - ASF srl.pdf";
        String userAtPath = "/ITA";

        // when
        String overriddenAtPath = documentBarcodeService.overrideUserAtPathUsingDocumentName(userAtPath, filename);

        // then
        Assertions.assertThat(overriddenAtPath).isEqualTo("/ITA");

    }
}
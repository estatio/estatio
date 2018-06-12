package org.estatio.module.capex.app;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DocumentBarcodeService_Test {

    @Test
    public void countryPrefixFromBarcode_works() throws Exception {

        // given
        DocumentBarcodeService service = new DocumentBarcodeService();
        // when
        String barcode = "1234567890";
        // then
        Assertions.assertThat(service.countryPrefixFromBarcode(barcode)).isEqualTo("NL");
        // when
        barcode = "2345678901";
        // then
        Assertions.assertThat(service.countryPrefixFromBarcode(barcode)).isEqualTo("IT");
        // when
        barcode = "3456789012";
        // then
        Assertions.assertThat(service.countryPrefixFromBarcode(barcode)).isEqualTo("FR");
        // when
        barcode = "4567890123";
        // then
        Assertions.assertThat(service.countryPrefixFromBarcode(barcode)).isEqualTo("SE");
        // when
        barcode = "5678901234";
        // then
        Assertions.assertThat(service.countryPrefixFromBarcode(barcode)).isEqualTo("GB");
        // when
        barcode = "6789012345";
        // then
        Assertions.assertThat(service.countryPrefixFromBarcode(barcode)).isEqualTo("BE");
        // when
        barcode = "7890123456";
        // then
        Assertions.assertThat(service.countryPrefixFromBarcode(barcode)).isNull();
        // when
        barcode = "";
        // then
        Assertions.assertThat(service.countryPrefixFromBarcode(barcode)).isNull();
        // when
        barcode = null;
        // then
        Assertions.assertThat(service.countryPrefixFromBarcode(barcode)).isNull();


    }

}
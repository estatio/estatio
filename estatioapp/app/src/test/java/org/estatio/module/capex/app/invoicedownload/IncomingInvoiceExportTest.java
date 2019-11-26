package org.estatio.module.capex.app.invoicedownload;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;

public class IncomingInvoiceExportTest {

    @Test
    public void getCodaElement6FromSellerReference() {

        // given
        String frenchSupplierRef = "FR12345";
        // when then
        Assertions.assertThat(IncomingInvoiceExport.getCodaElement6FromSellerReference(frenchSupplierRef)).isEqualTo("FRFO12345");

        // given
        String belgianSupplierRef = "BE123456";
        // when then
        Assertions.assertThat(IncomingInvoiceExport.getCodaElement6FromSellerReference(belgianSupplierRef)).isEqualTo("BEFO123456");

        // given
        String unknownSupplierRef = "UN123456";
        // when then
        Assertions.assertThat(IncomingInvoiceExport.getCodaElement6FromSellerReference(unknownSupplierRef)).isNull();

    }

    @Test
    public void deriveCodaElement3FromPropertyAndIncomingInvoiceType(){

        // given, when, then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(null, null)).isNull();

        // given
        IncomingInvoiceType typeForCorporate = IncomingInvoiceType.CORPORATE_EXPENSES;
        Property property = new Property();
        // when, then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(null, typeForCorporate)).isEqualTo("FRGGEN0");
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(property, typeForCorporate)).isEqualTo("FRGGEN0");

        // given
        IncomingInvoiceType typeForLocal = IncomingInvoiceType.LOCAL_EXPENSES;
        // when, then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(null, typeForLocal)).isEqualTo("FRGPAR0");
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(property, typeForLocal)).isEqualTo("FRGPAR0");

        // given
        IncomingInvoiceType typeForCapex = IncomingInvoiceType.CAPEX; // or any type not euqal to CORPORATE_EXPENSES or LOCAL_EXPENSES
        // when, then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(null, typeForCapex)).isNull();
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(property, typeForCapex)).isNull(); // since there is no external ref set on property

        // given
        property.setExternalReference("SOME_EXT_REF");
        // when, then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(property, typeForCapex)).isEqualTo("SOME_EXT_REF");

    }
}
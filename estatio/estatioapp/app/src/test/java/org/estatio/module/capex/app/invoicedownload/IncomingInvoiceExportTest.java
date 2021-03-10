package org.estatio.module.capex.app.invoicedownload;

import org.assertj.core.api.Assertions;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.junit.Test;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;

public class IncomingInvoiceExportTest {

    @Test
    public void getCodaElement6FromSellerReference() {

        // given
        String frenchSupplierRef = "FR12345";
        // when then
        Assertions.assertThat(IncomingInvoiceExport.getCodaElement6FromSellerReference(frenchSupplierRef)).isEqualTo("FRFO12345");

        // given
        String belgianSupplierRef = "BEFO123456";
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
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(null, null, null)).isNull();

        // given
        IncomingInvoiceType typeForCorporate = IncomingInvoiceType.CORPORATE_EXPENSES;
        Property property = new Property();
        // when, then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(null, typeForCorporate, null)).isEqualTo("FRGGEN0");
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(property, typeForCorporate, null)).isEqualTo("FRGGEN0");
        // and when (for BEL corporate expenses)
        String atPath = "/BEL/Etc";
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(null, typeForCorporate, atPath)).isEqualTo("BEGGEN0");
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(property, typeForCorporate, atPath)).isEqualTo("BEGGEN0");

        // given
        IncomingInvoiceType typeForLocal = IncomingInvoiceType.LOCAL_EXPENSES;
        // when, then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(null, typeForLocal, null)).isEqualTo("FRGPAR0");
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(property, typeForLocal, null)).isEqualTo("FRGPAR0");

        // given
        IncomingInvoiceType typeForCapex = IncomingInvoiceType.CAPEX; // or any type not euqal to CORPORATE_EXPENSES or LOCAL_EXPENSES
        // when, then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(null, typeForCapex, null)).isNull();
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(property, typeForCapex, null)).isNull(); // since there is no external ref set on property

        // given
        property.setExternalReference("SOME_EXT_REF");
        // when, then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement3FromPropertyAndIncomingInvoiceType(property, typeForCapex, null)).isEqualTo("SOME_EXT_REF");

    }

    @Test
    public void deriveCodaElement1FromBuyer() throws Exception {
        // given
        Party buyer = new Organisation();

        // when 'null' case, should not happen
        // then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement1FromBuyer(null)).isNull();

        // when 'regular' case
        buyer.setReference("SOMETHING");
        // then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement1FromBuyer(buyer)).isEqualTo("SOMETHINGEUR");

        // when exception EUROCOMMERCIAL PROPERTIES BELGIUM S.A. which has BE00 as reference in Estatio data and BE01EUR in Coda
        buyer.setReference("BE00");
        // then
        Assertions.assertThat(IncomingInvoiceExport.deriveCodaElement1FromBuyer(buyer)).isEqualTo("BE01EUR");

    }


}
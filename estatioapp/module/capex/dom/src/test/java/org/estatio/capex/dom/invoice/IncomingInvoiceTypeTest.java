package org.estatio.capex.dom.invoice;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceTypeTest {

    @Test
    public void parse() throws Exception {

        assertThat(IncomingInvoiceType.parse(null)).isEqualTo(IncomingInvoiceType.CAPEX);
        assertThat(IncomingInvoiceType.parse(" PROPERTY_EXPENSES  ")).isEqualTo(IncomingInvoiceType.PROPERTY_EXPENSES);
        assertThat(IncomingInvoiceType.parse("garbage")).isEqualTo(IncomingInvoiceType.CAPEX);

        assertThat(IncomingInvoiceType.parse("CAPEX")).isEqualTo(IncomingInvoiceType.CAPEX);
        assertThat(IncomingInvoiceType.parse("capex")).isEqualTo(IncomingInvoiceType.CAPEX);

        assertThat(IncomingInvoiceType.parse("PROPERTY_EXPENSES")).isEqualTo(IncomingInvoiceType.PROPERTY_EXPENSES);

        assertThat(IncomingInvoiceType.parse("CORPORATE_EXPENSES")).isEqualTo(IncomingInvoiceType.CORPORATE_EXPENSES);
        assertThat(IncomingInvoiceType.parse("corporate_expenses")).isEqualTo(IncomingInvoiceType.CORPORATE_EXPENSES);

        assertThat(IncomingInvoiceType.parse("LEGAL")).isEqualTo(IncomingInvoiceType.LEGAL);
        assertThat(IncomingInvoiceType.parse("legal")).isEqualTo(IncomingInvoiceType.LEGAL);

        assertThat(IncomingInvoiceType.parse("LOCAL_EXPENSES")).isEqualTo(IncomingInvoiceType.LOCAL_EXPENSES);
        assertThat(IncomingInvoiceType.parse("local_expenses")).isEqualTo(IncomingInvoiceType.LOCAL_EXPENSES);

    }

}
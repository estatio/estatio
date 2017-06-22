package org.estatio.capex.dom.invoice;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceTypeTest {

    @Test
    public void parse() throws Exception {

        assertThat(IncomingInvoice.Type.parse(null)).isEqualTo(IncomingInvoice.Type.CAPEX);
        assertThat(IncomingInvoice.Type.parse(" Capex  ")).isEqualTo(IncomingInvoice.Type.CAPEX);
        assertThat(IncomingInvoice.Type.parse("garbage")).isEqualTo(IncomingInvoice.Type.CAPEX);

        assertThat(IncomingInvoice.Type.parse("CAPEX")).isEqualTo(IncomingInvoice.Type.CAPEX);
        assertThat(IncomingInvoice.Type.parse("capex")).isEqualTo(IncomingInvoice.Type.CAPEX);

        assertThat(IncomingInvoice.Type.parse("ASSET")).isEqualTo(IncomingInvoice.Type.ASSET);
        assertThat(IncomingInvoice.Type.parse("asset")).isEqualTo(IncomingInvoice.Type.ASSET);

        assertThat(IncomingInvoice.Type.parse("CORPORATE")).isEqualTo(IncomingInvoice.Type.CORPORATE);
        assertThat(IncomingInvoice.Type.parse("corporate")).isEqualTo(IncomingInvoice.Type.CORPORATE);

        assertThat(IncomingInvoice.Type.parse("LEGAL")).isEqualTo(IncomingInvoice.Type.LEGAL);
        assertThat(IncomingInvoice.Type.parse("legal")).isEqualTo(IncomingInvoice.Type.LEGAL);

        assertThat(IncomingInvoice.Type.parse("LOCAL")).isEqualTo(IncomingInvoice.Type.LOCAL);
        assertThat(IncomingInvoice.Type.parse("local")).isEqualTo(IncomingInvoice.Type.LOCAL);

    }

}
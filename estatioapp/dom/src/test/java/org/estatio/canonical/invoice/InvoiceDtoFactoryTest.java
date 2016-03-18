package org.estatio.canonical.invoice;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.canonical.invoice.v1.InvoiceDto;
import org.estatio.dom.DtoMappingHelper;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItemForTesting;
import org.estatio.dom.tax.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceDtoFactoryTest {

    private Invoice invoice;
    private InvoiceDtoFactory invoiceDtoFactory;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DtoMappingHelper mockMappingHelper;

    @Before
    public void setUp() throws Exception {
        invoiceDtoFactory = new InvoiceDtoFactory();
        invoiceDtoFactory.mappingHelper = mockMappingHelper;

        context.checking(new Expectations() {{
            ignoring(mockMappingHelper);
        }});
    }

    @Test
    public void happy_case() throws Exception {

        // given
        invoice = new Invoice();

        invoice.getItems().add(newItem("1.01", "2.02", "3.03"));
        invoice.getItems().add(newItem("10.10", "20.20", "30.30"));
        invoice.getItems().add(newItem("100.00", "200.00", "300.00"));

        // when
        final InvoiceDto invoiceDto = invoiceDtoFactory.newDto(invoice);

        // then
        assertThat(invoiceDto.getNetAmount()).isEqualTo(new BigDecimal("111.11"));
        assertThat(invoiceDto.getGrossAmount()).isEqualTo(new BigDecimal("222.22"));
        assertThat(invoiceDto.getVatAmount()).isEqualTo(new BigDecimal("333.33"));
    }

    private static InvoiceItemForTesting newItem(final String netAmt, final String grossAmt, final String vatAmt) {
        final InvoiceItemForTesting invoiceItem = new InvoiceItemForTesting();
        final Charge charge = new Charge();
        invoiceItem.setCharge(charge);
        final Tax tax = new Tax();
        invoiceItem.setTax(tax);
        invoiceItem.setNetAmount(new BigDecimal(netAmt));
        invoiceItem.setGrossAmount(new BigDecimal(grossAmt));
        invoiceItem.setVatAmount(new BigDecimal(vatAmt));
        return invoiceItem;
    }

}
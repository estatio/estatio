package org.estatio.module.lease.canonical.v1;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.dto.DtoMappingHelper;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.canonical.invoice.v1.InvoiceDto;
import org.estatio.canonical.invoice.v1.PaymentMethod;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeGroup;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceItemForTesting;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.tax.dom.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceForLeaseDtoFactory_Test {

    private InvoiceForLease invoice;
    private InvoiceForLeaseDtoFactory invoiceForLeaseDtoFactory;
    private InvoiceItemForLeaseDtoFactory invoiceItemForLeaseDtoFactory;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DtoMappingHelper mockMappingHelper;

    @Before
    public void setUp() throws Exception {
        invoiceForLeaseDtoFactory = new InvoiceForLeaseDtoFactory();
        invoiceItemForLeaseDtoFactory = new InvoiceItemForLeaseDtoFactory();

        invoiceForLeaseDtoFactory.invoiceItemForLeaseDtoFactory = invoiceItemForLeaseDtoFactory;
        invoiceForLeaseDtoFactory.mappingHelper = mockMappingHelper;
        invoiceItemForLeaseDtoFactory.mappingHelper = mockMappingHelper;

        context.checking(new Expectations() {{
            ignoring(mockMappingHelper);
        }});
    }

    @Test
    public void happy_case() throws Exception {

        // given
        invoice = new InvoiceForLease();
        invoice.setInvoiceDate(new LocalDate(2016,1,1));
        invoice.setPaymentMethod(org.estatio.module.invoice.dom.PaymentMethod.DIRECT_DEBIT);


        invoice.getItems().add(newItem(invoice, "1.01", "2.02", "3.03"));
        invoice.getItems().add(newItem(invoice, "10.10", "20.20", "30.30"));
        invoice.getItems().add(newItem(invoice, "100.00", "200.00", "300.00"));

        // when
        final InvoiceDto invoiceDto = invoiceForLeaseDtoFactory.newDto(invoice);

        // then
        assertThat(invoiceDto.getNetAmount()).isEqualTo(new BigDecimal("111.11"));
        assertThat(invoiceDto.getGrossAmount()).isEqualTo(new BigDecimal("222.22"));
        assertThat(invoiceDto.getVatAmount()).isEqualTo(new BigDecimal("333.33"));
        assertThat(invoiceDto.getInvoiceDate().toString()).isEqualTo("2016-01-01T00:00:00.000Z");
        assertThat(invoiceDto.getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
    }

    private static InvoiceItemForTesting newItem(final Invoice invoice, final String netAmt, final String grossAmt, final String vatAmt) {
        final InvoiceItemForTesting invoiceItem = new InvoiceItemForTesting(invoice);
        invoiceItem.setInvoice(invoice);
        final ChargeGroup chargeGroup = new ChargeGroup();
        final Charge charge = new Charge();
        charge.setGroup(chargeGroup);
        invoiceItem.setCharge(charge);
        final Tax tax = new Tax();
        invoiceItem.setTax(tax);
        invoiceItem.setNetAmount(new BigDecimal(netAmt));
        invoiceItem.setGrossAmount(new BigDecimal(grossAmt));
        invoiceItem.setVatAmount(new BigDecimal(vatAmt));
        return invoiceItem;
    }

}
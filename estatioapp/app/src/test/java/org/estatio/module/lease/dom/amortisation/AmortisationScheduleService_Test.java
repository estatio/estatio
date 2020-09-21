package org.estatio.module.lease.dom.amortisation;

import java.math.BigInteger;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.Frequency;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseTermForTesting;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;

public class AmortisationScheduleService_Test {

    AmortisationScheduleService service;

    @Test
    public void nextDate_works() {

        // given
        service = new AmortisationScheduleService();

        // when, then
        assertNextDate(new LocalDate(2020,1,1),  Frequency.QUARTERLY, new LocalDate(2020,4,1));
        assertNextDate(new LocalDate(2020,1,3),  Frequency.QUARTERLY, new LocalDate(2020,4,1));
        assertNextDate(new LocalDate(2020,3,31),  Frequency.QUARTERLY, new LocalDate(2020,4,1));
        assertNextDate(new LocalDate(2020,4,1),  Frequency.QUARTERLY, new LocalDate(2020,7,1));
        assertNextDate(new LocalDate(2020,6,30),  Frequency.QUARTERLY, new LocalDate(2020,7,1));
        assertNextDate(new LocalDate(2020,7,1),  Frequency.QUARTERLY, new LocalDate(2020,10,1));
        assertNextDate(new LocalDate(2020,9,30),  Frequency.QUARTERLY, new LocalDate(2020,10,1));
        assertNextDate(new LocalDate(2020,10,1),  Frequency.QUARTERLY, new LocalDate(2021,1,1));
        assertNextDate(new LocalDate(2020,12,31),  Frequency.QUARTERLY, new LocalDate(2021,1,1));

        assertNextDate(new LocalDate(2020,1,1),  Frequency.MONTHLY, new LocalDate(2020,2,1));
        assertNextDate(new LocalDate(2020,1,31),  Frequency.MONTHLY, new LocalDate(2020,2,1));
        assertNextDate(new LocalDate(2020,2,1),  Frequency.MONTHLY, new LocalDate(2020,3,1));
        // etc ...
        assertNextDate(new LocalDate(2020,12,31),  Frequency.MONTHLY, new LocalDate(2021,1,1));

        // default behaviour: returns null for not supported frequencies
        // f.i.
        assertNextDate(new LocalDate(2020,1,1),  Frequency.QUARTERLY_PLUS1M, null);


    }

    private void assertNextDate(final LocalDate date, final Frequency frequency, final LocalDate expectedResult){
        Assertions.assertThat(service.nextDate(date, frequency)).isEqualTo(expectedResult);
    }

    @Test
    public void first_invoice_date_for_leaseItem_works() throws Exception {

        LocalDate firstInvoiceDate;

        // given
        service = new AmortisationScheduleService();
        LeaseItem leaseItem = new LeaseItem();
        // when
        firstInvoiceDate = service.firstInvoiceDateForLeaseItem(leaseItem);
        // then
        Assertions.assertThat(firstInvoiceDate).isNull();

        // and when term added
        LeaseTermForTesting term = new LeaseTermForTesting();
        leaseItem.getTerms().add(term);
        firstInvoiceDate = service.firstInvoiceDateForLeaseItem(leaseItem);
        // then
        Assertions.assertThat(firstInvoiceDate).isNull();

        // and when invoice item added
        InvoiceItemForLease invoiceItemForLease = new InvoiceItemForLease();
        InvoiceForLease invoice = new InvoiceForLease();
        invoice.setStatus(InvoiceStatus.APPROVED);
        invoiceItemForLease.setInvoice(invoice);
        invoiceItemForLease.setDueDate(new LocalDate(2020, 7, 15));
        term.getInvoiceItems().add(invoiceItemForLease);
        firstInvoiceDate = service.firstInvoiceDateForLeaseItem(leaseItem);
        // then
        Assertions.assertThat(firstInvoiceDate).isNull();

        // and when invoice invoiced
        invoice.setStatus(InvoiceStatus.INVOICED);
        final LocalDate invoiceDate = new LocalDate(2020, 7, 15);
        invoice.setInvoiceDate(invoiceDate);
        invoiceItemForLease.setInvoice(invoice);
        term.getInvoiceItems().add(invoiceItemForLease);
        firstInvoiceDate = service.firstInvoiceDateForLeaseItem(leaseItem);
        // then
        Assertions.assertThat(firstInvoiceDate).isEqualTo(invoiceDate);

        // and when multiple approved invoices on term
        final LocalDate invoiceDate2 = new LocalDate(2020, 6, 2);
        InvoiceItemForLease invoiceItemForLease2 = new InvoiceItemForLease();
        InvoiceForLease invoice2 = new InvoiceForLease();
        invoice2.setDueDate(invoiceDate2); // on order to distinguish in sorted set
        invoice2.setStatus(InvoiceStatus.INVOICED);
        invoice2.setInvoiceDate(invoiceDate2);
        invoiceItemForLease2.setInvoice(invoice2);
        term.getInvoiceItems().add(invoiceItemForLease2);
        firstInvoiceDate = service.firstInvoiceDateForLeaseItem(leaseItem);
        // then
        Assertions.assertThat(firstInvoiceDate).isEqualTo(invoiceDate2);

        // when multiple invoiced terms
        LeaseTermForTesting term2 = new LeaseTermForTesting();
        leaseItem.getTerms().add(term2);
        final LocalDate invoiceDate3 = new LocalDate(2020, 5, 15);
        InvoiceItemForLease invoiceItemForLease3 = new InvoiceItemForLease();
        InvoiceForLease invoice3 = new InvoiceForLease();
        invoice3.setStatus(InvoiceStatus.INVOICED);
        invoice3.setInvoiceDate(invoiceDate3);
        invoiceItemForLease3.setInvoice(invoice3);
        term2.getInvoiceItems().add(invoiceItemForLease3);
        term2.setSequence(BigInteger.ONE); // to distinguish in sorted set
        leaseItem.getTerms().add(term2);
        firstInvoiceDate = service.firstInvoiceDateForLeaseItem(leaseItem);
        // then
        Assertions.assertThat(firstInvoiceDate).isEqualTo(invoiceDate3);

    }

}
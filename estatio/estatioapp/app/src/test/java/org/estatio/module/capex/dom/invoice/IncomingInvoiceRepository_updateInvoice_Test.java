package org.estatio.module.capex.dom.invoice;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IncomingInvoiceRepository_updateInvoice_Test {

    private IncomingInvoiceRepository incomingInvoiceRepository;

    @Before
    public void setUp() throws Exception {
        incomingInvoiceRepository = new IncomingInvoiceRepository();
    }

    @Test
    public void paid_date_can_be_set() throws Exception {

        // given
        final IncomingInvoice incomingInvoice = new IncomingInvoice();
        incomingInvoice.setPaidDate(null);

        // when
        final LocalDate newPaidDate = new LocalDate(2016,9,9);
        incomingInvoiceRepository.updateInvoice(incomingInvoice, null, null, null, null, null, null, null, null, null, null, null, null, null, false, newPaidDate);

        // then
        assertThat(incomingInvoice.getPaidDate()).isEqualTo(newPaidDate);
    }

    @Test
    public void paid_date_is_never_overwritten() throws Exception {

        // given
        final IncomingInvoice incomingInvoice = new IncomingInvoice();
        final LocalDate paidDate = new LocalDate(2015, 1, 1);
        incomingInvoice.setPaidDate(paidDate);

        // when
        final LocalDate newPaidDate = new LocalDate(2016,9,9);
        incomingInvoiceRepository.updateInvoice(incomingInvoice, null, null, null, null, null, null, null, null, null, null, null, null, null, false, newPaidDate);

        // then
        assertThat(incomingInvoice.getPaidDate()).isEqualTo(paidDate);
    }

}
package org.estatio.module.lease.dom.amendments;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.invoice.dom.InvoicingInterval;
import org.estatio.module.lease.dom.LeaseTermForTesting;
import org.estatio.module.lease.dom.invoicing.InvoiceCalculationService;

import static org.assertj.core.api.Assertions.assertThat;

public class PersistedCalculationResult_Test {

    @Test
    public void construction_works() throws Exception {

        // given
        final LocalDate invoicingStartDate = new LocalDate(2020, 1, 1);
        final LocalDate invoicingEndDate = new LocalDate(2020, 3, 31);
        final LocalDate invoicingDueDate = new LocalDate(2020,1,2);
        LocalDateInterval intervalForInvoice = LocalDateInterval.including(invoicingStartDate, invoicingEndDate);
        InvoicingInterval invoicingInterval = new InvoicingInterval(intervalForInvoice, invoicingDueDate);

        final LocalDate effectiveStartDate = new LocalDate(2020, 1,3);
        final LocalDate effectiveEndate = new LocalDate(2020,3,30);
        LocalDateInterval effectiveInterval = LocalDateInterval.including(effectiveStartDate, effectiveEndate);

        final BigDecimal value = new BigDecimal("123.45");

        InvoiceCalculationService.CalculationResult calculationResult = new InvoiceCalculationService.CalculationResult(invoicingInterval, effectiveInterval, value);

        // when
        final LeaseTermForTesting leaseTerm = new LeaseTermForTesting();
        PersistedCalculationResult persistedCalculationResult = new PersistedCalculationResult(calculationResult,
                leaseTerm);

        // then
        assertThat(persistedCalculationResult.getLeaseTerm()).isEqualTo(leaseTerm);
        assertThat(persistedCalculationResult.getValue()).isEqualTo(value);
        assertThat(persistedCalculationResult.getInvoicingStartDate()).isEqualTo(invoicingStartDate);
        assertThat(persistedCalculationResult.getInvoicingEndDate()).isEqualTo(invoicingEndDate);
        assertThat(persistedCalculationResult.getInvoicingDueDate()).isEqualTo(invoicingDueDate);
        assertThat(persistedCalculationResult.getEffectiveStartDate()).isEqualTo(effectiveStartDate);
        assertThat(persistedCalculationResult.getEffectiveEndDate()).isEqualTo(effectiveEndate);
    }

}
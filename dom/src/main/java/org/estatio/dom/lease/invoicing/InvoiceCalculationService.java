/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.lease.invoicing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.InvoicingInterval;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermValueType;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.settings.EstatioSettingsService;

@Hidden
public class InvoiceCalculationService {

    /**
     * class to store the result a calculation
     * 
     */
    public static class CalculationResult {
        private static final BigDecimal ZERO = new BigDecimal("0.00");
        private BigDecimal value;
        private BigDecimal valueOnDueDate;
        private BigDecimal mockValue;;

        private InvoicingInterval invoicingInterval;
        private LocalDateInterval effectiveInterval;

        public CalculationResult() {
            this(null);
        }

        public CalculationResult(final InvoicingInterval interval) {
            this(interval, null, ZERO, ZERO, ZERO);
        }

        public CalculationResult(
                final InvoicingInterval interval,
                final LocalDateInterval effectiveInterval,
                final BigDecimal value,
                final BigDecimal valueOnDueDate,
                final BigDecimal mockValue) {
            this.invoicingInterval = interval;
            this.effectiveInterval = effectiveInterval;
            this.value = value;
            this.valueOnDueDate = valueOnDueDate;
            this.mockValue = mockValue;
        }

        public BigDecimal value() {
            return value;
        }

        public BigDecimal valueOnDueDate() {
            return valueOnDueDate;
        }

        public BigDecimal mockValue() {
            return mockValue;
        }

        public InvoicingInterval invoicingInterval() {
            return invoicingInterval;
        }

        public LocalDateInterval effectiveInterval() {
            return effectiveInterval;
        }

        @Override
        public String toString() {
            return invoicingInterval.toString().concat(" : ").concat(value.toString());
        }
    }

    /**
     * Utility class for collection of calculation results
     */
    public static class CalculationResultsUtil {
        public static BigDecimal sum(final List<CalculationResult> list) {
            BigDecimal sum = BigDecimal.ZERO;
            if (list == null) {
                return sum;
            }
            for (CalculationResult result : list) {
                sum = sum.add(result.value());
            }
            return sum;
        }
    }

    /**
     * Calculates term and creates invoice
     * 
     * @param leaseTerm
     * @param startDueDate
     * @param nextDueDate
     * @param invoiceDueDate
     * @param invoicingFrequency
     * @param runType
     */
    @NotContributed
    public void calculateAndInvoice(
            final LeaseTerm leaseTerm,
            final LocalDate startDueDate,
            final LocalDate nextDueDate,
            final LocalDate invoiceDueDate,
            final InvoicingFrequency invoicingFrequency,
            final InvoiceRunType runType) {
        final List<CalculationResult> results;
        LocalDate termStartDate = leaseTerm.getStartDate();
        LocalDate start = startDueDate;
        // Use the start date of the term when start due date is before or retro
        // run
        if (runType.equals(InvoiceRunType.RETRO_RUN) || startDueDate.compareTo(termStartDate) < 0) {
            start = termStartDate;
        }
        results = calculateDueDateRange(leaseTerm, start, nextDueDate, invoicingFrequency);
        createInvoiceItems(leaseTerm, invoiceDueDate, results, invoicingFrequency);
    }

    /**
     * Calculates a term with a given invoicing frequency
     */
    public List<CalculationResult> calculateDueDateRange(
            final LeaseTerm leaseTerm,
            final LocalDate startDueDate,
            final LocalDate nextDueDate,
            final InvoicingFrequency invoicingFrequency) {
        final List<CalculationResult> results = Lists.newArrayList();
        final List<InvoicingInterval> intervals = invoicingFrequency.intervalsInDueDateRange(startDueDate, nextDueDate);
        final LocalDateInterval termInterval = leaseTerm.getEffectiveInterval();
        final LocalDate epochDate =
                ObjectUtils.firstNonNull(leaseTerm.getLeaseItem().getEpochDate(),
                        estatioSettingsService.fetchEpochDate());
        for (final InvoicingInterval invoicingInterval : intervals) {
            final LocalDateInterval overlap = invoicingInterval.overlap(termInterval);
            if (overlap == null) {
                results.add(new CalculationResult(invoicingInterval));
            } else {
                final BigDecimal overlapDays = new BigDecimal(overlap.days());
                final BigDecimal frequencyDays = new BigDecimal(invoicingInterval.days());
                final BigDecimal rangeFactor =
                        leaseTerm.valueType().equals(LeaseTermValueType.FIXED) ?
                                BigDecimal.ONE :
                                overlapDays.divide(frequencyDays, MathContext.DECIMAL64);
                final BigDecimal annualFactor = invoicingFrequency.annualMultiplier();
                results.add(new CalculationResult(
                        invoicingInterval,
                        overlap,
                        calculateValue(rangeFactor, annualFactor, leaseTerm.valueForDate(nextDueDate.minusDays(1))),
                        calculateValue(rangeFactor, annualFactor, leaseTerm.valueForDate(invoicingInterval.dueDate())),
                        calculateValue(rangeFactor, annualFactor, epochDate == null || invoicingInterval.dueDate().compareTo(epochDate) >= 0 ? null : leaseTerm.valueForDate(epochDate.minusDays(1)))));
            }
        }
        return results;
    }

    /**
     * Multiplies a value with the range and annual factors
     * 
     * @param rangeFactor
     * @param annualFactor
     * @param value
     * @return
     */
    private BigDecimal calculateValue(
            final BigDecimal rangeFactor,
            final BigDecimal annualFactor,
            final BigDecimal value) {
        if (value != null && annualFactor != null && rangeFactor != null) {
            return value.multiply(annualFactor)
                    .multiply(rangeFactor)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal("0.00");
    }

    // //////////////////////////////////////
    // Creation of invoice items

    /**
     * Creates invoice items for a list of calculation results
     * 
     * @param leaseTerm
     * @param dueDate
     * @param results
     */
    void createInvoiceItems(
            final LeaseTerm leaseTerm,
            final LocalDate dueDate,
            final List<CalculationResult> results) {
        createInvoiceItems(leaseTerm, dueDate, results, leaseTerm.getLeaseItem().getInvoicingFrequency());
    }

    /**
     * Calculates an invoice item with the difference between the already
     * invoiced and calculated value.
     * 
     * @param leaseTerm
     * @param dueDate
     * @param calculationResult
     * @param invoicingFrequency
     */
    void createInvoiceItems(
            final LeaseTerm leaseTerm,
            final LocalDate dueDate,
            final List<CalculationResult> results,
            final InvoicingFrequency invoicingFrequency) {

        for (CalculationResult result : results) {
            BigDecimal invoicedValue = invoiceItemsForLease.invoicedValueFor(leaseTerm, result.invoicingInterval());
            BigDecimal newValue = result.value().subtract(invoicedValue).subtract(result.mockValue());
            if (newValue.compareTo(BigDecimal.ZERO) != 0) {
                createInvoiceItem(leaseTerm, dueDate, result, newValue);
            }
        }
    }

    /**
     * Creates an invoice item
     * 
     * @param leaseTerm
     * @param invoiceDueDate
     * @param calculationResult
     *            the result of the calculation of the lease term
     * @param overrideValue
     *            the value that overrides the calculation result usually based
     *            on the already invoiced amounts
     */
    private void createInvoiceItem(
            final LeaseTerm leaseTerm,
            final LocalDate invoiceDueDate,
            final CalculationResult calculationResult,
            final BigDecimal overrideValue) {

        InvoiceItemForLease invoiceItem =
                invoiceItemsForLease.findOrCreateUnapprovedInvoiceItemFor(
                        leaseTerm, calculationResult.invoicingInterval(), invoiceDueDate);
        invoiceItem.setNetAmount(overrideValue);
        invoiceItem.setQuantity(BigDecimal.ONE);
        LeaseItem leaseItem = leaseTerm.getLeaseItem();
        Charge charge = leaseItem.getCharge();
        invoiceItem.setCharge(charge);
        invoiceItem.setDescription(charge.getDescription());
        invoiceItem.setDueDate(invoiceDueDate);
        invoiceItem.setStartDate(calculationResult.invoicingInterval().startDate());
        invoiceItem.setEndDate(calculationResult.invoicingInterval().endDate());
        invoiceItem.setEffectiveStartDate(calculationResult.effectiveInterval().startDate());
        invoiceItem.setEffectiveEndDate(calculationResult.effectiveInterval().endDate());
        Tax tax = charge.getTax();
        invoiceItem.setTax(tax);
        invoiceItem.verify();

    }

    // //////////////////////////////////////

    private EstatioSettingsService estatioSettingsService;

    public void setEstatioSettings(final EstatioSettingsService estatioSettings) {
        this.estatioSettingsService = estatioSettings;
    }

    private Invoices invoices;

    public void injctInvoices(final Invoices invoices) {
        this.invoices = invoices;
    }

    private InvoiceItemsForLease invoiceItemsForLease;

    public void injectInvoiceItemsForLease(final InvoiceItemsForLease invoiceItemsForLease) {
        this.invoiceItemsForLease = invoiceItemsForLease;
    }

}

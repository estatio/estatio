/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.Leases.InvoiceRunType;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.utils.CalendarUtils;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.settings.EstatioSettingsService;

@Hidden
public class InvoiceCalculationService {

    /**
     * class to store the result a calculation
     *
     */
    static class CalculationResult {
        BigDecimal value;
        LocalDateInterval frequencyInterval;

        public CalculationResult() {
            value = BigDecimal.ZERO.setScale(2);
        }

        public CalculationResult(final LocalDateInterval interval, final BigDecimal value) {
            this.value = value;
            this.frequencyInterval = interval;
        }

        public BigDecimal getCalculatedValue() {
            return value;
        }

        public LocalDateInterval getFrequencyInterval() {
            return frequencyInterval;
        }
    }

    /**
     * Calculates term and creates invoice
     * 
     * @param leaseTerm
     * @param periodStartDate
     * @param dueDate
     * @param invoicingFrequency
     * @param runType
     */
    @NotContributed
    public void calculateAndInvoice(
            final LeaseTerm leaseTerm,
            final LocalDate periodStartDate,
            final LocalDate dueDate,
            final InvoicingFrequency invoicingFrequency,
            final InvoiceRunType runType) {
        if (runType.equals(InvoiceRunType.RETRO_RUN)) {
            final List<CalculationResult> results = calculateFullLengthOfTerm(leaseTerm, dueDate);
            createInvoiceItems(leaseTerm, dueDate, results);
        } else {
            final CalculationResult result = 
                    calculateForInvoiceFrequency(leaseTerm, periodStartDate, dueDate, invoicingFrequency);
            createAdjustedInvoiceItem(leaseTerm, dueDate, result, invoicingFrequency);
        }
    }

    /**
     * Calculates the total value for a term
     * 
     * @param leaseTerm
     * @param periodStartDate
     * @param dueDate
     * @param invoicingFrequency
     * @return
     */
    @NotContributed
    public BigDecimal calculateSumForAllPeriods(
            final LeaseTerm leaseTerm,
            final LocalDate periodStartDate,
            final LocalDate dueDate,
            final InvoicingFrequency invoicingFrequency) {
        BigDecimal value = BigDecimal.ZERO;
        final List<CalculationResult> results = 
                calculateAllPeriodsWithGivenInvoicingFrequency(
                        leaseTerm, periodStartDate, dueDate, invoicingFrequency);
        for (CalculationResult result : results) {
            value = value.add(result.getCalculatedValue());
        }
        return value;
    }

    /**
     * Calculates a term using the invoicing frequency of the parent lease item
     * 
     * @param leaseTerm
     * @param periodStartDate
     * @param dueDate
     * @return
     */
    CalculationResult calculateLeaseTerm(
            final LeaseTerm leaseTerm,
            final LocalDate periodStartDate,
            final LocalDate dueDate) {
        return calculateForInvoiceFrequency(
                leaseTerm, periodStartDate, dueDate, leaseTerm.getLeaseItem().getInvoicingFrequency());
    }

    /**
     * Calculates a term with a different invoicing frequency
     * 
     * @param leaseTerm
     * @param periodStartDate
     * @param dueDate
     * @param invoicingFrequency
     * @return
     */
    CalculationResult calculateForInvoiceFrequency(
            final LeaseTerm leaseTerm,
            final LocalDate periodStartDate,
            final LocalDate dueDate,
            final InvoicingFrequency invoicingFrequency) {
        final LocalDateInterval frequencyInterval = 
                new LocalDateInterval(CalendarUtils.intervalMatching(periodStartDate, invoicingFrequency.getRrule()));
        if (frequencyInterval.startDate() != null) {
            final LocalDateInterval termInterval = leaseTerm.getEffectiveInterval();
            final LocalDateInterval overlap = frequencyInterval.overlap(termInterval);
            if (overlap != null) {
                final BigDecimal overlapDays = new BigDecimal(overlap.days());
                final BigDecimal frequencyDays = new BigDecimal(frequencyInterval.days());
                final BigDecimal rangeFactor = overlapDays.divide(frequencyDays, MathContext.DECIMAL64);
                final BigDecimal freqFactor = 
                        invoicingFrequency.getNumerator().divide(
                                invoicingFrequency.getDenominator(), MathContext.DECIMAL64);
                final BigDecimal currentValue = leaseTerm.valueForDueDate(dueDate);
                if (currentValue != null && freqFactor != null && rangeFactor != null) {
                    final BigDecimal value =
                            currentValue.multiply(freqFactor)
                                    .multiply(rangeFactor)
                                    .setScale(2, RoundingMode.HALF_UP);
                    return new CalculationResult(frequencyInterval, value);
                }
            }
            return new CalculationResult(frequencyInterval, BigDecimal.ZERO.setScale(2));
        }
        return null;
    }

    /**
     * calculates a term with an invoicing frequency that can be different then
     * the invoicing frequency of the lease item of the term
     * 
     * @param leaseTerm
     * @param periodStartDate
     * @param dueDate
     * @param invoicingFrequency
     * @return a list of calculation results
     */
    List<CalculationResult> calculateAllPeriodsWithGivenInvoicingFrequency(
            final LeaseTerm leaseTerm,
            final LocalDate periodStartDate,
            final LocalDate dueDate,
            final InvoicingFrequency invoicingFrequency) {
        List<CalculationResult> results = new ArrayList<CalculationResult>();
        LocalDate intervalStartDate = periodStartDate;
        LocalDateInterval invoicingFrequencyInterval = new LocalDateInterval(
                CalendarUtils.intervalMatching(intervalStartDate, invoicingFrequency.getRrule()));
        do {
            final CalculationResult result = 
                    calculateForInvoiceFrequency(leaseTerm, intervalStartDate, dueDate, 
                            leaseTerm.getLeaseItem().getInvoicingFrequency());
            results.add(result);
            intervalStartDate = result.getFrequencyInterval().endDateExcluding();
        } while (invoicingFrequencyInterval.endDateExcluding() != null &&
                invoicingFrequencyInterval.contains(intervalStartDate));
        return results;
    }

    /**
     * Calculates all results for the total length of the term
     * 
     * @param leaseTerm
     * @param dueDate
     * @return
     */
    List<CalculationResult> calculateFullLengthOfTerm(final LeaseTerm leaseTerm, final LocalDate dueDate) {
        List<CalculationResult> results = new ArrayList<CalculationResult>();
        CalculationResult result;
        LocalDateInterval frequencyInterval = new LocalDateInterval(CalendarUtils.intervalContaining(
                leaseTerm.getStartDate(),
                leaseTerm.getLeaseItem().getInvoicingFrequency().getRrule()));
        if (frequencyInterval != null) {
            LocalDate intervalStartDate = frequencyInterval.startDate();
            do {
                result = calculateLeaseTerm(leaseTerm, intervalStartDate, dueDate);
                results.add(result);
                intervalStartDate = result.getFrequencyInterval().endDateExcluding();
            } while (intervalStartDate.compareTo(dueDate) < 0);

        }
        return results;
    }

    /**
     * Creates invoice items for a list of calculation results
     * 
     * @param leaseTerm
     * @param dueDate
     * @param results
     */
    void createInvoiceItems(
            final LeaseTerm leaseTerm, final LocalDate dueDate,
            final List<CalculationResult> results) {
        for (CalculationResult result : results) {
            createAdjustedInvoiceItem(leaseTerm, dueDate, result, leaseTerm.getLeaseItem().getInvoicingFrequency());
        }
    }

    /**
     * Calculates an invoice item
     * 
     * @param leaseTerm
     * @param dueDate
     * @param calculationResult
     * @param invoicingFrequency
     */
    void createAdjustedInvoiceItem(
            final LeaseTerm leaseTerm,
            final LocalDate dueDate,
            final CalculationResult calculationResult,
            final InvoicingFrequency invoicingFrequency) {
        if (calculationResult == null) {
            return;
        }
        final LocalDate epochDate = estatioSettingsService.fetchEpochDate();

        final LocalDate startDate = calculationResult.frequencyInterval.startDate();
        BigDecimal invoicedValue;
        if (epochDate != null && startDate.compareTo(epochDate) < 0) {
            CalculationResult mockResult = calculateForInvoiceFrequency(
                    leaseTerm,
                    startDate,
                    startDate,
                    invoicingFrequency);
            invoicedValue = mockResult.getCalculatedValue();
        } else {
            invoicedValue = leaseTerm.invoicedValueFor(startDate);
        }
        BigDecimal newValue = calculationResult.value.subtract(invoicedValue);
        if (newValue.compareTo(BigDecimal.ZERO) != 0) {
            createInvoiceItem(leaseTerm, dueDate, calculationResult, newValue);
        }
    }

    /**
     * Creates an invoice item
     * 
     * @param leaseTerm
     * @param dueDate
     * @param calculationResult
     *            the result of the calculation of the lease term
     * @param overrideValue
     *            the value that overrides the calculation result usually based
     *            on the already invoiced amounts
     */
    private void createInvoiceItem(
            final LeaseTerm leaseTerm,
            final LocalDate dueDate,
            final CalculationResult calculationResult,
            final BigDecimal overrideValue) {
        InvoiceItemForLease invoiceItem =
                leaseTerm.findOrCreateUnapprovedInvoiceItemFor(
                        calculationResult.frequencyInterval.startDate(), dueDate);
        invoiceItem.setNetAmount(overrideValue);
        invoiceItem.setQuantity(BigDecimal.ONE);
        LeaseItem leaseItem = leaseTerm.getLeaseItem();
        Charge charge = leaseItem.getCharge();
        invoiceItem.setCharge(charge);
        invoiceItem.setDescription(charge.getName());
        invoiceItem.setDueDate(dueDate);
        invoiceItem.setStartDate(calculationResult.frequencyInterval.startDate());
        invoiceItem.setEndDate(calculationResult.frequencyInterval.endDate());
        Tax tax = charge.getTax();
        invoiceItem.setTax(tax);
        invoiceItem.attachToInvoice();
        invoiceItem.verify();
    }

    // //////////////////////////////////////

    private EstatioSettingsService estatioSettingsService;

    public void setEstatioSettings(final EstatioSettingsService estatioSettings) {
        this.estatioSettingsService = estatioSettings;
    }

}

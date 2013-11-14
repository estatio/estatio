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

import org.joda.time.Interval;
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

    static class CalculationResultsUtil {
        public static BigDecimal sum(List<CalculationResult> list) {
            BigDecimal sum = BigDecimal.ZERO;
            if (list == null || list.size() < 1) {
                return sum;
            }
            for (CalculationResult result : list) {
                sum = sum.add(result.getCalculatedValue());
            }
            return sum;
        }
    }

    /**
     * Calculates term and creates invoice
     * 
     * @param leaseTerm
     * @param periodStartDate
     * @param periodEndDate
     * @param dueDate
     * @param invoicingFrequency
     * @param runType
     */
    @NotContributed
    public void calculateAndInvoice(
            final LeaseTerm leaseTerm,
            final LocalDate periodStartDate,
            final LocalDate periodEndDate,
            final LocalDate dueDate,
            final InvoicingFrequency invoicingFrequency, 
            final InvoiceRunType runType) {
        final List<CalculationResult> results;
        LocalDate start = periodStartDate;
        LocalDate end = periodEndDate;
        if (runType.equals(InvoiceRunType.RETRO_RUN)) {
            start = leaseTerm.getStartDate();
            if (leaseTerm.getEndDate() == null || leaseTerm.getEndDate().isAfter(dueDate)) {
                end = dueDate;
            }
        }
        results = calculateWithFrequency(leaseTerm, start, end, dueDate, invoicingFrequency);
        createAdjustedInvoiceItem(leaseTerm, dueDate, results, invoicingFrequency);
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
        final List<CalculationResult> results =
                calculateAllPeriodsWithGivenInvoicingFrequency(
                        leaseTerm, periodStartDate, dueDate, invoicingFrequency);
        return CalculationResultsUtil.sum(results);
    }

    /**
     * Calculates a term using the invoicing frequency of the parent lease item
     * 
     * @param leaseTerm
     * @param periodStartDate
     * @param dueDate
     * @return
     */
    @Deprecated
    CalculationResult calculateLeaseTerm(
            final LeaseTerm leaseTerm,
            final LocalDate periodStartDate,
            final LocalDate dueDate) {
        return calculateWithFrequency(
                leaseTerm,
                periodStartDate,
                null,
                dueDate, leaseTerm.getLeaseItem().getInvoicingFrequency()).get(0);
    }

    /**
     * Calculates a term with a given invoicing frequency
     * 
     * @param leaseTerm
     * @param periodStartDate
     * @param periodEndDate
     *            TODO
     * @param valueDate
     * @param invoicingFrequency
     * @return
     */
    List<CalculationResult> calculateWithFrequency(
            final LeaseTerm leaseTerm,
            final LocalDate periodStartDate,
            final LocalDate periodEndDate,
            final LocalDate valueDate, final InvoicingFrequency invoicingFrequency) {
        String rrule = invoicingFrequency.getRrule();

        List<CalculationResult> results = new ArrayList<CalculationResult>();

        List<Interval> intervals = CalendarUtils.intervalsInRange(periodStartDate, periodEndDate, rrule);

        for (Interval interval : intervals)
        {
            final LocalDateInterval termInterval = leaseTerm.getEffectiveInterval();
            final LocalDateInterval calculationInterval = new LocalDateInterval(interval);
            final LocalDateInterval overlap = calculationInterval.overlap(termInterval);
            if (overlap != null) {
                final BigDecimal overlapDays = new BigDecimal(overlap.days());
                final BigDecimal frequencyDays = new BigDecimal(calculationInterval.days());
                final BigDecimal rangeFactor = overlapDays.divide(frequencyDays, MathContext.DECIMAL64);
                final BigDecimal annualFactor = invoicingFrequency.annualMultiplier();
                final BigDecimal currentValue = leaseTerm.valueForDate(valueDate);
                if (currentValue != null && annualFactor != null && rangeFactor != null) {
                    final BigDecimal value =
                            currentValue.multiply(annualFactor)
                                    .multiply(rangeFactor)
                                    .setScale(2, RoundingMode.HALF_UP);
                    results.add(new CalculationResult(calculationInterval, value));
                }
            }
        }
        return results;
    }

    @NotContributed
    public List<CalculationResult> calculateFullLengthOfTerm(LeaseTerm leaseTerm, LocalDate dueDate) {
        return calculateWithFrequency(leaseTerm, leaseTerm.getStartDate(), leaseTerm.getEndDate(), dueDate, leaseTerm.getLeaseItem().getInvoicingFrequency());
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
        LocalDateInterval interval = new LocalDateInterval(
                CalendarUtils.intervalMatching(periodStartDate, invoicingFrequency.getRrule()));
        final List<CalculationResult> results =
                calculateWithFrequency(
                        leaseTerm,
                        interval.startDate(),
                        interval.endDate(),
                        dueDate, leaseTerm.getLeaseItem().getInvoicingFrequency());
        return results;
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
        createAdjustedInvoiceItem(leaseTerm, dueDate, results, leaseTerm.getLeaseItem().getInvoicingFrequency());
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
    void createAdjustedInvoiceItem(
            final LeaseTerm leaseTerm,
            final LocalDate dueDate,
            final List<CalculationResult> results,
            final InvoicingFrequency invoicingFrequency) {
        final LocalDate epochDate = estatioSettingsService.fetchEpochDate();

        for (CalculationResult result : results) {
            final LocalDate startDate = result.frequencyInterval.startDate();
            BigDecimal invoicedValue;
            if (epochDate != null && startDate.compareTo(epochDate) < 0) {
                List<CalculationResult> mockResult = calculateWithFrequency(
                        leaseTerm,
                        startDate,
                        null,
                        startDate,
                        invoicingFrequency);
                invoicedValue = CalculationResultsUtil.sum(mockResult);
            } else {
                invoicedValue = leaseTerm.invoicedValueFor(startDate);
            }
            BigDecimal newValue = result.value.subtract(invoicedValue);
            if (newValue.compareTo(BigDecimal.ZERO) != 0) {
                createInvoiceItem(leaseTerm, dueDate, result, newValue);
            }
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

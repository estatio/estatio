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

    static class CalculationResult {
        BigDecimal value;
        LocalDateInterval frequencyInterval;

        public CalculationResult() {
            value = BigDecimal.ZERO.setScale(2);
        }

        public CalculationResult(LocalDateInterval interval, BigDecimal value) {
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

    // //////////////////////////////////////

    // TODO: [JWA] Should injected services by default be not contributed?
    @NotContributed
    public void calculateAndInvoice(LeaseTerm leaseTerm, LocalDate periodStartDate, LocalDate dueDate, InvoicingFrequency invoicingFrequency, InvoiceRunType runType) {
        if (runType.equals(InvoiceRunType.RETRO_RUN)) {
            List<CalculationResult> results = fullCalculationResults(leaseTerm, dueDate);
            createInvoiceItems(leaseTerm, dueDate, results);
        } else {
            CalculationResult result = calculate(leaseTerm, periodStartDate, dueDate, invoicingFrequency);
            createInvoiceItem(leaseTerm, dueDate, result, invoicingFrequency);
        }
    }

    // //////////////////////////////////////

    @NotContributed
    public BigDecimal calculatedValue(LeaseTerm leaseTerm, LocalDate periodStartDate, LocalDate dueDate, InvoicingFrequency invoicingFrequency) {
        BigDecimal value = BigDecimal.ZERO;
        List<CalculationResult> results = calculationResults(leaseTerm, periodStartDate, dueDate, invoicingFrequency);
        for (CalculationResult result : results) {
            value = value.add(result.getCalculatedValue());
        }
        return value;
    }

    // //////////////////////////////////////

    CalculationResult calculate(LeaseTerm leaseTerm, LocalDate periodStartDate, LocalDate dueDate) {
        return calculate(leaseTerm, periodStartDate, dueDate, leaseTerm.getLeaseItem().getInvoicingFrequency());
    }

    CalculationResult calculate(LeaseTerm leaseTerm, LocalDate periodStartDate, LocalDate dueDate, InvoicingFrequency freq) {
        LocalDateInterval frequencyInterval = new LocalDateInterval(CalendarUtils.intervalMatching(periodStartDate, freq.getRrule()));
        if (frequencyInterval.startDate() != null) {
            LocalDateInterval termInterval = leaseTerm.getEffectiveInterval();
            LocalDateInterval overlap = frequencyInterval.overlap(termInterval);
            if (overlap != null) {
                BigDecimal overlapDays = new BigDecimal(overlap.days());
                BigDecimal frequencyDays = new BigDecimal(frequencyInterval.days());
                BigDecimal rangeFactor = overlapDays.divide(frequencyDays, MathContext.DECIMAL64);
                BigDecimal freqFactor = freq.getNumerator().divide(freq.getDenominator(), MathContext.DECIMAL64);
                BigDecimal currentValue = leaseTerm.valueForDueDate(dueDate);
                if (currentValue != null && freqFactor != null && rangeFactor != null) {
                    BigDecimal value = currentValue.multiply(freqFactor).multiply(rangeFactor).setScale(2, RoundingMode.HALF_UP);
                    return new CalculationResult(frequencyInterval, value);
                }
            }
            return new CalculationResult(frequencyInterval, BigDecimal.ZERO.setScale(2));
        }
        return null;
    }

    // //////////////////////////////////////

    List<CalculationResult> calculationResults(LeaseTerm leaseTerm, LocalDate periodStartDate, LocalDate dueDate, InvoicingFrequency invoicingFrequency) {
        List<CalculationResult> results = new ArrayList<CalculationResult>();
        LocalDate intervalStartDate = periodStartDate;
        LocalDateInterval frequencyInterval = new LocalDateInterval(CalendarUtils.intervalMatching(intervalStartDate, invoicingFrequency.getRrule()));
        CalculationResult result;
        do {
            result = calculate(leaseTerm, intervalStartDate, dueDate, leaseTerm.getLeaseItem().getInvoicingFrequency());
            results.add(result);
            intervalStartDate = result.getFrequencyInterval().endDateExcluding();
        } while (result.getFrequencyInterval().endDateExcluding().isBefore(frequencyInterval.endDateExcluding()));

        return results;
    }

    // //////////////////////////////////////

    List<CalculationResult> fullCalculationResults(LeaseTerm leaseTerm, LocalDate dueDate) {
        List<CalculationResult> results = new ArrayList<CalculationResult>();
        CalculationResult result;
        LocalDateInterval frequencyInterval = new LocalDateInterval(CalendarUtils.intervalContaining(leaseTerm.getStartDate(), leaseTerm.getLeaseItem().getInvoicingFrequency().getRrule()));
        if (frequencyInterval != null) {
            LocalDate intervalStartDate = frequencyInterval.startDate();
            do {
                result = calculate(leaseTerm, intervalStartDate, dueDate);
                results.add(result);
                intervalStartDate = result.getFrequencyInterval().endDateExcluding();
            } while (intervalStartDate.compareTo(dueDate) < 0);

        }
        return results;
    }

    // //////////////////////////////////////

    void createInvoiceItems(LeaseTerm leaseTerm, LocalDate dueDate, List<CalculationResult> results) {
        for (CalculationResult result : results) {
            createInvoiceItem(leaseTerm, dueDate, result, leaseTerm.getLeaseItem().getInvoicingFrequency());
        }
    }

    void createInvoiceItem(LeaseTerm leaseTerm, LocalDate dueDate, CalculationResult calculationResult, InvoicingFrequency invoicingFrequency) {
        if (calculationResult != null) {
            BigDecimal invoicedValue;
            LocalDate epochDate = estatioSettingsService.fetchEpochDate();

            if (epochDate != null && calculationResult.frequencyInterval.startDate().compareTo(epochDate) < 0) {
                CalculationResult mockResult = calculate(leaseTerm, calculationResult.frequencyInterval.startDate(), calculationResult.frequencyInterval.startDate(), invoicingFrequency);
                invoicedValue = mockResult.getCalculatedValue();
            } else {
                invoicedValue = leaseTerm.invoicedValueFor(calculationResult.frequencyInterval.startDate());
            }
            BigDecimal newValue = calculationResult.value.subtract(invoicedValue);
            if (newValue.compareTo(BigDecimal.ZERO) != 0) {
                InvoiceItemForLease invoiceItem = leaseTerm.findOrCreateUnapprovedInvoiceItemFor(calculationResult.frequencyInterval.startDate(), dueDate);
                invoiceItem.setNetAmount(newValue);
                invoiceItem.setQuantity(BigDecimal.ONE);
                LeaseItem leaseItem = leaseTerm.getLeaseItem();
                Charge charge = leaseItem.getCharge();
                invoiceItem.setCharge(charge);
                invoiceItem.setDescription(charge.getDescription());
                invoiceItem.setDueDate(dueDate);
                invoiceItem.setStartDate(calculationResult.frequencyInterval.startDate());
                invoiceItem.setEndDate(calculationResult.frequencyInterval.endDateExcluding());
                Tax tax = charge.getTax();
                invoiceItem.setTax(tax);
                invoiceItem.attachToInvoice();
                invoiceItem.verify();
            }
        }
    }

    // //////////////////////////////////////

    private EstatioSettingsService estatioSettingsService;

    public void setEstatioSettings(final EstatioSettingsService estatioSettings) {
        this.estatioSettingsService = estatioSettings;
    }

}

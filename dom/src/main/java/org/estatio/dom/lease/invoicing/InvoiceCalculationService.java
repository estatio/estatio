package org.estatio.dom.lease.invoicing;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.utils.CalendarUtils;
import org.estatio.dom.utils.LocalDateInterval;
import org.estatio.dom.utils.LocalDateInterval.IntervalEnding;
import org.estatio.services.appsettings.EstatioSettingsService;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;

@Hidden
public class InvoiceCalculationService {

    static class CalculationResult {
        BigDecimal value = BigDecimal.ZERO.setScale(2);
        LocalDateInterval frequencyInterval;

        public BigDecimal getCalculatedValue() {
            return value;
        }

        public LocalDateInterval getFrequencyInterval() {
            return frequencyInterval;
        }
    }

    public void calculateAndInvoiceItems(LeaseTerm leaseTerm, LocalDate startDate, LocalDate dueDate, InvoicingFrequency freq) {
        CalculationResult result = calculate(leaseTerm, startDate, dueDate, freq);
        createInvoiceItems(leaseTerm, startDate, dueDate, result, freq);
    }

    CalculationResult calculate(LeaseTerm leaseTerm, LocalDate periodStartDate, LocalDate dueDate) {
        return calculate(leaseTerm, periodStartDate, dueDate, leaseTerm.getLeaseItem().getInvoicingFrequency());
    }

    private CalculationResult calculate(LeaseTerm leaseTerm, LocalDate periodStartDate, LocalDate dueDate, InvoicingFrequency freq) {
        CalculationResult result = new CalculationResult();
        result.frequencyInterval = new LocalDateInterval(CalendarUtils.intervalMatching(periodStartDate, freq.rrule));
        if (result.frequencyInterval.getStartDate() != null) {
            LocalDateInterval termInterval = new LocalDateInterval(leaseTerm.getStartDate(), leaseTerm.getEndDate(), IntervalEnding.INCLUDING_END_DATE);
            LocalDateInterval overlap = result.frequencyInterval.overlap(termInterval);
            if (overlap != null) {
                BigDecimal overlapDays = new BigDecimal(overlap.getDays());
                BigDecimal frequencyDays = new BigDecimal(result.frequencyInterval.getDays());
                BigDecimal rangeFactor = overlapDays.divide(frequencyDays, MathContext.DECIMAL64);
                BigDecimal freqFactor = freq.numerator.divide(freq.denominator, MathContext.DECIMAL64);
                BigDecimal currentValue = leaseTerm.valueForDueDate(dueDate);
                if (currentValue != null && freqFactor != null && rangeFactor != null) {
                    result.value = currentValue.multiply(freqFactor).multiply(rangeFactor).setScale(2, RoundingMode.HALF_UP);
                }
            }
        }
        return result;
    }

    List<CalculationResult> calculationResults(LeaseTerm leaseTerm, LocalDate periodStartDate, LocalDate dueDate, InvoicingFrequency invoicingFrequency) {
        List<CalculationResult> results = new ArrayList<CalculationResult>();
        LocalDate intervalStartDate = periodStartDate;
        LocalDateInterval frequencyInterval = new LocalDateInterval(CalendarUtils.intervalMatching(intervalStartDate, invoicingFrequency.rrule));
        CalculationResult result;
        do {
            result = calculate(leaseTerm, intervalStartDate, dueDate, leaseTerm.getLeaseItem().getInvoicingFrequency());
            if (result != null) {
                results.add(result);
                intervalStartDate = result.getFrequencyInterval().getEndDate();
            }
        } while (result != null && result.getFrequencyInterval().getEndDate().isBefore(frequencyInterval.getEndDate()));

        return results;
    }

    public BigDecimal calculatedValue(LeaseTerm leaseTerm, LocalDate periodStartDate, LocalDate dueDate, InvoicingFrequency invoicingFrequency) {
        BigDecimal value = BigDecimal.ZERO;
        List<CalculationResult> results = calculationResults(leaseTerm, periodStartDate, dueDate, invoicingFrequency);
        for (CalculationResult result : results){
            value = value.add(result.getCalculatedValue());
        }
        return value;
    }

    void createInvoiceItems(LeaseTerm leaseTerm, LocalDate startDate, LocalDate dueDate, CalculationResult calculationResult, InvoicingFrequency freq) {
        if (calculationResult.value != null) {
            BigDecimal invoicedValue;
            LocalDate mockDate = estatioSettingsService.fetchEpochDate();
            if (mockDate != null && startDate.compareTo(mockDate) < 0) {
                CalculationResult mockResult = calculate(leaseTerm, startDate, startDate, freq);
                invoicedValue = mockResult.getCalculatedValue();
            } else {
                invoicedValue = leaseTerm.invoicedValueFor(startDate);
            }
            BigDecimal newValue = calculationResult.value.subtract(invoicedValue);
            if (newValue.compareTo(BigDecimal.ZERO) != 0) {
                InvoiceItemForLease invoiceItem = leaseTerm.findOrCreateUnapprovedInvoiceItemFor(startDate, dueDate);
                invoiceItem.setNetAmount(newValue);
                invoiceItem.setQuantity(BigDecimal.ONE);
                LeaseItem leaseItem = leaseTerm.getLeaseItem();
                Charge charge = leaseItem.getCharge();
                invoiceItem.setCharge(charge);
                invoiceItem.setDescription(charge.getDescription());
                invoiceItem.setDueDate(dueDate);
                invoiceItem.setStartDate(calculationResult.frequencyInterval.getStartDate());
                invoiceItem.setEndDate(calculationResult.frequencyInterval.getEndDate());
                Tax tax = charge.getTax();
                invoiceItem.setTax(tax);
                invoiceItem.attachToInvoice();
                invoiceItem.verify();
            }
        }
    }

    // {{ injected: EstatioSettingsService
    private EstatioSettingsService estatioSettingsService;

    public void setEstatioSettings(final EstatioSettingsService estatioSettings) {
        this.estatioSettingsService = estatioSettings;
    }
    // }}

}

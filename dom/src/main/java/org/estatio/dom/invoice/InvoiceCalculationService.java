package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

import com.google.common.collect.Lists;

import org.estatio.appsettings.EstatioSettingsService;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.utils.CalenderUtils;
import org.estatio.dom.utils.DateRange;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Hidden;

@Hidden
public class InvoiceCalculationService {

    public List<InvoiceItem> calculateAndInvoiceItems(LeaseTerm leaseTerm, LocalDate startDate, LocalDate dueDate) {
        CalculationResult result = calculate(leaseTerm, startDate, dueDate);
        return createInvoiceItems(leaseTerm, startDate, dueDate, result);
    }

    static class CalculationResult {
        BigDecimal value;
        DateRange boundingRange;
        
        public BigDecimal getCalculatedValue() {
            return value;
        }
    }
    
    CalculationResult calculate(LeaseTerm leaseTerm, LocalDate periodStartDate, LocalDate dueDate) {

        CalculationResult result = new CalculationResult();

        InvoicingFrequency freq = leaseTerm.getLeaseItem().getInvoicingFrequency();
        result.boundingRange = new DateRange(CalenderUtils.intervalMatching(periodStartDate, freq.rrule));
        if (result.boundingRange.getStartDate() != null) {
            DateRange range = new DateRange(leaseTerm.getStartDate(), leaseTerm.getEndDate(), true);
            range.setBoundingRange(result.boundingRange);
            BigDecimal boundingRangeDays = new BigDecimal(result.boundingRange.getDays());
            BigDecimal rangeDays = new BigDecimal(range.getActualDays());
            BigDecimal rangeFactor = rangeDays.divide(boundingRangeDays, MathContext.DECIMAL64);
            BigDecimal freqFactor = freq.numerator.divide(freq.denominator, MathContext.DECIMAL64);
            BigDecimal currentValue = leaseTerm.valueForDueDate(dueDate);
            if (currentValue != null && freqFactor != null && rangeFactor != null) {
                result.value = currentValue.multiply(freqFactor).multiply(rangeFactor).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return result;
    }

    List<InvoiceItem> createInvoiceItems(LeaseTerm leaseTerm, LocalDate startDate, LocalDate dueDate, CalculationResult calculationResult) {
        List<InvoiceItem> impactedItems = Lists.newArrayList();
        if (calculationResult.value != null) {
            BigDecimal newValue = calculationResult.value.subtract(leaseTerm.invoicedValueFor(startDate));
            if (newValue.compareTo(BigDecimal.ZERO) != 0) {
                InvoiceItem invoiceItem = leaseTerm.findOrCreateUnapprovedInvoiceItemFor(startDate, dueDate);
                invoiceItem.setNetAmount(newValue);
                invoiceItem.setDescription(String.format("Due date {d}", startDate));
                invoiceItem.setQuantity(BigDecimal.ONE);
                LeaseItem leaseItem = leaseTerm.getLeaseItem();
                Charge charge = leaseItem.getCharge();
                invoiceItem.setCharge(charge);
                invoiceItem.setDueDate(dueDate);
                invoiceItem.setStartDate(calculationResult.boundingRange.getStartDate());
                invoiceItem.setEndDate(calculationResult.boundingRange.getEndDate());
                Tax tax = charge.getTax();
                invoiceItem.setTax(tax);
                invoiceItem.attachToInvoice();
                invoiceItem.verify();
                
                impactedItems.add(invoiceItem);
            }
        }
        return impactedItems;
    }

    
    // {{ injected: EstatioSettingsService
    private EstatioSettingsService estatioSettingsService;

    public void setEstatioSettings(final EstatioSettingsService estatioSettings) {
        this.estatioSettingsService = estatioSettings;
    }
    // }}


}

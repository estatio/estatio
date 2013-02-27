package org.estatio.dom.invoice;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.utils.CalenderUtils;
import org.estatio.dom.utils.DateRange;
import org.joda.time.LocalDate;

public class InvoiceCalculator {

    private LeaseTerm leaseTerm;
    private DateRange boundingRange;
    private BigDecimal calculatedValue;

    public BigDecimal getCalculatedValue() {
        return calculatedValue;
    }

    private LocalDate startDate;

    public InvoiceCalculator(LeaseTerm leaseTerm, LocalDate startDate) {
        this.leaseTerm = leaseTerm;
        this.startDate = startDate;
    }

    public void calculate() {
        InvoicingFrequency freq = leaseTerm.getLeaseItem().getInvoicingFrequency();
        boundingRange = new DateRange(CalenderUtils.currentInterval(startDate, freq.rrule));
        DateRange range = new DateRange(leaseTerm.getStartDate(), leaseTerm.getEndDate(), true);
        range.setBoundingRange(boundingRange);
        BigDecimal boundingRangeDays = new BigDecimal(boundingRange.getDays());
        BigDecimal rangeDays = new BigDecimal(range.getActualDays());
        BigDecimal rangeFactor = rangeDays.divide(boundingRangeDays, MathContext.DECIMAL64);
        BigDecimal freqFactor = freq.numerator.divide(freq.denominator, MathContext.DECIMAL64);
        calculatedValue = leaseTerm.getValue().multiply(freqFactor).multiply(rangeFactor).setScale(2, RoundingMode.HALF_UP);
    }
    
    public void createInvoiceItems(){
        BigDecimal newValue = calculatedValue.subtract(leaseTerm.getInvoicedValueForDate(startDate));
        if (newValue.compareTo(BigDecimal.ZERO) != 0) {
            InvoiceItem invoiceItem = leaseTerm.createInvoiceItem();
            invoiceItem.setNetAmount(newValue);
            invoiceItem.setDescription(String.format("Due date {d}", startDate));
            invoiceItem.setQuantity(BigDecimal.ONE);
            invoiceItem.setCharge(leaseTerm.getLeaseItem().getCharge());
            invoiceItem.setStartDate(boundingRange.getStartDate());
            invoiceItem.setEndDate(boundingRange.getEndDate());
        }
    }
    
}

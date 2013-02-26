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

    private Invoices invoiceRepository;

    public InvoiceCalculator(LeaseTerm leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    public void calculate(LocalDate startDate) {
        InvoicingFrequency freq = leaseTerm.getLeaseItem().getInvoicingFrequency();
        boundingRange = new DateRange(CalenderUtils.currentInterval(startDate, freq.rrule));
        DateRange range = new DateRange(leaseTerm.getStartDate(), leaseTerm.getEndDate(), true);
        range.setBoundingRange(boundingRange);
        BigDecimal boundingRangeDays = new BigDecimal(boundingRange.getDays());
        BigDecimal rangeDays = new BigDecimal(range.getActualDays());
        BigDecimal rangeFactor = rangeDays.divide(boundingRangeDays, MathContext.DECIMAL64);
        BigDecimal freqFactor = freq.numerator.divide(freq.denominator, MathContext.DECIMAL64);
        calculatedValue = leaseTerm.getValue().multiply(freqFactor).multiply(rangeFactor).setScale(2, RoundingMode.HALF_UP);
        BigDecimal invoicedValue = BigDecimal.ZERO;
        for (InvoiceItem item : leaseTerm.getInvoiceItems()) {
            if (item.getStartDate().equals(startDate)){
                // retrieve current value
                invoicedValue.add(item.getNetAmount());
            }
        }
        BigDecimal newValue = calculatedValue.subtract(invoicedValue);
        if (newValue.compareTo(BigDecimal.ZERO) != 0) {
            InvoiceItem ii = invoiceRepository.newInvoiceItem();
            ii.setLeaseTerm(leaseTerm);
            ii.setNetAmount(newValue);
            ii.setDescription(String.format("Due date {d}", startDate));
            ii.setQuantity(BigDecimal.ONE);
            ii.setCharge(leaseTerm.getLeaseItem().getCharge());
            leaseTerm.addToInvoiceItems(ii);
        }
    }

    public void removeConceptItems() {
        for (InvoiceItem item : leaseTerm.getInvoiceItems()) {
            if (item.getInvoice() == null) {
                // TODO: how to delete?

            }
        }
    }
}

package org.estatio.dom.tax;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.EstatioDomainService;

public class TaxRates extends EstatioDomainService<TaxRate> {

    public TaxRates() {
        super(TaxRates.class, TaxRate.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Other", sequence = "taxStuff.taxRates.1")
    public List<TaxRate> allTaxRates() {
        return allInstances();
    }

    // //////////////////////////////////////
    
    @Hidden
    public TaxRate newRate(final Tax tax, final LocalDate startDate, final BigDecimal percentage) {
        TaxRate currentRate = tax.taxRateFor(startDate);
        TaxRate rate;
        if (currentRate == null || !startDate.equals(currentRate.getStartDate())) {
            rate = newTransientInstance(TaxRate.class);
            rate.setTax(tax);
            rate.setStartDate(startDate);
            persist(rate);
        } else {
            rate = currentRate;
        }
        rate.setPercentage(percentage);
        if (currentRate != null) {
            TaxRate currentNextRate = currentRate.getNextRate();
            currentRate.modifyNextRate(rate);
            rate.modifyNextRate(currentNextRate);
        }
        return rate;
    }

    // //////////////////////////////////////

    @Hidden
    public TaxRate findTaxRateByTaxAndDate(final Tax tax, final LocalDate date) {
        return firstMatch("findByTaxAndDate", "tax", tax, "date", date);
    }

}

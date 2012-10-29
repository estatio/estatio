package com.eurocommercialproperties.estatio.objstore.dflt.tax;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.filter.Filter;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.index.IndexValue;
import com.eurocommercialproperties.estatio.dom.tax.Tax;
import com.eurocommercialproperties.estatio.dom.tax.TaxRate;
import com.eurocommercialproperties.estatio.dom.tax.Taxes;

public class TaxesDefault extends AbstractFactoryAndRepository implements Taxes {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "taxes";
    }

    public String iconName() {
        return "Tax";
    }

    @Override
    @MemberOrder(sequence = "1")
    public Tax newTax(String reference) {
        Tax tax = newTransientInstance(Tax.class);
        tax.setReference(reference);
        persist(tax);
        return tax;
    }

    @Override
    @MemberOrder(sequence = "2")
    public TaxRate newTaxRate(Tax tax, LocalDate startDate, BigDecimal percentage) {
        TaxRate rate = newTransientInstance(TaxRate.class);
        rate.setStartDate(startDate);
        rate.setPercentage(percentage);
        persist(rate);
        tax.addToRates(rate);
        return rate;
    }

    @Override
    @MemberOrder(sequence = "3")
    public TaxRate newTaxRate(TaxRate previousRate, LocalDate startDate, BigDecimal percentage) {
        TaxRate rate = newTaxRate(previousRate.getTax(), startDate, percentage);
        previousRate.setEndDate(startDate.minusDays(1));
        return rate;
    }

    @Override
    @MemberOrder(sequence = "4")
    public TaxRate findTaxRateForDate(final Tax tax, final LocalDate date) {
        return firstMatch(TaxRate.class, new Filter<TaxRate>() {
            @Override
            public boolean accept(final TaxRate rate) {
                return date.isAfter(rate.getStartDate()) &&
                        (rate.getEndDate().equals(null) || date.isBefore(rate.getEndDate()))
                        && date.equals(rate.getStartDate()) 
                        && tax.equals(rate.getTax());

            }
        });
    }
    
    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "10")
    public List<Tax> allTaxes() {
        return allInstances(Tax.class);
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "11")
    public List<TaxRate> allTaxRates() {
        return allInstances(TaxRate.class);
    }


}

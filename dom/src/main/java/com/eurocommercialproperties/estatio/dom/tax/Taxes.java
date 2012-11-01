package com.eurocommercialproperties.estatio.dom.tax;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;

@Named("Taxes")
public class Taxes extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "taxes";
    }

    public String iconName() {
        return "Tax";
    }
    // }}
    
    // {{ newTax
    @MemberOrder(sequence = "1")
    public Tax newTax(
            final @Named("Reference") String reference) {
        final Tax tax = newTransientInstance(Tax.class);
        tax.setReference(reference);
        persist(tax);
        return tax;
    }
    // }}
    
    // {{ newTaxRate
    @MemberOrder(sequence = "2")
    public TaxRate newTaxRate(
            final Tax tax,
            final @Named("Start date") LocalDate startDate,
            final @Named("Percentage") BigDecimal percentage) {
        final TaxRate rate = newTransientInstance(TaxRate.class);
        rate.setStartDate(startDate);
        rate.setPercentage(percentage);
        persist(rate);
        tax.addToRates(rate);
        return rate;
    }
    // }}
    
    // {{ newTaxRate
    @MemberOrder(sequence = "3")
    public TaxRate newTaxRate(
            final @Named("Previous Rate") TaxRate previousRate,
            final @Named("Start date") LocalDate startDate,
            final @Named("Percentage") BigDecimal percentage) {
            final TaxRate rate = newTaxRate(previousRate.getTax(), startDate, percentage);
        previousRate.setEndDate(startDate.minusDays(1));
        return rate;
    }
    // }}
    
    // {{ findTaxRateForDate
    @MemberOrder(sequence = "4")
    public TaxRate findTaxRateForDate(
        final Tax tax,
        final @Named("Date") LocalDate date) {
        return firstMatch(TaxRate.class, new Filter<TaxRate>() {
            @Override
            public boolean accept(
                final TaxRate rate) {
                return date.isAfter(rate.getStartDate()) && (rate.getEndDate().equals(null) || date.isBefore(rate.getEndDate())) && date.equals(rate.getStartDate()) && tax.equals(rate.getTax());

            }
        });
    }
    // }}
    
    // {{ allTaxes
    // (not a prototype, bounded)
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "5")
    public List<Tax> allTaxes() {
        return allInstances(Tax.class);
    }
    // }}
    
    // {{ allTaxRates
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "11")
    public List<TaxRate> allTaxRates() {
        return allInstances(TaxRate.class);
    }
    // }}
}

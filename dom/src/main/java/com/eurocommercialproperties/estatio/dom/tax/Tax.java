package com.eurocommercialproperties.estatio.dom.tax;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Title;
import org.joda.time.LocalDate;

@PersistenceCapable
public class Tax extends AbstractDomainObject {

    // {{ Reference (property)
    private String reference;

    @Title
    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Name (property)
    private String name;

    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Rates (Collection)
    private Set<TaxRate> rates = new LinkedHashSet<TaxRate>();

    @Persistent(mappedBy = "tax")
    @MemberOrder(sequence = "3")
    public Set<TaxRate> getRates() {
        return rates;
    }

    public void setRates(final Set<TaxRate> rates) {
        this.rates = rates;
    }

    // }}
    

    public void addToRates(final TaxRate taxRate) {
        // check for no-op
        if (taxRate == null || getRates().contains(taxRate)) {
            return;
        }
        // associate arg
        taxRate.setTax(this);
        getRates().add(taxRate);
        // additional business logic
    }

    public void removeFromRates(final TaxRate taxRate) {
        // check for no-op
        if (taxRate == null || !getRates().contains(taxRate)) {
            return;
        }
        // dissociate arg
        taxRate.setTax(null);
        getRates().remove(taxRate);
        // additional business logic
    }

    // {{ NewRate (action)
    public TaxRate newRate(
                           @Named("Start Date") LocalDate startDate,
                           @Named("Percentage") BigDecimal percentage) {
        TaxRate rate = newTransientInstance(TaxRate.class);
        rate.setStartDate(startDate);
        rate.setPercentage(percentage);
        persist(rate);
        this.addToRates(rate);
        return rate;
        
//        rates.getCurrentRate();
    }

    // }}

    // {{ getPercentageForDate
    public BigDecimal getPercentageForDate(LocalDate date) {

        TaxRate rate = taxRepo.findTaxRateForDate(this, date);
        if (rate.equals(null)){
            return null;
        }
        return rate.getPercentage();
    }
    
    // {{

    // {{ Taxes (injected)
    private Taxes taxRepo;

    public void setTaxRepo(Taxes taxes) {
        this.taxRepo = taxes;
    }

    // }}
}

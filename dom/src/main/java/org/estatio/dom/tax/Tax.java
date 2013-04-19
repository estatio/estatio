package org.estatio.dom.tax;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.estatio.dom.EstatioRefDataObject;
import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
@Bounded
@Immutable
public class Tax extends EstatioRefDataObject {

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
    private SortedSet<TaxRate> rates = new TreeSet<TaxRate>();

    @Persistent(mappedBy = "tax")
    @MemberOrder(sequence = "1")
    public SortedSet<TaxRate> getRates() {
        return rates;
    }

    public void setRates(final SortedSet<TaxRate> rates) {
        this.rates = rates;
    }

    // }}

    // {{ NewRate (action)
    public TaxRate newRate(@Named("Start Date") LocalDate startDate, @Named("Percentage") BigDecimal percentage) {
        return taxRepo.newRate(this, startDate, percentage);
    }

    // }}

    // {{ getPercentageForDate
    public BigDecimal percentageFor(LocalDate date) {
        TaxRate rate = taxRepo.findTaxRateForDate(this, date);
        if (rate.equals(null)) {
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

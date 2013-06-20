package org.estatio.dom.tax;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.ObjectContracts;

import org.estatio.dom.ComparableByReference;
import org.estatio.dom.EstatioRefDataObject;
import org.estatio.dom.WithNameGetter;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Query(name = "findByReference", language = "JDOQL", value = "SELECT FROM org.estatio.dom.tax.Tax WHERE reference == :reference")
@Bounded
@Immutable
public class Tax extends EstatioRefDataObject implements ComparableByReference<Tax>, WithNameGetter {

    private String reference;

    @Title
    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "tax")
    private SortedSet<TaxRate> rates = new TreeSet<TaxRate>();

    @MemberOrder(sequence = "1")
    public SortedSet<TaxRate> getRates() {
        return rates;
    }

    public void setRates(final SortedSet<TaxRate> rates) {
        this.rates = rates;
    }

    // //////////////////////////////////////

    public TaxRate newRate(@Named("Start Date") LocalDate startDate, @Named("Percentage") BigDecimal percentage) {
        return taxes.newRate(this, startDate, percentage);
    }

    // //////////////////////////////////////

    @Programmatic
    public TaxRate taxRateFor(LocalDate date) {
        TaxRate rate = taxes.findTaxRateForDate(this, date);
        return rate;
    }

    // //////////////////////////////////////

    @Programmatic
    public BigDecimal percentageFor(LocalDate date) {
        TaxRate rate = taxRateFor(date);
        if (rate == null) {
            return null;
        }
        return rate.getPercentage();
    }

    // //////////////////////////////////////

    @Override
    public String toString() {
        return ComparableByReference.ToString.of(this);
    }

    // //////////////////////////////////////

    @Override
    public int compareTo(Tax other) {
        //return ORDERING_BY_REFERENCE.compare(this, other);
        return ObjectContracts.compare(this, other, "reference");
    }

    // //////////////////////////////////////

    private Taxes taxes;

    public void injectTaxes(Taxes taxes) {
        this.taxes = taxes;
    }

}

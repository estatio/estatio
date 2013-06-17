package org.estatio.dom.tax;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.query.QueryDefault;

@Named("Taxes")
public class Taxes extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "taxes";
    }

    public String iconName() {
        return "Tax";
    }

    @MemberOrder(sequence = "1")
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @NotContributed
    public Tax newTax(final @Named("Reference") String reference) {
        final Tax tax = newTransientInstance(Tax.class);
        tax.setReference(reference);
        persist(tax);
        return tax;
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Tax> allTaxes() {
        return allInstances(Tax.class);
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<TaxRate> allTaxRates() {
        return allInstances(TaxRate.class);
    }

    @Hidden
    public TaxRate newRate(@Named("Tax") Tax tax, @Named("Start Date") LocalDate startDate, @Named("Percentage") BigDecimal percentage) {
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

    @Hidden
    public Tax findTaxByReference(final String reference) {
        return firstMatch(new QueryDefault<Tax>(Tax.class, "findByReference", "reference", reference));
    }

    @Hidden
    public TaxRate findTaxRateForDate(final @Named("Tax") Tax tax, final @Named("Date") LocalDate date) {
        return firstMatch(new QueryDefault<TaxRate>(TaxRate.class, "findForDate", "tax", tax, "date", date));
    }
}

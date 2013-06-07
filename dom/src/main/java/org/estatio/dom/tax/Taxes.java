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
import org.apache.isis.applib.filter.Filter;

import org.estatio.dom.utils.StringUtils;

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
        final String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch(Tax.class, new Filter<Tax>() {
            @Override
            public boolean accept(final Tax tax) {
                return (tax.getReference().matches(regex));
            }
        });
    }

    @Hidden
    public TaxRate findTaxRateForDate(final @Named("Tax") Tax tax, final @Named("Date") LocalDate date) {
        return firstMatch(TaxRate.class, new Filter<TaxRate>() {
            @Override
            public boolean accept(final TaxRate rate) {
                return tax.equals(rate.getTax()) && date.compareTo(rate.getStartDate()) >= 0 && (rate.getEndDate() == null || date.compareTo(rate.getEndDate()) <= 0);
            }
        });
    }
}

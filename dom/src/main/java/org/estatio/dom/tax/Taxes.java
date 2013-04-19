package org.estatio.dom.tax;

import java.math.BigDecimal;
import java.security.acl.NotOwnerException;
import java.util.List;

import org.joda.time.LocalDate;


import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;
import org.estatio.dom.utils.StringUtils;

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
    
    @MemberOrder(sequence = "2")
    public TaxRate newRate(@Named("Tax") Tax tax, @Named("Start Date") LocalDate startDate, @Named("Percentage") BigDecimal percentage) {
        TaxRate rate = newTransientInstance(TaxRate.class);
        rate.setTax(tax);
        rate.setStartDate(startDate);
        rate.setPercentage(percentage);
        persist(rate);
        return rate;
    }

    
    @MemberOrder(sequence = "3")
    public Tax findTaxByReference(final String reference) {
        final String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch(Tax.class, new Filter<Tax>() {
            @Override
            public boolean accept(
                final Tax tax) {
                return (tax.getReference().matches(regex));
            }
        });
    }
    
    // {{ findTaxRateForDate
    @MemberOrder(sequence = "4")
    public TaxRate findTaxRateForDate(
        final @Named("Tax") Tax tax,
        final @Named("Date") LocalDate date) {
        return firstMatch(TaxRate.class, new Filter<TaxRate>() {
            @Override
            public boolean accept(
                final TaxRate rate) {
                return 
                    tax.equals(rate.getTax()) &&
                    date.compareTo(rate.getStartDate()) >= 0 && 
                    (rate.getEndDate() == null ||date.compareTo(rate.getEndDate()) <= 0);
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

package com.eurocommercialproperties.estatio.dom.tax;

import java.math.BigDecimal;
import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.joda.time.LocalDate;

@Named("Taxes")
public interface Taxes {

    @MemberOrder(sequence = "1")
    public Tax newTax(String reference);

    @MemberOrder(sequence = "2")
    public TaxRate newTaxRate(Tax tax, LocalDate startDate, BigDecimal percentage);

    @MemberOrder(sequence = "3")
    public TaxRate newTaxRate(TaxRate taxRate, LocalDate startDate, BigDecimal percentage);

    @MemberOrder(sequence = "4")
    public TaxRate findTaxRateForDate(Tax tax, LocalDate date);
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "10")
    List<Tax> allTaxes();

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "11")
    List<TaxRate> allTaxRates();


}

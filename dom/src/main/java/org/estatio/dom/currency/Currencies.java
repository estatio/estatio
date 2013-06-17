package org.estatio.dom.currency;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;

@Named("Currencies")
public class Currencies extends EstatioDomainService {

    public Currencies() {
        super(Currencies.class, Currency.class);
    }

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Currency newCurrency(@Named("reference") String reference) {
        Currency curr = newTransientInstance(Currency.class);
        curr.setReference(reference);
        persist(curr);
        return curr;
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Currency findCurrencyByReference(@Named("reference") final String reference) {
        throw new NotImplementedException();
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Currency> allCurrencies() {
        return allInstances(Currency.class);
    }
}

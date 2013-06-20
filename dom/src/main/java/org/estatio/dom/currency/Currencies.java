package org.estatio.dom.currency;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class Currencies extends EstatioDomainService<Currency> {

    public Currencies() {
        super(Currencies.class, Currency.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Currency newCurrency(@Named("reference") String reference) {
        final Currency currency = newTransientInstance();
        currency.setReference(reference);
        persist(currency);
        return currency;
    }
    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Currency findCurrencyByReference(@Named("reference") final String reference) {
        String rexeg = StringUtils.wildcardToRegex(reference);
        return firstMatch("currency_findCurrencyByReference", "r", rexeg);
    }

    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<Currency> allCurrencies() {
        return allInstances();
    }
}

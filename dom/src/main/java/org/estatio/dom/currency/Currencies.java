package org.estatio.dom.currency;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;
import org.estatio.dom.utils.StringUtils;

@Named("Currencies")
public class Currencies extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "currencies";
    }

    public String iconName() {
        return "Currency";
    }

    // {{ NewCurrency
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Currency newCurrency(@Named("reference") String reference) {
        Currency curr = newTransientInstance(Currency.class);
        curr.setReference(reference);
        persist(curr);
        return curr;
    }

    // }}

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Currency findCurrencyByReference(@Named("reference") final String reference) {
        final String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch(Currency.class, new Filter<Currency>() {
            @Override
            public boolean accept(final Currency curr) {
                return curr.getReference().matches(regex);
            }
        });
    }

    // {{ AllCurrencies
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Currency> allCurrencies() {
        return allInstances(Currency.class);
    }
    // }}
}

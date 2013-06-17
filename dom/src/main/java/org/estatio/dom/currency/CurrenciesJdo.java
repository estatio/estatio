package org.estatio.dom.currency;


import org.estatio.dom.utils.StringUtils;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

@Named("Currencies")
public class CurrenciesJdo extends Currencies {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Currency findCurrencyByReference(@Named("Reference") String reference) {
        String rexeg = StringUtils.wildcardToRegex(reference);
        return firstMatch(queryForFindCurrencyByReference(rexeg));
    }

    private static QueryDefault<Currency> queryForFindCurrencyByReference(String pattern) {
        return new QueryDefault<Currency>(Currency.class, "currency_findCurrencyByReference", "r", pattern);
    }

}

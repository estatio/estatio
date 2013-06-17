package org.estatio.dom.financial;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;
import org.estatio.dom.utils.StringUtils;

public class FinancialAccountsJdo extends FinancialAccounts {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public FinancialAccount findByReference(@Named("Reference") String reference) {
        String rexeg = StringUtils.wildcardToRegex(reference);
        return firstMatch(queryForFindFinancialAccountByReference(rexeg));
    }
    
    private static QueryDefault<FinancialAccount> queryForFindFinancialAccountByReference(String pattern) {
        return new QueryDefault<FinancialAccount>(FinancialAccount.class, "charge_findFinancialAccountByReference", "r", pattern);
    }

}

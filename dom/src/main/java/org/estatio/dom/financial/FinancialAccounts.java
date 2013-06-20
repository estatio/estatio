package org.estatio.dom.financial;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

@Named("Accounts")
public class FinancialAccounts extends EstatioDomainService {

    public FinancialAccounts() {
        super(FinancialAccounts.class, FinancialAccount.class);
    }

    // //////////////////////////////////////

    @Hidden
    public BankAccount newBankAccount(final @Named("IBAN") String IBAN) {
        final BankAccount ba = newTransientInstance(BankAccount.class);
        ba.setIBAN(IBAN);
        ba.setName(IBAN);
        ba.setReference(IBAN);
        persist(ba);
        return ba;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public FinancialAccount findByReference(@Named("Reference") String reference) {
        String rexeg = StringUtils.wildcardToRegex(reference);
        return firstMatch(queryForFindFinancialAccountByReference(rexeg));
    }
    
    private static QueryDefault<FinancialAccount> queryForFindFinancialAccountByReference(String pattern) {
        return new QueryDefault<FinancialAccount>(FinancialAccount.class, "charge_findFinancialAccountByReference", "r", pattern);
    }

    // //////////////////////////////////////

    @Prototype
    public List<FinancialAccount> allAccounts() {
        return allInstances(FinancialAccount.class);
    }
    
}

package org.estatio.dom.financial;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;

import org.estatio.dom.utils.StringUtils;

@Named("Accounts")
public class FinancialAccounts extends AbstractFactoryAndRepository {

    @Override
    public String getId() {
        return "financialAccounts";
    }

    public String iconName() {
        return "FinancialAccount";
    }

    @Hidden
    public BankAccount newBankAccount(final @Named("IBAN") String IBAN) {
        final BankAccount ba = newTransientInstance(BankAccount.class);
        ba.setIBAN(IBAN);
        ba.setName(IBAN);
        ba.setReference(IBAN);
        persist(ba);
        return ba;
    }
    
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public FinancialAccount findByReference(@Named("Reference") String reference) {
        final String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch(FinancialAccount.class, new Filter<FinancialAccount>() {
            @Override
            public boolean accept(final FinancialAccount account) {
                return account.getReference().matches(regex);
            }
        });
    }

    @Prototype
    public List<FinancialAccount> allAccounts() {
        return allInstances(FinancialAccount.class);
    }

}

package org.estatio.dom.financial;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

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

    @Prototype
    public List<FinancialAccount> allAccounts() {
        return allInstances(FinancialAccount.class);
    }

}

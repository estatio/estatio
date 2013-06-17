package org.estatio.dom.financial;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
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

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public FinancialAccount findByReference(@Named("Reference") String reference) {
        throw new NotImplementedException();
    }

    @Prototype
    public List<FinancialAccount> allAccounts() {
        return allInstances(FinancialAccount.class);
    }

}

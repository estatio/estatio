package org.estatio.dom.financial;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;

@Named("Accounts")
public class FinancialAccounts extends EstatioDomainService<FinancialAccount> {

    public FinancialAccounts() {
        super(FinancialAccounts.class, FinancialAccount.class);
    }

    // //////////////////////////////////////

    @Hidden
    public BankAccount newBankAccount(final @Named("Owner") Party owner, final @Named("IBAN") String IBAN) {
        final BankAccount bankAccount = newTransientInstance(BankAccount.class);
        bankAccount.setIBAN(IBAN);
        bankAccount.setName(IBAN);
        bankAccount.setReference(IBAN);
        persistIfNotAlready(bankAccount);
        bankAccount.setOwner(owner);
        return bankAccount;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public FinancialAccount findAccountByReference(@Named("Reference") String reference) {
        String regex = StringUtils.wildcardToRegex(reference);
        return firstMatch("findByReference", "reference", regex);
    }
    
    // //////////////////////////////////////
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Hidden
    public List<BankAccount> findBankAccountsByParty(Party party) {
        return (List)allMatches("findByTypeAndParty", "type", FinancialAccountType.BANK_ACCOUNT, "owner", party);
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.SAFE)
    @Prototype
    @MemberOrder(sequence = "99")
    public List<FinancialAccount> allAccounts() {
        return allInstances();
    }

    
}

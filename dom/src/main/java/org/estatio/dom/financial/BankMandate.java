package org.estatio.dom.financial;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.party.Party;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
public class BankMandate extends Agreement {
    
    
    // {{ BankAccount (property)
    private FinancialAccount bankAccount;

    @MemberOrder(name = "Details", sequence = "11")
    public FinancialAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(final FinancialAccount bankAccount) {
        this.bankAccount = bankAccount;
    }
    // }}
    
    
    // {{ Derived attribute (overridden)
    @MemberOrder(sequence = "3")
    public Party getPrimaryParty() {
        return findParty(FinancialConstants.ART_CREDITOR);
    }

    @MemberOrder(sequence = "4")
    public Party getSecondaryParty() {
        return findParty(FinancialConstants.ART_DEBTOR);
    }
    // }}


}

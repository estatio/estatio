package org.estatio.dom.agreement;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.MemberOrder;
import org.estatio.dom.financial.FinancialAccount;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
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
    
    
}

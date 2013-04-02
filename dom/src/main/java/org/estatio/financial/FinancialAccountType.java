package org.estatio.financial;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

import com.google.common.collect.Ordering;

public enum FinancialAccountType {

    BANK_ACCOUNT("Bank Account", BankAccount.class);

    private String title;
    private final Class<? extends FinancialAccount> clss;

    private FinancialAccountType(String title, Class<? extends FinancialAccount> clss) {
        this.title = title;
        this.clss = clss;
    }

    public String title() {
        return title;
    }

    public static final Ordering<FinancialAccountType> ORDERING_NATURAL = Ordering.<FinancialAccountType>natural().nullsFirst();

    public FinancialAccount createAccount(DomainObjectContainer container){ 
        try {
            FinancialAccount account = container.newTransientInstance(clss);
            return account;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
    
}

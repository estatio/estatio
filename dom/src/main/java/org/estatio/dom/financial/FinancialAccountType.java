package org.estatio.dom.financial;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

import org.estatio.dom.utils.StringUtils;

import com.google.common.collect.Ordering;

public enum FinancialAccountType {

    BANK_ACCOUNT(BankAccount.class);

    private final Class<? extends FinancialAccount> clss;

    private FinancialAccountType(Class<? extends FinancialAccount> clss) {
        this.clss = clss;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
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

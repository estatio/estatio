package org.estatio.dom.financial;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

import org.estatio.dom.PowerType;
import org.estatio.dom.utils.StringUtils;

public enum FinancialAccountType implements PowerType<FinancialAccount> {

    BANK_ACCOUNT(BankAccount.class);

    private final Class<? extends FinancialAccount> clss;

    private FinancialAccountType(Class<? extends FinancialAccount> clss) {
        this.clss = clss;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    // //////////////////////////////////////

    public FinancialAccount create(DomainObjectContainer container){ 
        try {
            FinancialAccount account = container.newTransientInstance(clss);
            return account;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
    
}

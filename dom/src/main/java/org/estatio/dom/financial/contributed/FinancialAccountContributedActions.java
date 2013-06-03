package org.estatio.dom.financial.contributed;

import java.util.List;

import org.apache.isis.applib.AbstractContainedObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.filter.Filter;

import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.party.Party;

public class FinancialAccountContributedActions extends AbstractContainedObject {

    @NotInServiceMenu
    @MemberOrder(sequence = "13")
    public FinancialAccount addAccount(final Party owner, final FinancialAccountType financialAccountType) {
        FinancialAccount financialAccount = financialAccountType.create(getContainer());
        financialAccount.setOwner(owner);
        return financialAccount;
    }
    
    @NotInServiceMenu
    @MemberOrder(sequence = "13.5")
    public List<FinancialAccount> listAccounts(final Party owner) {
        // TODO: replace with JDOQL
        return allMatches(FinancialAccount.class, new Filter<FinancialAccount>(){
            @Override
            public boolean accept(FinancialAccount t) {
                return t.getOwner() == owner;
            }
        });
    }
    
}

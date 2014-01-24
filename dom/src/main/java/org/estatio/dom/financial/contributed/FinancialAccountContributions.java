/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.financial.contributed;

import java.util.List;

import org.apache.isis.applib.AbstractContainedObject;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;

import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.party.Party;

@Hidden
public class FinancialAccountContributions extends AbstractContainedObject {

    @NotInServiceMenu
    @MemberOrder(name = "Financial Accounts", sequence = "13")
    public FinancialAccount addAccount(
            final Party owner,
            final FinancialAccountType financialAccountType,
            final @Named("Reference") String reference,
            final @Named("Name") String name) {
        FinancialAccount account = financialAccountType.create(getContainer());
        account.setOwner(owner);
        account.setReference(reference);
        account.setName(name);
        persistIfNotAlready(account);
        return account;
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    @NotContributed(As.ACTION)
    @MemberOrder(name = "Financial Accounts", sequence = "13.5")
    public List<FinancialAccount> financialAccounts(final Party owner) {
        return financialAccounts.findAccountsByOwner(owner);
    }

    // //////////////////////////////////////

    private FinancialAccounts financialAccounts;

    public void injectFinancialAccounts(final FinancialAccounts financialAccounts) {
        this.financialAccounts = financialAccounts;
    }

}

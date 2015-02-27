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
package org.estatio.dom.financial;

import java.util.List;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.party.Party;

@DomainService(repositoryFor = FinancialAccount.class)
@DomainServiceLayout(
        named = "Accounts",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "30.1")
public class FinancialAccounts extends UdoDomainRepositoryAndFactory<FinancialAccount> {

    public FinancialAccounts() {
        super(FinancialAccounts.class, FinancialAccount.class);
    }

    @Override
    public String iconName() {
        return "FinancialAccount";
    }

    // //////////////////////////////////////

    @Programmatic
    public FinancialAccount newFinancialAccount(
            final FinancialAccountType financialAccountType,
            final String reference,
            final String name,
            final Party owner) {
        FinancialAccount financialAccount = financialAccountType.create(getContainer());
        financialAccount.setReference(reference);
        financialAccount.setName(name);
        financialAccount.setOwner(owner);
        return financialAccount;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "2")
    public FinancialAccount findAccountByReference(final @ParameterLayout(named = "Reference") String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FinancialAccount> findAccountsByOwner(final Party party) {
        return allMatches("findByOwner", "owner", party);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<FinancialAccount> findAccountsByTypeOwner(final FinancialAccountType accountType, final Party party) {
        return allMatches("findByTypeAndOwner",
                "type", accountType,
                "owner", party);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "3")
    public List<FinancialAccount> allAccounts() {
        return allInstances();
    }

}

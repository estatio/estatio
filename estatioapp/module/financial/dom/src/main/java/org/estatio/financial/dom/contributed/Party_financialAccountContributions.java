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
package org.estatio.financial.dom.contributed;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.base.dom.types.ReferenceType;

import org.estatio.dom.UdoDomainService;
import org.estatio.financial.dom.FinancialAccount;
import org.estatio.financial.dom.FinancialAccountRepository;
import org.estatio.financial.dom.FinancialAccountTransactionRepository;
import org.estatio.financial.dom.FinancialAccountType;
import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class Party_financialAccountContributions extends UdoDomainService<Party_financialAccountContributions> {

    public Party_financialAccountContributions() {
        super(Party_financialAccountContributions.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(hidden = Where.EVERYWHERE)
    @MemberOrder(name = "financialAccounts", sequence = "1")
    public FinancialAccount addAccount(
            final Party owner,
            final FinancialAccountType financialAccountType,
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX) String reference,
            final String name) {
        return financialAccountRepository.newFinancialAccount(financialAccountType, reference, name, owner);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<FinancialAccount> financialAccounts(final Party owner) {
        return financialAccountRepository.findAccountsByOwner(owner);
    }

    // //////////////////////////////////////


    @Inject
    private FinancialAccountRepository financialAccountRepository;

    @Inject
    private FinancialAccountTransactionRepository financialAccountTransactionRepository;

}

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

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainService;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountTransaction;
import org.estatio.dom.financial.FinancialAccountTransactionRepository;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.financial.FinancialAccountRepository;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.party.Party;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class FinancialAccountContributions extends UdoDomainService<FinancialAccountContributions> {

    public FinancialAccountContributions() {
        super(FinancialAccountContributions.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(hidden = Where.EVERYWHERE)
    @MemberOrder(name = "financialAccounts", sequence = "1")
    public FinancialAccount addAccount(
            final Party owner,
            final FinancialAccountType financialAccountType,
            final @Parameter(regexPattern = RegexValidation.REFERENCE) String reference,
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

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<FinancialAccountTransaction> transactions(Guarantee guarantee) {
        return financialAccountTransactionRepository.transactions(guarantee.getFinancialAccount());
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Guarantee newTransaction(
            final Guarantee guarantee,
            final LocalDate transactionDate,
            final String description,
            final BigDecimal amount) {

        financialAccountTransactionRepository.newTransaction(guarantee.getFinancialAccount(), transactionDate, description, amount);
        return guarantee;
    }

    public boolean hideNewTransaction(final Guarantee guarantee, final LocalDate transactionDate, final String description, final BigDecimal amount) {
        return guarantee.getFinancialAccount() == null;
    }

    @Action(semantics = SemanticsOf.SAFE)
    public BigDecimal balance(FinancialAccount financialAccount) {
        BigDecimal balance = financialAccountTransactionRepository.balance(financialAccount);
        return balance;
    }

    // //////////////////////////////////////

    @Inject
    private FinancialAccountRepository financialAccountRepository;

    @Inject
    private FinancialAccountTransactionRepository financialAccountTransactionRepository;

}

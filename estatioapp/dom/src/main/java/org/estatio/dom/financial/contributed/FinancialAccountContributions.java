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

import org.apache.isis.applib.annotation.*;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainService;
import org.estatio.dom.financial.*;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class FinancialAccountContributions extends UdoDomainService<FinancialAccountContributions> {

    public FinancialAccountContributions() {
        super(FinancialAccountContributions.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "Financial Accounts", sequence = "13")
    public FinancialAccount addAccount(
            final Party owner,
            final FinancialAccountType financialAccountType,
            final @Parameter(regexPattern = RegexValidation.REFERENCE) String reference,
            final String name) {
        return financialAccounts.newFinancialAccount(financialAccountType, reference, name, owner);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @MemberOrder(name = "Financial Accounts", sequence = "13.5")
    public List<FinancialAccount> financialAccounts(final Party owner) {
        return financialAccounts.findAccountsByOwner(owner);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.EAGERLY)
    public List<FinancialAccountTransaction> transactions(Guarantee guarantee) {
        return financialAccountTransactions.transactions(guarantee.getFinancialAccount());
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Guarantee newTransaction(
            final Guarantee guarantee,
            final LocalDate transactionDate,
            final String description,
            final BigDecimal amount) {

        financialAccountTransactions.newTransaction(guarantee.getFinancialAccount(), transactionDate, description, amount);
        return guarantee;
    }

    public boolean hideNewTransaction(final Guarantee guarantee, final LocalDate transactionDate, final String description, final BigDecimal amount) {
        return guarantee.getFinancialAccount() == null;
    }

    // //////////////////////////////////////

    @Inject
    private FinancialAccounts financialAccounts;

    @Inject
    private FinancialAccountTransactions financialAccountTransactions;

}

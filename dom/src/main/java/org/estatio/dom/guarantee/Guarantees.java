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
package org.estatio.dom.guarantee;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountTransaction;
import org.estatio.dom.financial.FinancialAccountTransactions;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.utils.StringUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

public class Guarantees extends EstatioDomainService<Guarantee> {

    @Override
    public String iconName() {
        return "Guarantee";
    }
    
    public Guarantees() {
        super(Guarantees.class, Guarantee.class);
    }

    // //////////////////////////////////////
    
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Guarantee newGuarantee(
            final Lease lease,
            final @Named("Reference") String reference,
            final @Named("Name") String name,
            final GuaranteeType guaranteeType,
            final @Named("Start date") LocalDate startDate,
            final @Named("End date") @Optional LocalDate endDate,
            final @Named("Description") String description,
            final @Named("Maximum amount") BigDecimal maximumAmount
            ) {
        Guarantee guarantee = newTransientInstance(Guarantee.class);
        final AgreementType at = agreementTypes.find(GuaranteeConstants.AT_GUARANTEE);
        guarantee.setType(at);
        guarantee.setReference(reference);
        guarantee.setName(name);
        guarantee.setStartDate(startDate);
        guarantee.setEndDate(endDate);
        guarantee.setGuaranteeType(guaranteeType);
        guarantee.setLease(lease);
        guarantee.setMaximumAmount(maximumAmount);
        persistIfNotAlready(guarantee);
        guarantee.newRole(
                agreementRoleTypes.findByTitle(GuaranteeConstants.ART_GUARANTEE),
                lease.getSecondaryParty(), null, null);
        guarantee.newRole(
                agreementRoleTypes.findByTitle(GuaranteeConstants.ART_GUARANTOR),
                lease.getPrimaryParty(), null, null);

        guarantee.setFinancialAccount(
                financialAccounts.newFinancialAccount(
                        guaranteeType.getFinancialAccountType(),
                        reference,
                        name,
                        lease.getSecondaryParty()));

        return guarantee;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<Guarantee> findGuarantees(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String refOrName) {
        String pattern = StringUtils.wildcardToCaseInsensitiveRegex(refOrName);
        return allMatches("matchByReferenceOrName", "referenceOrName", pattern);
    }

    // //////////////////////////////////////
    
    @Programmatic
    public Guarantee findByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }
    
    // //////////////////////////////////////
    
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<Guarantee> allGuarantees() {
        return allInstances();
    }
    
    // //////////////////////////////////////

    @NotInServiceMenu
    @NotContributed(As.ACTION)
    @Render(Type.EAGERLY)
    public List<FinancialAccountTransaction> transactions(Guarantee guarantee) {
        return financialAccountTransactions.transactions(guarantee.getFinancialAccount());
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    public Guarantee newTransaction(
            final Guarantee guarantee,
            final @Named("Transaction date") LocalDate transactionDate,
            final @Named("Description") String description,
            final @Named("Amount") BigDecimal amount) {

        financialAccountTransactions.newTransaction(guarantee.getFinancialAccount(), transactionDate, description, amount);
        return guarantee;
    }

    public boolean hideNewTransaction(final Guarantee guarantee, final LocalDate transactionDate, final String description, final BigDecimal amount) {
        return guarantee.getFinancialAccount() == null;
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    @NotContributed(As.ACTION)
    public List<Guarantee> guarantees(Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

    // //////////////////////////////////////

    @Programmatic
    public Guarantee findFor(FinancialAccount financialAccount) {
        return firstMatch("findByFinancialAccount", "financialAccount", financialAccount);
    }

    // //////////////////////////////////////

    @Hidden
    public List<Guarantee> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 2
                ? findGuarantees("*" + searchPhrase + "*")
                : Lists.<Guarantee> newArrayList();
    }

    // //////////////////////////////////////

    @Inject
    private AgreementTypes agreementTypes;

    @Inject
    private AgreementRoleTypes agreementRoleTypes;

    @Inject
    private FinancialAccounts financialAccounts;

    @Inject
    private FinancialAccountTransactions financialAccountTransactions;

}

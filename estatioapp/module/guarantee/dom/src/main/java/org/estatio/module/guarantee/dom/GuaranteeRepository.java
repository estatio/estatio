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
package org.estatio.module.guarantee.dom;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.utils.StringUtils;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccountTransactionRepository;
import org.estatio.module.financial.dom.FinancialAccountType;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.party.dom.Party;

@DomainService(repositoryFor = Guarantee.class, nature = NatureOfService.DOMAIN)
public class GuaranteeRepository extends UdoDomainRepositoryAndFactory<Guarantee> {

    @Override
    public String iconName() {
        return "Guarantee";
    }

    public GuaranteeRepository() {
        super(GuaranteeRepository.class, Guarantee.class);
    }

    @Programmatic
    public Guarantee newGuarantee(
            final Lease lease,
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX, regexPatternReplacement = ReferenceType.Meta.REGEX_DESCRIPTION) String reference,
            final String name,
            final GuaranteeType guaranteeType,
            final LocalDate startDate,
            final LocalDate endDate,
            final String description,
            final BigDecimal contractualAmount,
            final BigDecimal startAmount
    ) {

        AgreementRoleType artGuarantee = agreementRoleTypeRepository.find(GuaranteeAgreementRoleTypeEnum.GUARANTEE);
        Party leasePrimaryParty = lease.getPrimaryParty();

        AgreementRoleType artGuarantor = agreementRoleTypeRepository.find(GuaranteeAgreementRoleTypeEnum.GUARANTOR);
        Party leaseSecondaryParty = lease.getSecondaryParty();

        Guarantee guarantee = newTransientInstance(Guarantee.class);
        final AgreementType at = agreementTypeRepository.find(GuaranteeAgreementTypeEnum.GUARANTEE);
        guarantee.setType(at);
        guarantee.setReference(reference);
        guarantee.setDescription(description);
        guarantee.setName(name);
        guarantee.setStartDate(startDate);
        guarantee.setEndDate(endDate);
        guarantee.setGuaranteeType(guaranteeType);
        guarantee.setLease(lease);
        guarantee.setContractualAmount(contractualAmount);

        guarantee.newRole(
                artGuarantee,
                leasePrimaryParty, null, null);
        guarantee.newRole(
                artGuarantor,
                leaseSecondaryParty, null, null);

        FinancialAccountType financialAccountType = guaranteeType.getFinancialAccountType();
        if (financialAccountType != null) {
            FinancialAccount financialAccount = financialAccountRepository.newFinancialAccount(
                    financialAccountType,
                    reference,
                    name,
                    leaseSecondaryParty);
            guarantee.setFinancialAccount(financialAccount);
            if (ObjectUtils.compare(startAmount, BigDecimal.ZERO) > 0) {
                financialAccountTransactionRepository.newTransaction(guarantee.getFinancialAccount(), startDate, null, startAmount);
            }
        }

        persistIfNotAlready(guarantee);
        return guarantee;
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Guarantee> findGuarantees(
            final String refOrNameOrComments) {
        String pattern = StringUtils.wildcardToCaseInsensitiveRegex(refOrNameOrComments);
        return allMatches("matchByReferenceOrNameOrComments", "referenceOrNameOrComments", pattern);
    }

    // //////////////////////////////////////

    @Programmatic
    public Guarantee findByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Guarantee> allGuarantees() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public List<Guarantee> findByLease(final Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

    // //////////////////////////////////////

    @Programmatic
    public Guarantee findbyFinancialAccount(FinancialAccount financialAccount) {
        return firstMatch("findByFinancialAccount", "financialAccount", financialAccount);
    }

    // //////////////////////////////////////

    @CollectionLayout(hidden = Where.EVERYWHERE)
    public List<Guarantee> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 2
                ? findGuarantees("*" + searchPhrase + "*")
                : Lists.<Guarantee>newArrayList();
    }

    // //////////////////////////////////////


    @Inject
    private AgreementTypeRepository agreementTypeRepository;

    @Inject
    private AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    private FinancialAccountRepository financialAccountRepository;

    @Inject
    private FinancialAccountTransactionRepository financialAccountTransactionRepository;

}

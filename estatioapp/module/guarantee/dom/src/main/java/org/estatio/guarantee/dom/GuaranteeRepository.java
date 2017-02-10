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
package org.estatio.guarantee.dom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
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

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.agreement.dom.AgreementRoleType;
import org.estatio.agreement.dom.AgreementRoleTypeRepository;
import org.estatio.agreement.dom.AgreementType;
import org.estatio.agreement.dom.AgreementTypeRepository;
import org.estatio.financial.dom.FinancialAccount;
import org.estatio.financial.dom.FinancialAccountRepository;
import org.estatio.financial.dom.FinancialAccountTransactionRepository;
import org.estatio.financial.dom.FinancialAccountType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.party.Party;
import org.incode.module.base.dom.utils.StringUtils;

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

        AgreementRoleType artGuarantee = agreementRoleTypeRepository.findByTitle(GuaranteeConstants.ART_GUARANTEE);
        Party leasePrimaryParty = lease.getPrimaryParty();

        AgreementRoleType artGuarantor = agreementRoleTypeRepository.findByTitle(GuaranteeConstants.ART_GUARANTOR);
        Party leaseSecondaryParty = lease.getSecondaryParty();

        Guarantee guarantee = newTransientInstance(Guarantee.class);
        final AgreementType at = agreementTypeRepository.find(GuaranteeConstants.AT_GUARANTEE);
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

    @PostConstruct
    @Programmatic
    public void init(Map<String, String> properties) {
        super.init(properties);
        AgreementType agreementType = agreementTypeRepository.findOrCreate(GuaranteeConstants.AT_GUARANTEE);
        agreementRoleTypeRepository.findOrCreate(GuaranteeConstants.ART_GUARANTEE, agreementType);
        agreementRoleTypeRepository.findOrCreate(GuaranteeConstants.ART_GUARANTOR, agreementType);
        agreementRoleTypeRepository.findOrCreate(GuaranteeConstants.ART_BANK, agreementType);
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

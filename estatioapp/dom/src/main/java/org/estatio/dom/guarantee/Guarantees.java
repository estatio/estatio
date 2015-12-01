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

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.isis.applib.annotation.*;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypeRepository;
import org.estatio.dom.financial.*;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;
import org.joda.time.LocalDate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@DomainService(repositoryFor = Guarantee.class, nature = NatureOfService.DOMAIN)
public class Guarantees extends UdoDomainRepositoryAndFactory<Guarantee> {

    @Override
    public String iconName() {
        return "Guarantee";
    }

    public Guarantees() {
        super(Guarantees.class, Guarantee.class);
    }

    public Guarantee newGuarantee(
            final Lease lease,
            final String reference,
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
            FinancialAccount financialAccount = financialAccounts.newFinancialAccount(
                    financialAccountType,
                    reference,
                    name,
                    leaseSecondaryParty);
            guarantee.setFinancialAccount(financialAccount);
            if (ObjectUtils.compare(startAmount, BigDecimal.ZERO) > 0) {
                financialAccountTransactions.newTransaction(guarantee.getFinancialAccount(), startDate, null, startAmount);
            }
        }

        persistIfNotAlready(guarantee);
        return guarantee;
    }

    // //////////////////////////////////////

    public List<Guarantee> findGuarantees(
            final String refOrNameOrComments) {
        String pattern = StringUtils.wildcardToCaseInsensitiveRegex(refOrNameOrComments);
        return allMatches("matchByReferenceOrNameOrComments", "referenceOrNameOrComments", pattern);
    }

    // //////////////////////////////////////


    public Guarantee findByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    // //////////////////////////////////////


    public List<Guarantee> allGuarantees() {
        return allInstances();
    }

    // //////////////////////////////////////


    public List<Guarantee> findByLease(final Lease lease) {
        return allMatches("findByLease", "lease", lease);
    }

    // //////////////////////////////////////

    @Programmatic
    public Guarantee findFor(FinancialAccount financialAccount) {
        return firstMatch("findByFinancialAccount", "financialAccount", financialAccount);
    }

    // //////////////////////////////////////

    @CollectionLayout(hidden = Where.EVERYWHERE)
    public List<Guarantee> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 2
                ? findGuarantees("*" + searchPhrase + "*")
                : Lists.<Guarantee> newArrayList();
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
    private FinancialAccounts financialAccounts;

    @Inject
    private FinancialAccountTransactions financialAccountTransactions;

}

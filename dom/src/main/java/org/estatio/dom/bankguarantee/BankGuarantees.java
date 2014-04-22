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
package org.estatio.dom.bankguarantee;

import java.math.BigDecimal;
import java.util.List;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssets;
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;

public class BankGuarantees extends EstatioDomainService<BankGuarantee> {

    public BankGuarantees() {
        super(BankGuarantees.class, BankGuarantee.class);
    }

    // //////////////////////////////////////

    @NotContributed
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public BankGuarantee newBankGuarantee(
            final @Named("Reference") String reference,
            final @Named("Name") String name,
            final @Named("Start date") LocalDate startDate,
            final @Named("End date") LocalDate endDate,
            final @Named("Termination date") LocalDate terminationDate,
            final @Named("Decription") String description,
            final @Named("Amount") BigDecimal amount,
            final @Named("Debtor") Party creditor,
            final @Named("Debtor") Party debtor
            ) {
        BankGuarantee BankGuarantee = newTransientInstance();
        final AgreementType at = agreementTypes.find(BankGuaranteeConstants.AT_BANK_GUARANTEE);
        BankGuarantee.setType(at);
        BankGuarantee.setReference(reference);
        BankGuarantee.setName(name);
        BankGuarantee.setStartDate(startDate);
        BankGuarantee.setEndDate(endDate);
        persistIfNotAlready(BankGuarantee);

        if (debtor != null) {
            final AgreementRoleType artTenant = agreementRoleTypes.findByTitle(BankGuaranteeConstants.ART_DEBTOR);
            BankGuarantee.newRole(artTenant, debtor, null, null);
        }
        if (creditor != null) {
            final AgreementRoleType artLandlord = agreementRoleTypes.findByTitle(BankGuaranteeConstants.ART_CREDITOR);
            BankGuarantee.newRole(artLandlord, creditor, null, null);
        }
        return BankGuarantee;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<BankGuarantee> findBankGuarantees(
            final @Named("Reference or Name") @DescribedAs("May include wildcards '*' and '?'") String refOrName) {
        String pattern = StringUtils.wildcardToCaseInsensitiveRegex(refOrName);
        return allMatches("matchByReferenceOrName", "referenceOrName", pattern);
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "4")
    public List<BankGuarantee> findBankGuaranteesActiveOnDate(
            final FixedAsset fixedAsset,
            final @Named("Active On Date") LocalDate activeOnDate) {
        return allMatches("findByAssetAndActiveOnDate", "asset", fixedAsset, "activeOnDate", activeOnDate);
    }

    public List<FixedAsset> autoComplete0FindBankGuaranteesActiveOnDate(final String searchPhrase) {
        return fixedAssets.matchAssetsByReferenceOrName(searchPhrase);
    }

    public LocalDate default1FindBankGuaranteesActiveOnDate() {
        return getClockService().now();
    }

    // //////////////////////////////////////

    public List<BankGuarantee> findExpireInDateRange(final LocalDate rangeStartDate, final LocalDate rangeEndDate) {
        return allMatches(
                "findExpireInDateRange",
                "rangeStartDate", rangeStartDate,
                "rangeEndDate", rangeEndDate);
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<BankGuarantee> allBankGuarantees() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    public BankGuarantee findBankGuaranteeByReference(final String reference) {
        return firstMatch("findByReference", "reference", reference);
    }

    @Programmatic
    public List<BankGuarantee> findBankGuaranteesByProperty(final Property property) {
        return allMatches("findByProperty", "property", property);
    }

    // //////////////////////////////////////

    @Hidden
    public List<BankGuarantee> autoComplete(final String searchPhrase) {
        return searchPhrase.length() > 2
                ? findBankGuarantees("*" + searchPhrase + "*")
                : Lists.<BankGuarantee> newArrayList();
    }

    // //////////////////////////////////////

    private FixedAssets fixedAssets;

    public final void injectFixedAssets(final FixedAssets fixedAssets) {
        this.fixedAssets = fixedAssets;
    }

    private AgreementTypes agreementTypes;

    public final void injectAgreementTypes(final AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

    private AgreementRoleTypes agreementRoleTypes;

    public final void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

}

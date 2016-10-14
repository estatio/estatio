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

import java.util.Collection;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainService;
import org.incode.module.base.dom.WithInterval;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleHolder;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypeRepository;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandateConstants;

/**
 * These contributions act upon {@link AgreementRoleHolder}, and from its
 * {@link AgreementRoleHolder#getAgreements()set of} {@link AgreementRole},
 * project to the corresponding {@link BankMandate}s.
 * <p/>
 * <p/>
 * An alternative design would be to simply do a repository query against the
 * database; this would be more efficient (avoid an N+1 search as is the current
 * design). However, that query would be quite complex, having to traverse from
 * {@link BankMandate} to {@link AgreementRole} to
 * {@link org.estatio.dom.party.Party}.
 */
@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY, menuOrder = "30")
public class AgreementRoleHolder_bankMandateContributions
        extends UdoDomainService<AgreementRoleHolder_bankMandateContributions> {

    public AgreementRoleHolder_bankMandateContributions() {
        super(AgreementRoleHolder_bankMandateContributions.class);
    }

    /**
     * A contributed collection of the current {@link BankMandate}s of the
     * {@link AgreementRoleHolder}.
     * <p/>
     * <p/>
     * All {@link BankMandate} are {@link #allBankMandate(AgreementRoleHolder)
     * contributed} as an action.
     */
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.LAZILY)
    @MemberOrder(sequence = "80")
    public Collection<BankMandate> currentBankMandates(final AgreementRoleHolder agreementRoleHolder) {
        final AgreementType agreementType = agreementTypeRepository.find(BankMandateConstants.AT_MANDATE);
        return Lists.newArrayList(
                Iterables.transform(
                        Iterables.filter(
                                agreementRoleHolder.getAgreements(),
                                whetherCurrentAndAgreementTypeIs(agreementType)),
                        AgreementRole.Functions.<BankMandate>agreementOf()));
    }

    private static Predicate<AgreementRole> whetherCurrentAndAgreementTypeIs(final AgreementType agreementType) {
        return Predicates.and(
                AgreementRole.Predicates.whetherAgreementTypeIs(agreementType),
                WithInterval.Predicates.<AgreementRole>whetherCurrentIs(true));
    }

    // //////////////////////////////////////

    /**
     * A contributed action of all {@link BankMandate}s of the
     * {@link AgreementRoleHolder}.
     * <p/>
     * <p/>
     * The current {@link BankMandate}s are
     * {@link #currentBankMandates(AgreementRoleHolder) contributed} as a
     * collection.
     */
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "List All", contributed = Contributed.AS_ACTION)
    public Collection<BankMandate> allBankMandates(final AgreementRoleHolder agreementRoleHolder) {
        final AgreementType agreementType = agreementTypeRepository.find(BankMandateConstants.AT_MANDATE);
        return Lists.newArrayList(
                Iterables.transform(
                        Iterables.filter(
                                agreementRoleHolder.getAgreements(),
                                AgreementRole.Predicates.whetherAgreementTypeIs(agreementType)),
                        AgreementRole.Functions.<BankMandate>agreementOf()));
    }

    // //////////////////////////////////////

    @Inject
    private AgreementTypeRepository agreementTypeRepository;

}

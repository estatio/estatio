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
package org.estatio.module.lease.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.agreement.AgreementRoleRepository;
import org.estatio.dom.agreement.role.AgreementRoleTypeRepository;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.party.dom.Party;

@DomainService(menuOrder = "40", nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class Party_leaseContributions extends UdoDomainService<Party_leaseContributions> {

    public Party_leaseContributions() {
        super(Party_leaseContributions.class);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(defaultView = "table")
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Lease> currentLeases(final Party party) {
        return agreementRoleRepository.findByPartyAndTypeAndContainsDate(party, agreementRoleTypeRepository.findByTitle(
                LeaseAgreementRoleTypeEnum.TENANT.getTitle()), getClockService().now())
                .stream()
                .map((agreementRole) -> (Lease)agreementRole.getAgreement())
                .sorted()
                .collect(Collectors.toList());
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(named = "List All", contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "currentLeases", sequence = "1")
    public List<Lease> allLeases(final Party party) {
        return agreementRoleRepository.findByPartyAndType(party, agreementRoleTypeRepository.findByTitle(
                LeaseAgreementRoleTypeEnum.TENANT.getTitle()))
                .stream()
                .map((agreementRole) -> (Lease)agreementRole.getAgreement())
                .sorted()
                .collect(Collectors.toList());
    }

    @Inject
    AgreementRoleRepository agreementRoleRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;


}

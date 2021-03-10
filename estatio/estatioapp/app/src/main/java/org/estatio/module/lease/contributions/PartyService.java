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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseStatus;
import org.estatio.module.party.dom.Party;

@DomainService(menuOrder = "40", nature = NatureOfService.DOMAIN)
public class PartyService extends UdoDomainService<PartyService> {

    public PartyService() {
        super(PartyService.class);
    }

    public List<Lease> currentLeases(final Party party) {
        return agreementRoleRepository.findByPartyAndTypeAndContainsDate(party, agreementRoleTypeRepository.findByTitle(
                LeaseAgreementRoleTypeEnum.TENANT.getTitle()), getClockService().now())
                .stream()
                .filter(agreementRole -> agreementRole.getAgreement().getClass().isAssignableFrom(Lease.class))
                .map((agreementRole) -> (Lease)agreementRole.getAgreement())
                .filter(l->l.getStatus()!= LeaseStatus.PREVIEW)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Lease> allLeases(final Party party) {
        return agreementRoleRepository.findByPartyAndType(party, agreementRoleTypeRepository.findByTitle(
                LeaseAgreementRoleTypeEnum.TENANT.getTitle()))
                .stream()
                .filter(agreementRole -> agreementRole.getAgreement().getClass().isAssignableFrom(Lease.class))
                .map((agreementRole) -> (Lease)agreementRole.getAgreement())
                .filter(l->l.getStatus()!= LeaseStatus.PREVIEW)
                .sorted()
                .collect(Collectors.toList());
    }

    @Inject
    AgreementRoleRepository agreementRoleRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;


}

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
package org.estatio.dom.communications;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannel;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypeRepository;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;

@DomainService(nature = NatureOfService.DOMAIN)
public class AgreementRoleCommunicationChannelLocator {

    @Programmatic
    public List<CommunicationChannel> locate(
            final Agreement agreement,
            final String art,
            final String arcct,
            final CommunicationChannelType cct) {

        final List<CommunicationChannel> communicationChannels = Lists.newArrayList();

        final AgreementRoleType partyInRoleOf =
                agreementRoleTypeRepository.findByTitle(art);
        final AgreementRoleCommunicationChannelType commChannelInRoleOf =
                agreementRoleCommunicationChannelTypeRepository.findByTitle(arcct);

        final SortedSet<AgreementRole> agreementRoles = agreement.getRoles();
        for (final AgreementRole role : agreementRoles) {
            if(role.getType() == partyInRoleOf) {
                final SortedSet<AgreementRoleCommunicationChannel> rolesOfChannels = role.getCommunicationChannels();
                for (AgreementRoleCommunicationChannel roleOfChannel : rolesOfChannels) {
                    if(roleOfChannel.getType() == commChannelInRoleOf) {
                        final CommunicationChannel communicationChannel = roleOfChannel.getCommunicationChannel();
                        if(roleOfChannel.isCurrent() && (cct == null || communicationChannel.getType() == cct)) {
                            communicationChannels.add(communicationChannel);
                        }
                    }
                }
            }
        }
        return communicationChannels;
    }

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

}

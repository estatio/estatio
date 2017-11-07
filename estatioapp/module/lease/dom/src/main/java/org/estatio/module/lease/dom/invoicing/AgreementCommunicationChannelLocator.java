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
package org.estatio.module.lease.dom.invoicing;

import java.util.Arrays;
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
import org.estatio.dom.agreement.role.AgreementRoleType;
import org.estatio.dom.agreement.role.AgreementRoleTypeRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLinkRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.estatio.module.party.dom.Party;

/**
 * There are two different ways to obtain {@link CommunicationChannel}s for an {@link Agreement}.  The more complex,
 * {@link #current(Agreement, String, String, CommunicationChannelType[])} is the "official" way to obtain the
 * {@link CommunicationChannel} currently specified, while the other,
 * {@link #onFile(Agreement, String, CommunicationChannelType[])}, provides a more complete list of every channel
 * currently "on file" for the agreement party.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class AgreementCommunicationChannelLocator {

    /**
     * Locates all the {@link CommunicationChannel}s that are current and nominated to be of the specified type (eg
     * &quot;Invoice Address&quot;) for the specified {@link AgreementRole} (eg &quot;Tenant&quot;) of the
     * specified {@link Agreement}, optionally filtering by channel type(s) (eg &quot;EMAIL_ADDRESS&quot;)
     */
    @Programmatic
    public List<CommunicationChannel> current(
            final Agreement agreement,
            final String art,
            final String arcct,
            final CommunicationChannelType... cctIfAny) {
        return current(agreement, art, arcct, asListElseAll(cctIfAny));
    }

    @Programmatic
    public List<CommunicationChannel> current(
            final Agreement agreement,
            final String art,
            final String arcct,
            final List<CommunicationChannelType> communicationChannelTypes) {

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
                        if(roleOfChannel.isCurrent() && communicationChannelTypes.contains(communicationChannel.getType())) {
                            communicationChannels.add(communicationChannel);
                        }
                    }
                }
            }
        }
        return communicationChannels;
    }

    /**
     * Locates all the {@link CommunicationChannel}s of the {@link Party} that plays the specified role
     * (eg &quot;Tenant&quot;) of the given agreement, optionally filtering by channel type(s) (eg &quot;EMAIL_ADDRESS&quot;).
     */
    @Programmatic
    public List<CommunicationChannel> onFile(
            final Agreement agreement,
            final String art,
            final CommunicationChannelType... cctIfAny) {
        return onFile(agreement, art, asListElseAll(cctIfAny));
    }

    @Programmatic
    public List<CommunicationChannel> onFile(
            final Agreement agreement,
            final String art,
            final List<CommunicationChannelType> communicationChannelTypes) {

        final List<CommunicationChannel> communicationChannels = Lists.newArrayList();

        final AgreementRoleType partyInRoleOf =
                agreementRoleTypeRepository.findByTitle(art);

        final SortedSet<AgreementRole> agreementRoles = agreement.getRoles();
        for (final AgreementRole role : agreementRoles) {
            if(role.getType() == partyInRoleOf) {
                final Party party = role.getParty();
                final SortedSet<CommunicationChannel> channels = communicationChannelRepository.findByOwner(party);
                for (CommunicationChannel channel : channels) {
                    if(communicationChannelTypes.contains(channel.getType())) {
                        communicationChannels.add(channel);
                    }
                }
                break;
            }
        }
        return communicationChannels;
    }


    private static List<CommunicationChannelType> asListElseAll(final CommunicationChannelType[] cctIfAny) {
        if(cctIfAny.length == 0) {
            return asListElseAll(CommunicationChannelType.values());
        }
        return Arrays.asList(cctIfAny);
    }


    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;
    @Inject
    CommunicationChannelRepository communicationChannelRepository;

}

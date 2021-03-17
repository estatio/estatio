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
package org.estatio.module.agreement.subscriptions;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannel;
import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelRepository;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class CommunicationChannelSubscriptions extends UdoDomainService<CommunicationChannelSubscriptions> {

    public CommunicationChannelSubscriptions() {
        super(CommunicationChannelSubscriptions.class);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final CommunicationChannel.RemoveEvent ev) {
        CommunicationChannel sourceCommunicationChannel = ev.getSource();
        CommunicationChannel replacementCommunicationChannel = ev.getReplacement();

        List<AgreementRoleCommunicationChannel> communicationChannels;
        switch (ev.getEventPhase()) {
        case VALIDATE:
            communicationChannels =
                    agreementRoleCommunicationChannelRepository.findByCommunicationChannel(sourceCommunicationChannel);
            if (communicationChannels.size() > 0 && replacementCommunicationChannel == null) {
                ev.invalidate("Communication channel is being used: provide a replacement");
            } else {
                scratchpad.put(onCommunicationChannelRemoveScratchpadKey = UUID.randomUUID(), communicationChannels);
            }
            break;
        case EXECUTING:
            communicationChannels =
                    (List<AgreementRoleCommunicationChannel>) scratchpad.get(onCommunicationChannelRemoveScratchpadKey);
            for (AgreementRoleCommunicationChannel arcc : communicationChannels) {
                arcc.setCommunicationChannel(replacementCommunicationChannel);
            }
            break;
        default:
            break;
        }
    }

    private transient UUID onCommunicationChannelRemoveScratchpadKey;



    @Inject
    private Scratchpad scratchpad;

    @Inject
    private AgreementRoleCommunicationChannelRepository agreementRoleCommunicationChannelRepository;

}

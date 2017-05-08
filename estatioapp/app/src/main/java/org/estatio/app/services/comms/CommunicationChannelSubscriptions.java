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
package org.estatio.app.services.comms;

import java.util.List;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.comms.CommChannelRole;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.comms.CommunicationRepository;

import org.estatio.dom.UdoDomainService;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class CommunicationChannelSubscriptions extends UdoDomainService<org.estatio.dom.agreement.CommunicationChannelSubscriptions> {

    public CommunicationChannelSubscriptions() {
        super(org.estatio.dom.agreement.CommunicationChannelSubscriptions.class);
    }

    @Subscribe
    public void on(final CommunicationChannel.RemoveEvent ev) {
        CommunicationChannel sourceCommunicationChannel = ev.getSource();
        CommunicationChannel replacementCommunicationChannel = ev.getReplacement();

        switch (ev.getEventPhase()) {
        case VALIDATE:
            final List<Communication> communications = communicationRepository.findByCommunicationChannel(sourceCommunicationChannel);
            if (communications.size() > 0 && replacementCommunicationChannel == null) {
                ev.invalidate("Communication channel is being used in a communication: provide a replacement");
            }
            break;
        case EXECUTING:
            for (Communication comm : communicationRepository.findByCommunicationChannel(sourceCommunicationChannel)) {
                for(CommChannelRole commChannelRole : comm.getCorrespondents()){
                    if(commChannelRole.getChannel().equals(sourceCommunicationChannel)){
                        commChannelRole.setChannel(replacementCommunicationChannel);
                    }
                }
            }
            break;
        default:
            break;
        }
    }

    @Inject
    CommunicationRepository communicationRepository;

}

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
package org.estatio.dom.agreement;

import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import com.google.common.eventbus.Subscribe;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.services.scratchpad.Scratchpad;
import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.communicationchannel.CommunicationChannel;

@DomainService(menuOrder = "25", repositoryFor = AgreementRoleCommunicationChannel.class)
@Hidden
public class AgreementRoleCommunicationChannels extends EstatioDomainService<AgreementRoleCommunicationChannel> {

    public AgreementRoleCommunicationChannels() {
        super(AgreementRoleCommunicationChannels.class, AgreementRoleCommunicationChannel.class);
    }

    @Hidden
    @ActionSemantics(Of.SAFE)
    public AgreementRoleCommunicationChannel findByRoleAndTypeAndContainsDate(
            final AgreementRole role,
            final AgreementRoleCommunicationChannelType type,
            final LocalDate date) {
        return firstMatch("findByRoleAndTypeAndContainsDate",
                "role", role,
                "type", type,
                "date", date);
    }

    @Hidden
    @ActionSemantics(Of.SAFE)
    public List<AgreementRoleCommunicationChannel> findByCommunicationChannel(
            final CommunicationChannel communicationChannel) {
        return allMatches("findByCommunicationChannel",
                "communicationChannel", communicationChannel);
    }

    @Subscribe
    public void on(final CommunicationChannel.RemoveEvent ev) {
        CommunicationChannel sourceCommunicationChannel = (CommunicationChannel) ev.getSource();
        CommunicationChannel replacementCommunicationChannel = ev.getReplacement();

        List<AgreementRoleCommunicationChannel> communicationChannels;
        switch (ev.getEventPhase()) {
        case VALIDATE:
            communicationChannels = findByCommunicationChannel(sourceCommunicationChannel);
            if (communicationChannels.size() > 0 && replacementCommunicationChannel == null) {
                ev.invalidate("Communication channel is being used: provide a replacement");
            } else {
                scratchpad.put(onCommunicationChannelRemoveScratchpadKey = UUID.randomUUID(), communicationChannels);
            }
            break;
        case EXECUTING:
                communicationChannels = (List<AgreementRoleCommunicationChannel>) scratchpad.get(onCommunicationChannelRemoveScratchpadKey);
                for (AgreementRoleCommunicationChannel arcc : communicationChannels) {
                arcc.setCommunicationChannel(replacementCommunicationChannel);
            }
            break;
        default:
            break;
        }
    }

    private transient UUID onCommunicationChannelRemoveScratchpadKey;

    // //////////////////////////////////////




    @Inject
    private Scratchpad scratchpad;
}

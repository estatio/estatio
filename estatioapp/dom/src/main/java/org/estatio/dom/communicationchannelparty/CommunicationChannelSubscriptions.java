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
package org.estatio.dom.communicationchannelparty;

import java.util.List;
import javax.inject.Inject;
import com.google.common.eventbus.Subscribe;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.party.Party;

@DomainService
@Hidden
public class CommunicationChannelSubscriptions extends UdoDomainRepositoryAndFactory<CommunicationChannel> {

    public CommunicationChannelSubscriptions() {
        super(CommunicationChannelSubscriptions.class, CommunicationChannel.class);
    }


    @Subscribe
    @Programmatic
    public void on(final Party.RemoveEvent ev) {
        Party sourceParty = (Party) ev.getSource();
        Party replacementParty = ev.getReplacement();

        switch (ev.getPhase()) {
        case VALIDATE:
            // We don't care if being deleted
            break;
        case EXECUTING:
            for (CommunicationChannel communicationChannel : communicationChannels.findByOwner(sourceParty)) {
                if (replacementParty == null) {
                    communicationChannel.remove(null);
                } else {
                    communicationChannel.setOwner(replacementParty);
                }
            }
            break;
        default:
            break;
        }
    }

    private static final String KEY = CommunicationChannel.class.getName() + ".communicationChannels";

    private static void put(Party.RemoveEvent ev, List<CommunicationChannel> communicationChannels) {
        ev.put(KEY, communicationChannels);
    }

    @SuppressWarnings({ "unchecked" })
    private static List<CommunicationChannel> getCommunicationChannels(Party.RemoveEvent ev) {
        return (List<CommunicationChannel>) ev.get(KEY);
    }


    @Inject
    private CommunicationChannels communicationChannels;

}

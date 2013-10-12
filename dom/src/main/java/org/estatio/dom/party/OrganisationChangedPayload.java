/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.party;

import java.util.Set;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.invoice.Invoice;

import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.services.publish.EventPayloadForObjectChanged;

/**
 * Describes the payload for publishing an {@link Invoice} using Isis'. 
 */
public class OrganisationChangedPayload extends EventPayloadForObjectChanged<Organisation> {

    public OrganisationChangedPayload(
            final Organisation target) {
        super(target);
    }


    @Override
    @Render(Type.EAGERLY)
    public Organisation getChanged() {
        return super.getChanged();
    }
    
    @Render(Type.EAGERLY)
    public Set<CommunicationChannel> getCommunicationChannels() {
        return channelContributions.communicationChannels(getChanged());
    }

    // //////////////////////////////////////
    
    private CommunicationChannelContributions channelContributions;
    
    public void injectChannelContributions(CommunicationChannelContributions channelContributions) {
        this.channelContributions = channelContributions;
    }
}

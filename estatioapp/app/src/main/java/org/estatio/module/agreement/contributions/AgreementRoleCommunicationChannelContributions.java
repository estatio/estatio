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
package org.estatio.module.agreement.contributions;

import java.util.List;

import org.apache.isis.applib.annotation.*;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannel;
import org.estatio.module.agreement.dom.AgreementRoleCommunicationChannelRepository;

@Mixin(method="coll")
public class AgreementRoleCommunicationChannelContributions extends UdoDomainRepositoryAndFactory<AgreementRoleCommunicationChannel> {

    private final CommunicationChannel communicationChannel;

    public AgreementRoleCommunicationChannelContributions(CommunicationChannel communicationChannel) {
        super(AgreementRoleCommunicationChannelContributions.class, AgreementRoleCommunicationChannel.class);
        this.communicationChannel = communicationChannel;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<AgreementRoleCommunicationChannel> coll() {
        return agreementRoleCommunicationChannelRepository.findByCommunicationChannel(communicationChannel);
    }

    @javax.inject.Inject
    private AgreementRoleCommunicationChannelRepository agreementRoleCommunicationChannelRepository;
}

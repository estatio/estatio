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

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.communicationchannel.CommunicationChannel;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = AgreementRoleCommunicationChannel.class
)
public class AgreementRoleCommunicationChannelRepository
        extends UdoDomainRepositoryAndFactory<AgreementRoleCommunicationChannel> {

    public AgreementRoleCommunicationChannelRepository() {
        super(AgreementRoleCommunicationChannelRepository.class, AgreementRoleCommunicationChannel.class);
    }

    public AgreementRoleCommunicationChannel findByRoleAndTypeAndContainsDate(
            final AgreementRole role,
            final AgreementRoleCommunicationChannelType type,
            final LocalDate date) {
        return firstMatch("findByRoleAndTypeAndContainsDate",
                "role", role,
                "type", type,
                "date", date);
    }

    public List<AgreementRoleCommunicationChannel> findByCommunicationChannel(
            final CommunicationChannel communicationChannel) {
        return allMatches("findByCommunicationChannel",
                "communicationChannel", communicationChannel);
    }

    public Iterable<? extends EstatioDomainObject<?>> findByAgreement(final Agreement agreement) {
        return allMatches("findByAgreement",
                "agreement", agreement);
    }


}

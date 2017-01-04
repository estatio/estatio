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
package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

@DomainService(repositoryFor = PhoneOrFaxNumber.class, nature = NatureOfService.DOMAIN)
public class PhoneOrFaxNumberRepository {

    public String getId() {
        return "incodeCommunications.PhoneOrFaxNumberRepository";
    }

    public String iconName() {
        return PhoneOrFaxNumber.class.getSimpleName();
    }


    // //////////////////////////////////////

    @Programmatic
    public PhoneOrFaxNumber findByPhoneOrFaxNumber(
            final CommunicationChannelOwner owner,
            final String phoneNumber) {

        final Optional<PhoneOrFaxNumber> phoneNumberIfFound = findByPhoneOrFaxNumber(owner, phoneNumber, CommunicationChannelType.PHONE_NUMBER);
        if(phoneNumberIfFound.isPresent()) {
            return phoneNumberIfFound.get();
        }

        final Optional<PhoneOrFaxNumber> faxNumberIfFound = findByPhoneOrFaxNumber(owner, phoneNumber, CommunicationChannelType.FAX_NUMBER);
        return faxNumberIfFound.orNull();
    }

    private Optional<PhoneOrFaxNumber> findByPhoneOrFaxNumber(final CommunicationChannelOwner owner, final String phoneNumber, final CommunicationChannelType communicationChannelType) {
        final List<CommunicationChannelOwnerLink> links =
                communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelType(owner, communicationChannelType);
        final Iterable<PhoneOrFaxNumber> phoneOrFaxNumbers =
                Iterables.transform(
                        links,
                        CommunicationChannelOwnerLink.Functions.communicationChannel(PhoneOrFaxNumber.class));
        return Iterables.tryFind(phoneOrFaxNumbers, PhoneOrFaxNumber.Predicates.equalTo(phoneNumber, communicationChannelType));
    }

    // //////////////////////////////////////

    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;


}

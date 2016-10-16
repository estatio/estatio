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
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.State;

@DomainService(repositoryFor = CommunicationChannel.class, nature = NatureOfService.DOMAIN)
public class CommunicationChannelRepository extends UdoDomainRepositoryAndFactory<CommunicationChannel> {

    public String getId() {
        return "estatio.CommunicationChannelRepository";
    }

    public CommunicationChannelRepository() {
        super(CommunicationChannelRepository.class, CommunicationChannel.class);
    }

    @Programmatic
    public PostalAddress newPostal(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String address1,
            final String address2,
            final String address3,
            final String postalCode,
            final String city,
            final State state,
            final Country country
            ) {
        final PostalAddress pa = newTransientInstance(PostalAddress.class);
        pa.setType(type);
        pa.setAddress1(address1);
        pa.setAddress2(address2);
        pa.setAddress3(address3);
        pa.setCity(city);
        pa.setPostalCode(postalCode);
        pa.setState(state);
        pa.setCountry(country);
        pa.setOwner(owner);
        persistIfNotAlready(pa);
        return pa;
    }

    @Programmatic
    public EmailAddress newEmail(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String address) {
        final EmailAddress ea = newTransientInstance(EmailAddress.class);
        ea.setType(type);
        ea.setEmailAddress(address);
        ea.setOwner(owner);
        persistIfNotAlready(ea);
        return ea;
    }

    @Programmatic
    public PhoneOrFaxNumber newPhoneOrFax(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String number) {
        final PhoneOrFaxNumber pn = newTransientInstance(PhoneOrFaxNumber.class);
        pn.setType(type);
        pn.setPhoneNumber(number);
        pn.setOwner(owner);
        persistIfNotAlready(pn);
        return pn;
    }

    @Programmatic
    public CommunicationChannel findByReferenceAndType(
            final String reference, final CommunicationChannelType type) {
        return firstMatch("findByReferenceAndType", "reference", reference, "type", type);
    }

    @Programmatic
    public SortedSet<CommunicationChannel> findByOwner(final CommunicationChannelOwner owner) {
        final List<CommunicationChannelOwnerLink> links = communicationChannelOwnerLinkRepository.findByOwner(owner);
        return Sets.newTreeSet(
                Iterables.transform(links, CommunicationChannelOwnerLink.Functions.communicationChannel()));
    }

    @Programmatic
    public SortedSet<CommunicationChannel> findByOwnerAndType(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type) {
        final List<CommunicationChannelOwnerLink> links =
                communicationChannelOwnerLinkRepository.findByOwnerAndCommunicationChannelType(owner, type);
        return Sets.newTreeSet(Iterables.transform(
                links, CommunicationChannelOwnerLink.Functions.communicationChannel()));
    }

    @Programmatic
    public SortedSet<CommunicationChannel> findOtherByOwnerAndType(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final CommunicationChannel exclude) {
        final SortedSet<CommunicationChannel> communicationChannels = findByOwnerAndType(owner, type);
        communicationChannels.remove(exclude);
        return communicationChannels;
    }

    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;
}

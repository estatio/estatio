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
package org.estatio.dom.communicationchannel;

import java.util.List;
import java.util.SortedSet;
import com.google.common.collect.Sets;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Prototype;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;
//import org.estatio.dom.party.Party;

@DomainService(menuOrder = "70", repositoryFor = CommunicationChannel.class)
@Hidden
public class CommunicationChannels extends UdoDomainRepositoryAndFactory<CommunicationChannel> {

    public CommunicationChannels() {
        super(CommunicationChannels.class, CommunicationChannel.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
    public PostalAddress newPostal(
            // CHECKSTYLE:OFF ParameterNumber - Wicket viewer does not support
            // aggregate value types
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String address1,
            final String address2,
            final String address3,
            final String postalCode,
            final String city,
            final State state,
            final Country country
            // CHECKSTYLE:ON ParameterNumber
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

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
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

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
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

    // //////////////////////////////////////

    @Prototype
    public List<CommunicationChannel> allCommunicationChannels() {
        return allInstances(CommunicationChannel.class);
    }

    // //////////////////////////////////////

    @NotInServiceMenu
    @ActionSemantics(Of.SAFE)
    public CommunicationChannel findByReferenceAndType(
            final String reference, final CommunicationChannelType type) {
        return firstMatch("findByReferenceAndType", "reference", reference, "type", type);
    }

    @Programmatic
    public SortedSet<CommunicationChannel> findByOwner(final CommunicationChannelOwner owner) {
        return Sets.newTreeSet(allMatches("findByOwner", "owner", owner));
    }

    @Programmatic
    public SortedSet<CommunicationChannel> findByOwnerAndType(final CommunicationChannelOwner owner, CommunicationChannelType type) {
        return Sets.newTreeSet(allMatches("findByOwnerAndType", "owner", owner, "type", type));
    }

    @Programmatic
    public SortedSet<CommunicationChannel> findOtherByOwnerAndType(final CommunicationChannelOwner owner, CommunicationChannelType type, CommunicationChannel exclude) {
        return Sets.newTreeSet(allMatches("findOtherByOwnerAndType", "owner", owner, "type", type, "exclude", exclude));
    }


    // //////////////////////////////////////

}

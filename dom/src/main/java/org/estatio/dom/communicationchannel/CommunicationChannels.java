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
package org.estatio.dom.communicationchannel;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;

@Hidden
public class CommunicationChannels extends EstatioDomainService<CommunicationChannel> {

    public CommunicationChannels() {
        super(CommunicationChannels.class, CommunicationChannel.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
    public PostalAddress newPostal(
            final CommunicationChannelOwner owner, 
            final CommunicationChannelType type,
            final String address1, 
            final String address2, 
            final String postalCode, 
            final String city, 
            final State state, 
            final Country country) {
        final PostalAddress pa = newTransientInstance(PostalAddress.class);
        pa.setType(type);
        pa.setAddress1(address1);
        pa.setAddress2(address2);
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
        ea.setAddress(address);
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
        pn.setNumber(number);
        pn.setOwner(owner);
        persistIfNotAlready(pn);
        return pn;
    }


    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @NotContributed
    public CommunicationChannel findByReferenceAndType(String reference, CommunicationChannelType type) {
        return firstMatch("findByReferenceAndType", "reference", reference, "type", type);
    }

}

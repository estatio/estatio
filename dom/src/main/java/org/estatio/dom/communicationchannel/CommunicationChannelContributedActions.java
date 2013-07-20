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

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotInServiceMenu;
import org.apache.isis.applib.annotation.Optional;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.State;

@Hidden
public class CommunicationChannelContributedActions extends EstatioDomainService<CommunicationChannel> {

    public CommunicationChannelContributedActions() {
        super(CommunicationChannelContributedActions.class, CommunicationChannel.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="CommunicationChannels", sequence="1")
    @NotInServiceMenu
    public CommunicationChannelOwner newPostal(
            final @Named("Owner") CommunicationChannelOwner owner, 
            final @Named("Type") CommunicationChannelType type,
            final @Named("Address Line 1") String address1, 
            final @Named("Address Line 2") @Optional String address2, 
            final @Named("Postal Code") String postalCode, 
            final @Named("City") String city, 
            final State state, 
            final Country country) {
        communicationChannels.newPostal(owner, type, address1, address2, postalCode, city, state, country);
        return owner;
    }

    public List<CommunicationChannelType> choices1NewPostal() {
        return CommunicationChannelType.matching(PostalAddress.class);
    }
    public CommunicationChannelType default1NewPostal() {
        return choices1NewPhoneOrFax().get(0);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="CommunicationChannels", sequence="2")
    @NotInServiceMenu
    public CommunicationChannelOwner newEmail(
            final @Named("Owner") CommunicationChannelOwner owner,
            final @Named("Type") CommunicationChannelType type,
            final @Named("Address") String address) {
        communicationChannels.newEmail(owner, type, address);
        return owner;
    }

    public List<CommunicationChannelType> choices1NewEmail() {
        return CommunicationChannelType.matching(EmailAddress.class);
    }
    public CommunicationChannelType default1NewEmail() {
        return choices1NewPhoneOrFax().get(0);
    }
    public String validateNewEmail(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String address) {
        // TODO: validate email address format
        return null; 
    }

    // //////////////////////////////////////

    @Named("New Phone/Fax")
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(name="CommunicationChannels", sequence="3")
    @NotInServiceMenu
    public CommunicationChannelOwner newPhoneOrFax(
            final @Named("Owner") CommunicationChannelOwner owner,
            final @Named("Type") CommunicationChannelType type,
            final @Named("Number") String number) {
        communicationChannels.newPhoneOrFax(owner, type, number);
        return owner;
    }
    public List<CommunicationChannelType> choices1NewPhoneOrFax() {
        return CommunicationChannelType.matching(PhoneOrFaxNumber.class);
    }
    public CommunicationChannelType default1NewPhoneOrFax() {
        return choices1NewPhoneOrFax().get(0);
    }
    public String validateNewPhoneOrFax(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String number) {
        // TODO: validate phone number format
        return null; 
    }

    
    // //////////////////////////////////////

    private CommunicationChannels communicationChannels;
    public void injectCommunicationChannels(CommunicationChannels communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

}

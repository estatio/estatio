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

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.geography.Country;
import org.estatio.dom.geography.CountryRepository;
import org.estatio.dom.geography.State;
import org.estatio.dom.geography.StateRepository;

/**
 * Domain service that contributes actions to create a new
 * {@link #newPostal(CommunicationChannelOwner, CommunicationChannelType, Country, State, String, String, String, String, String)
 * postal address},
 * {@link #newEmail(CommunicationChannelOwner, CommunicationChannelType, String)
 * email} or
 * {@link #newPhoneOrFax(CommunicationChannelOwner, CommunicationChannelType, String)
 * phone/fax}, and contributes a collection to list the
 * {@link #communicationChannels(CommunicationChannelOwner) communication
 * channels} of a particular {@link CommunicationChannelOwner}.
 */
public abstract class CommunicationChannelContributions extends UdoDomainService<CommunicationChannelContributions> {

    public CommunicationChannelContributions() {
        super(CommunicationChannelContributions.class);
    }

    protected CommunicationChannelContributions(Class<? extends UdoDomainService<CommunicationChannelContributions>> serviceType) {
        super(serviceType);
    }

    // //////////////////////////////////////

    @MemberOrder(name = "CommunicationChannels", sequence = "1")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    // CHECKSTYLE.OFF: ParameterNumber - Wicket viewer does not support
    // aggregate value types
    public CommunicationChannelOwner newPostal(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final Country country,
            final @Parameter(optionality = Optionality.OPTIONAL) State state,
            final String addressLine1,
            final @Parameter(optionality = Optionality.OPTIONAL) String addressLine2,
            final @Parameter(optionality = Optionality.OPTIONAL) String addressLine3,
            final String postalCode,
            final String city
    ) {
        communicationChannelRepository.newPostal(owner, type, addressLine1, addressLine2, null, postalCode, city, state, country);
        return owner;
    }

    // CHECKSTYLE.ON: ParameterNumber

    public List<CommunicationChannelType> choices1NewPostal() {
        return CommunicationChannelType.matching(PostalAddress.class);
    }

    public CommunicationChannelType default1NewPostal() {
        return choices1NewPostal().get(0);
    }

    public Country default2NewPostal() {
        return countryRepository.allCountries().get(0);
    }

    public List<State> choices3NewPostal(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final Country country) {
        return stateRepository.findStatesByCountry(country);
    }

    public State default3NewPostal() {
        final Country country = default2NewPostal();
        final List<State> statesInCountry = stateRepository.findStatesByCountry(country);
        return statesInCountry.size() > 0 ? statesInCountry.get(0) : null;
    }

    // //////////////////////////////////////

    @MemberOrder(name = "CommunicationChannels", sequence = "2")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public CommunicationChannelOwner newEmail(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String address) {
        communicationChannelRepository.newEmail(owner, type, address);
        return owner;
    }

    public List<CommunicationChannelType> choices1NewEmail() {
        return CommunicationChannelType.matching(EmailAddress.class);
    }

    public CommunicationChannelType default1NewEmail() {
        return choices1NewEmail().get(0);
    }

    public String validateNewEmail(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String address) {
        // TODO: validate email address format
        return null;
    }

    // //////////////////////////////////////

    @MemberOrder(name = "CommunicationChannels", sequence = "3")
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(named = "New Phone/Fax", contributed = Contributed.AS_ACTION)
    public CommunicationChannelOwner newPhoneOrFax(
            final CommunicationChannelOwner owner,
            final CommunicationChannelType type,
            final String number) {
        communicationChannelRepository.newPhoneOrFax(owner, type, number);
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

    @Action(semantics = SemanticsOf.SAFE)
    @CollectionLayout(render = RenderType.EAGERLY)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public SortedSet<CommunicationChannel> communicationChannels(final CommunicationChannelOwner owner) {
        return communicationChannelRepository.findByOwner(owner);
    }

    // //////////////////////////////////////

    @Inject
    public CommunicationChannelRepository communicationChannelRepository;

    @Inject
    private StateRepository stateRepository;

    @Inject
    private CountryRepository countryRepository;

}

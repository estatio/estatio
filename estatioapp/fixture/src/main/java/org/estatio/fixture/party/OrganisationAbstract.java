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
package org.estatio.fixture.party;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner_newChannelContributions;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.country.dom.impl.Country;
import org.incode.module.country.dom.impl.CountryRepository;
import org.incode.module.country.dom.impl.State;
import org.incode.module.country.dom.impl.StateRepository;
import org.estatio.dom.party.OrganisationRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PersonRepository;

/**
 * Sets up the {@link org.estatio.dom.party.Organisation} and also a number of
 * {@link CommunicationChannel}s.
 */
public abstract class OrganisationAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected Party createOrganisation(
            String atPath,
            String partyReference,
            String partyName,
            String address1,
            String address2,
            String postalCode,
            String city,
            String stateReference,
            String countryReference,
            String phone,
            String fax,
            String emailAddress,
            ExecutionContext executionContext) {

        ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);

        Party party = organisationRepository.newOrganisation(partyReference, false, partyName, applicationTenancy);

        createCommunicationChannels(party, address1, address2, postalCode, city, stateReference, countryReference, phone, fax, emailAddress, executionContext);

        return executionContext.addResult(this, party.getReference(), party);
    }

    protected Party createCommunicationChannels(
            Party party,
            String address1,
            String address2,
            String postalCode,
            String city,
            String stateReference,
            String countryReference,
            String phone,
            String fax,
            String emailAddress,
            ExecutionContext executionContext) {

        if (address1 != null) {
            final Country country = countryRepository.findCountry(countryReference);
            final State state = stateRepository.findState(stateReference);
            communicationChannelContributedActions.newPostal(
                    party,
                    CommunicationChannelType.POSTAL_ADDRESS,
                    country,
                    state,
                    address1,
                    address2,
                    null,
                    postalCode,
                    city);
            getContainer().flush();
        }
        if (phone != null) {
            communicationChannelContributedActions.newPhoneOrFax(
                    party,
                    CommunicationChannelType.PHONE_NUMBER,
                    phone);
            getContainer().flush();
        }
        if (fax != null) {
            communicationChannelContributedActions.newPhoneOrFax(
                    party,
                    CommunicationChannelType.FAX_NUMBER,
                    fax);
            getContainer().flush();
        }
        if (emailAddress != null) {
            communicationChannelContributedActions.newEmail(
                    party,
                    CommunicationChannelType.EMAIL_ADDRESS,
                    emailAddress);
            getContainer().flush();
        }

        return executionContext.addResult(this, party.getReference(), party);
    }

    protected boolean defined(String[] values, int i) {
        return values.length > i && !values[i].isEmpty();
    }

    // //////////////////////////////////////

    @Inject
    protected CountryRepository countryRepository;

    @Inject
    protected StateRepository stateRepository;

    @Inject
    protected OrganisationRepository organisationRepository;

    @Inject
    protected PersonRepository personRepository;

    @Inject
    protected CommunicationChannelOwner_newChannelContributions communicationChannelContributedActions;

    @Inject
    protected ApplicationTenancies applicationTenancies;

}

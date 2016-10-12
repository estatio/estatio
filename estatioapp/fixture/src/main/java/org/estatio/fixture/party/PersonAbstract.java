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

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner_newChannelContributions;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.estatio.dom.geography.CountryRepository;
import org.estatio.dom.geography.StateRepository;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.dom.party.PersonRepository;
import org.estatio.dom.party.relationship.PartyRelationshipRepository;

public abstract class PersonAbstract extends FixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected Party createPerson(
            final String atPath,
            final String reference,
            final String initials,
            final String firstName,
            final String lastName,
            final PersonGenderType gender,
            final ExecutionContext executionContext) {

        ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);

        Party party = personRepository.newPerson(reference, initials, firstName, lastName, gender, applicationTenancy);
        return executionContext.addResult(this, party.getReference(), party);
    }

    protected Party createPerson(
            final String atPath,
            final String reference,
            final String initials,
            final String firstName,
            final String lastName,
            final PersonGenderType gender,
            final String phoneNumber,
            final String emailAddress,
            final String fromPartyStr,
            final String relationshipType,
            final ExecutionContext executionContext) {

        ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(atPath);
        // new person
        Party party = personRepository.newPerson(reference, initials, firstName, lastName, gender, applicationTenancy);
        if(emailAddress != null) {
            communicationChannelContributedActions
                    .newEmail(party, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);
        }
        if(phoneNumber != null) {
            communicationChannelContributedActions
                    .newPhoneOrFax(party, CommunicationChannelType.PHONE_NUMBER, phoneNumber);
        }
        // associate person
        Party from = partyRepository.findPartyByReference(fromPartyStr);
        partyRelationshipRepository.newRelationship(from, party, relationshipType, null);
        return executionContext.addResult(this, party.getReference(), party);
    }


    // //////////////////////////////////////

    @Inject
    protected CountryRepository countryRepository;

    @Inject
    protected StateRepository stateRepository;

    @Inject
    protected PartyRepository partyRepository;

    @Inject
    protected PersonRepository personRepository;

    @Inject
    protected CommunicationChannelOwner_newChannelContributions communicationChannelContributedActions;

    @Inject
    protected PartyRelationshipRepository partyRelationshipRepository;

    @Inject
    protected ApplicationTenancies applicationTenancies;

}

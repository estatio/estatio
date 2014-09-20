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

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.States;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Persons;
import org.estatio.dom.party.relationship.PartyRelationshipType;
import org.estatio.dom.party.relationship.PartyRelationships;
import org.estatio.fixture.EstatioFixtureScript;

public abstract class PersonAbstract extends EstatioFixtureScript {

    @Override
    protected abstract void execute(ExecutionContext executionContext);

    protected Party createPerson(
            final String reference,
            final String initials,
            final String firstName,
            final String lastName,
            final ExecutionContext executionContext) {
        Party party = persons.newPerson(reference, initials, firstName, lastName);
        return executionContext.add(this, party.getReference(), party);
    }

    protected Party createPerson(
            final String reference,
            final String initials,
            final String firstName,
            final String lastName,
            final String phoneNumber,
            final String emailAddress,
            final String fromPartyStr,
            final String relationshipType,
            final ExecutionContext executionContext) {
        // new person
        Party party = persons.newPerson(reference, initials, firstName, lastName);
        communicationChannelContributedActions.newEmail(party, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);
        communicationChannelContributedActions.newPhoneOrFax(party, CommunicationChannelType.PHONE_NUMBER, phoneNumber);
        // associate person
        Party from = parties.findPartyByReference(fromPartyStr);
        partyRelationships.newRelationship(from, party, relationshipType);
        return executionContext.add(this, party.getReference(), party);
    }
    
    public List<String> choices7CreatePerson() {
        return Collections.emptyList(); // TODO: return list of choices for argument N
    }

    // //////////////////////////////////////

    @Inject
    protected Countries countries;

    @Inject
    protected States states;

    @Inject
    protected Parties parties;

    @Inject
    protected Persons persons;

    @Inject
    protected CommunicationChannelContributions communicationChannelContributedActions;

    @Inject
    protected PartyRelationships partyRelationships;

}

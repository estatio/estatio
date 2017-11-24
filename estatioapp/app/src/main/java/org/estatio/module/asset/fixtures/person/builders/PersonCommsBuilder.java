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
package org.estatio.module.asset.fixtures.person.builders;

import javax.inject.Inject;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner_newChannelContributions;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.module.base.platform.fixturesupport.BuilderScriptAbstract;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.dom.relationship.PartyRelationshipRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class PersonCommsBuilder
        extends BuilderScriptAbstract<PersonCommsBuilder> {

    @Getter @Setter
    private Person person;

    @Getter @Setter
    private String phoneNumber;

    @Getter @Setter
    private String emailAddress;

    @Override
    public void execute(ExecutionContext executionContext) {

        checkParam("person", executionContext, Person.class);

        if(emailAddress != null) {
            communicationChannelContributedActions
                    .newEmail(person, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);
        }
        if(phoneNumber != null) {
            communicationChannelContributedActions
                    .newPhoneOrFax(person, CommunicationChannelType.PHONE_NUMBER, phoneNumber);
        }

    }

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


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
import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.States;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Persons;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class PersonAbstract extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {
        createPerson("JDOE", "J", "John", "Doe", executionContext);
        createPerson("LTORVALDS", "L", "Linus", "Torvalds", executionContext);
    }

    protected Party createPerson(String reference, String initials, String firstName, String lastName, ExecutionContext executionContext) {
        Party party = persons.newPerson(reference, initials, firstName, lastName);
        return executionContext.add(this, party.getReference(), party);
    }


    protected boolean defined(String[] values, int i) {
        return values.length>i && !values[i].isEmpty();
    }

    // //////////////////////////////////////

    @Inject
    protected Countries countries;

    @Inject
    protected States states;

    @Inject
    protected Organisations organisations;

    @Inject
    protected Persons persons;

    @Inject
    protected CommunicationChannelContributions communicationChannelContributedActions;

}

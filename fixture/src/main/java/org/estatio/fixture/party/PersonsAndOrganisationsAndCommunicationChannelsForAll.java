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

import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

public class PersonsAndOrganisationsAndCommunicationChannelsForAll extends CompositeFixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {
        execute(new OrganisationAndCommunicationChannelsForAcme(), executionContext);
        execute(new OrganisationAndCommunicationChannelsForHelloWorld(), executionContext);
        execute(new OrganisationAndCommunicationChannelsForTopModel(), executionContext);
        execute(new OrganisationAndCommunicationChannelsForMediaX(), executionContext);
        execute(new OrganisationAndCommunicationChannelsForPoison(), executionContext);
        execute(new OrganisationAndCommunicationChannelsForPret(), executionContext);
        execute(new OrganisationAndCommunicationChannelsForMiracle(), executionContext);
        execute(new PersonForJohnDoe(), executionContext);
        execute(new PersonForLinusTorvalds(), executionContext);
    }
}

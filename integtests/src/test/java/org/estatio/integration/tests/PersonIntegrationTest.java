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
package org.estatio.integration.tests;

import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Person;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;

public class PersonIntegrationTest extends AbstractEstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        world.install(new EstatioTransactionalObjectsFixture());
    }

    @Test
    public void cannotModifyName() throws Exception {
        Person party = wrap((Person)world.service(Parties.class).findParties("Doe, Jo*").get(0));
        
        expectedExceptions.expectMessage("Cannot be updated directly; derived from first and last names");
        party.setName("Cannot change name directly");
    }

}

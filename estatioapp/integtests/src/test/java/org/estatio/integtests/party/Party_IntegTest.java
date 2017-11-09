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
package org.estatio.integtests.party;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;

import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.party.OrganisationForAcmeNl;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.fixture.party.PersonForGinoVannelliGb;
import org.estatio.fixture.party.PersonForJohnDoeNl;
import org.estatio.fixture.security.users.EstatioAdmin;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.junit.Assert.assertNull;

public class Party_IntegTest extends EstatioIntegrationTest {

    @Inject
    PartyRepository partyRepository;

    @Inject
    SudoService sudoService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class Remove extends Party_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    // linked together:
                    executionContext.executeChild(this, new OrganisationForTopModelGb());
                    executionContext.executeChild(this, new PersonForGinoVannelliGb());
                    // only relationship
                    executionContext.executeChild(this, new PersonForJohnDoeNl());
                    // only comm channels
                    executionContext.executeChild(this, new OrganisationForAcmeNl());
                }
            });
        }

        @Test
        public void happyCase() {
            Party party = partyRepository.findPartyByReference(PersonForJohnDoeNl.REF);
            // WHen
            sudoService.sudo(EstatioAdmin.USER_NAME, Lists.newArrayList(EstatioRole.SUPERUSER.getRoleName()),
                    new Runnable() {
                        @Override public void run() {
                            wrap(party).delete(null);
                        }
                    });

            assertNull(partyRepository.findPartyByReferenceOrNull(PersonForJohnDoeNl.REF));
        }

    }

}
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
package org.estatio.module.asset.integtests.party;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.sudo.SudoService;

import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.integtests.AssetModuleIntegTestAbstract;
import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.fixtures.security.users.personas.EstatioAdmin;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.junit.Assert.assertNull;

public class Party_IntegTest extends AssetModuleIntegTestAbstract {

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
                    // linked together:
                    executionContext.executeChild(this, OrganisationAndComms_enum.TopModelGb.builder());
                    executionContext.executeChild(this, Person_enum.GinoVannelliGb.builder());
                    // only relationship
                    executionContext.executeChild(this, Person_enum.JohnDoeNl.builder());
                    // only comm channels
                    executionContext.executeChild(this, OrganisationAndComms_enum.AcmeNl.builder());
                }
            });
        }

        @Test
        public void happyCase() {
            Party party = partyRepository.findPartyByReference(Person_enum.JohnDoeNl.getRef());
            // WHen
            sudoService.sudo(EstatioAdmin.USER_NAME, Lists.newArrayList(EstatioRole.SUPERUSER.getRoleName()),
                    () -> {
                        wrap(party).delete(null);
                    });

            assertNull(partyRepository.findPartyByReferenceOrNull(Person_enum.JohnDoeNl.getRef()));
        }

    }

}
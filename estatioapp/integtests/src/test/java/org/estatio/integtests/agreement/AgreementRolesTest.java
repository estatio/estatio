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
package org.estatio.integtests.agreement;

import java.util.List;
import javax.inject.Inject;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent.Phase;
import org.apache.isis.applib.services.wrapper.InvalidException;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Party.RemoveEvent;
import org.estatio.dom.party.Persons;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AgreementRolesTest extends EstatioIntegrationTest {

    @Inject
    AgreementRoles agreementRoles;

    @Inject
    Parties parties;

    @Inject
    Persons persons;

    @Inject
    Organisations organisations;

    @Inject
    Agreements agreements;

    @Inject
    AgreementTypes agreementTypes;

    @Inject
    AgreementRoleTypes agreementRoleTypes;

    @Inject
    ApplicationTenancies applicationTenancies;


    Party party;
    Agreement agreement;
    AgreementType agreementType;
    AgreementRoleType agreementRoleType;

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());
                executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
            }
        });
    }

    @Before
    public void setUp() throws Exception {
        party = parties.findPartyByReference(_LeaseForOxfTopModel001Gb.PARTY_REF_TENANT);
        agreement = agreements.findAgreementByReference(_LeaseForOxfTopModel001Gb.REF);
        agreementType = agreementTypes.find(LeaseConstants.AT_LEASE);
        agreementRoleType = agreementRoleTypes.findByAgreementTypeAndTitle(agreementType, LeaseConstants.ART_TENANT);

    }

    public static class FindByParty extends AgreementRolesTest {

        @Test
        public void findByParty() throws Exception {
            List<AgreementRole> results = agreementRoles.findByParty(party);
            AgreementRole result = results.get(0);
            assertThat(results.size(), is(1));
        }
    }

    public static class FindByPartyAndTypeAndContainsDate extends AgreementRolesTest {

        @Test
        public void happyCase() throws Exception {
            assertThat(finder(agreement.getStartDate()).size(), is(1));
            assertThat(finder(agreement.getEndDate()).size(), is(1));
        }

        @Test
        public void sadCase() throws Exception {
            assertThat(finder(agreement.getStartDate().minusDays(10)).size(), is(0));
            assertThat(finder(agreement.getEndDate().plusDays(10)).size(), is(0));
        }

        private List<AgreementRole> finder(LocalDate date) {
            for (AgreementRole role : agreementRoles.findByParty(party)) {
                role.setStartDate(agreement.getStartDate());
                role.setEndDate(agreement.getEndDate());
            }
            return agreementRoles.findByPartyAndTypeAndContainsDate(party, agreementRoleType, date);
        }

    }

    public static class findByAgreementAndPartyAndTypeAndContainsDate extends AgreementRolesTest {

        @Test
        public void happyCase() throws Exception {
            assertNotNull(agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(agreement, party, agreementRoleType, agreement.getStartDate()));
            assertNotNull(agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(agreement, party, agreementRoleType, agreement.getEndDate()));
        }

    }

    public static class findByAgreementAndPartyAndContainsDate extends AgreementRolesTest {

        @Test
        public void happyCase() throws Exception {
            assertNotNull(agreementRoles.findByAgreementAndTypeAndContainsDate(agreement, agreementRoleType, agreement.getStartDate()));
            assertNotNull(agreementRoles.findByAgreementAndTypeAndContainsDate(agreement, agreementRoleType, agreement.getEndDate()));
        }

    }

    public static class OnPartyRemove extends AgreementRolesTest {

        Party oldParty;
        Party newParty;

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Before
        public void setUpData() throws Exception {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new _LeaseForOxfTopModel001Gb());
                }
            });

        }

        @Before
        public void setUp() throws Exception {
            oldParty = parties.findPartyByReference(OrganisationForTopModelGb.REF);
            // EST-467: shouldn't be using global here.
            ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath("/");
            newParty = organisations.newOrganisation("TEST", "Test", applicationTenancy);
        }

        @Test
        public void invalidBecauseNoReplacement() throws Exception {
            // when
            Party.RemoveEvent event = new RemoveEvent(oldParty, null, (Object[]) null);
            event.setEventPhase(Phase.VALIDATE);
            agreementRoles.on(event);

            // then
            assertTrue(event.isInvalid());
        }

        @Test
        public void executingReplacesParty() throws Exception {
            // when
            Party.RemoveEvent event = new RemoveEvent(oldParty, null, newParty);
            event.setEventPhase(Phase.VALIDATE);
            agreementRoles.on(event);
            event.setEventPhase(Phase.EXECUTING);
            agreementRoles.on(event);

            // then
            assertThat(agreementRoles.findByParty(oldParty).size(), is(0));
            assertThat(agreementRoles.findByParty(newParty).size(), is(1));
        }

        @Test
        public void whenVetoingSubscriber() {
            // then
            expectedException.expect(InvalidException.class);

            // when
            wrap(oldParty).remove();
        }
    }

}

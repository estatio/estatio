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

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRepository;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleRepository;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypeRepository;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Persons;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.services.clock.ClockService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AgreementRoleRepositoryTest extends EstatioIntegrationTest {

    @Inject
    AgreementRoleRepository agreementRoleRepository;

    @Inject
    Parties parties;

    @Inject
    Persons persons;

    @Inject
    Organisations organisations;

    @Inject
    AgreementRepository agreementRepository;

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

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
                executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
            }
        });
    }

    @Before
    public void setUp() throws Exception {
        party = parties.findPartyByReference(LeaseForOxfTopModel001Gb.PARTY_REF_TENANT);
        agreement = agreementRepository.findAgreementByReference(LeaseForOxfTopModel001Gb.REF);
        agreementType = agreementTypeRepository.find(LeaseConstants.AT_LEASE);
        agreementRoleType = agreementRoleTypeRepository.findByAgreementTypeAndTitle(agreementType, LeaseConstants.ART_TENANT);

    }

    public static class FindByAgreementAndPartyAndTypeAndContainsDate2 extends AgreementRoleRepositoryTest {

        private AgreementRoleType artTenant;
        private Lease leaseOxfTopModel;
        private Party partyTopModel;

        @Before
        public void setupData() {
            runScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseForOxfTopModel001Gb());
                }
            });
        }

        @Inject
        private Leases leases;
        @Inject
        private Parties parties;
        @Inject
        private ClockService clockService;

        @Before
        public void setUp() throws Exception {
            artTenant = agreementRoleTypeRepository.findByTitle(LeaseConstants.ART_TENANT);
            leaseOxfTopModel = leases.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            partyTopModel = parties.findPartyByReference(OrganisationForTopModelGb.REF);
        }

        @Test
        public void happyCase() throws Exception {

            // given
            final LocalDate date = clockService.now();

            // when
            AgreementRole role = agreementRoleRepository.findByAgreementAndPartyAndTypeAndContainsDate(leaseOxfTopModel,
                    partyTopModel, artTenant, date);

            // then
            Assert.assertNotNull(role);
        }

    }

    public static class FindByParty extends AgreementRoleRepositoryTest {

        @Test
        public void findByParty() throws Exception {
            List<AgreementRole> results = agreementRoleRepository.findByParty(party);
            AgreementRole result = results.get(0);
            assertThat(results.size(), is(1));
        }
    }

    public static class FindByPartyAndTypeAndContainsDate extends AgreementRoleRepositoryTest {

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
            for (AgreementRole role : agreementRoleRepository.findByParty(party)) {
                role.setStartDate(agreement.getStartDate());
                role.setEndDate(agreement.getEndDate());
            }
            return agreementRoleRepository.findByPartyAndTypeAndContainsDate(party, agreementRoleType, date);
        }

    }

    public static class FindByAgreementAndPartyAndTypeAndContainsDate extends AgreementRoleRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            assertNotNull(agreementRoleRepository
                    .findByAgreementAndPartyAndTypeAndContainsDate(agreement, party, agreementRoleType,
                            agreement.getStartDate()));
            assertNotNull(agreementRoleRepository
                    .findByAgreementAndPartyAndTypeAndContainsDate(agreement, party, agreementRoleType,
                            agreement.getEndDate()));
        }

    }

    public static class FindByAgreementAndPartyAndContainsDate extends AgreementRoleRepositoryTest {

        @Test
        public void happyCase() throws Exception {
            assertNotNull(agreementRoleRepository
                    .findByAgreementAndTypeAndContainsDate(agreement, agreementRoleType, agreement.getStartDate()));
            assertNotNull(agreementRoleRepository
                    .findByAgreementAndTypeAndContainsDate(agreement, agreementRoleType, agreement.getEndDate()));
        }

    }


}

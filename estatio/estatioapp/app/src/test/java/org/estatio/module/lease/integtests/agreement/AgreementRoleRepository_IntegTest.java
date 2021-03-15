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
package org.estatio.module.lease.integtests.agreement;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseAgreementTypeEnum;
import org.estatio.module.lease.dom.amendments.LeaseAmendment;
import org.estatio.module.lease.dom.occupancy.salesarea.SalesAreaLicense;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.OrganisationRepository;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AgreementRoleRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    AgreementRoleRepository agreementRoleRepository;

    @Inject
    PartyRepository partyRepository;

    @Inject
    PersonRepository personRepository;

    @Inject
    OrganisationRepository organisationRepository;

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
                executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
            }
        });
    }

    @Before
    public void setUp() throws Exception {
        party = Lease_enum.OxfTopModel001Gb.getTenant_d().findUsing(serviceRegistry);
        agreementType = agreementTypeRepository.find(LeaseAgreementTypeEnum.LEASE.getTitle());
        agreement = agreementRepository.findAgreementByTypeAndReference(agreementType,
                Lease_enum.OxfTopModel001Gb.getRef());
        agreementRoleType = agreementRoleTypeRepository.findByAgreementTypeAndTitle(agreementType, LeaseAgreementRoleTypeEnum.TENANT.getTitle());

    }

    public static class FindByAgreementAndPartyAndTypeAndContainsDate2 extends AgreementRoleRepository_IntegTest {

        private AgreementRoleType artTenant;
        private Lease leaseOxfTopModel;
        private Party partyTopModel;

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, Lease_enum.OxfTopModel001Gb.builder());
                }
            });
        }

        @Inject
        ClockService clockService;

        @Before
        public void setUp() throws Exception {
            artTenant = agreementRoleTypeRepository.findByTitle(LeaseAgreementRoleTypeEnum.TENANT.getTitle());
            leaseOxfTopModel = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            partyTopModel = Organisation_enum.TopModelGb.findUsing(serviceRegistry);
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

    public static class FindByParty extends AgreementRoleRepository_IntegTest {

        @Test
        public void findByParty() throws Exception {
            List<AgreementRole> results = agreementRoleRepository.findByParty(party);
            assertThat(results.size(), is(3)); //TODO: change due to adding amendment to fixture; maybe change this test ...?
            Assertions.assertThat(results.stream().filter(x->x.getAgreement().getClass().isAssignableFrom(
                    SalesAreaLicense.class)).collect(
                    Collectors.toList())).hasSize(1);
            Assertions.assertThat(results.stream().filter(x->x.getAgreement().getClass().isAssignableFrom(
                    LeaseAmendment.class)).collect(
                    Collectors.toList())).hasSize(1);
            Assertions.assertThat(results.stream().filter(x->x.getAgreement().getClass().isAssignableFrom(
                    Lease.class)).collect(
                    Collectors.toList())).hasSize(1);
        }
    }

    public static class FindByPartyAndTypeAndContainsDate extends AgreementRoleRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            assertThat(finder(agreement.getStartDate()).size(), is(3)); //TODO: change due to adding amendment to fixture; maybe change this test ...?
            assertThat(finder(agreement.getEndDate()).size(), is(3)); //TODO: change due to adding amendment to fixture; maybe change this test ...?
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

    public static class FindByAgreementAndPartyAndTypeAndContainsDate extends AgreementRoleRepository_IntegTest {

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

    public static class FindByAgreementAndPartyAndContainsDate extends AgreementRoleRepository_IntegTest {

        @Test
        public void happyCase() throws Exception {
            assertNotNull(agreementRoleRepository
                    .findByAgreementAndTypeAndContainsDate(agreement, agreementRoleType, agreement.getStartDate()));
            assertNotNull(agreementRoleRepository
                    .findByAgreementAndTypeAndContainsDate(agreement, agreementRoleType, agreement.getEndDate()));
        }

    }

}

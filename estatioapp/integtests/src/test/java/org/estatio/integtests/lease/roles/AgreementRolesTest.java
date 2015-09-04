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
package org.estatio.integtests.lease.roles;

import javax.inject.Inject;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.fixture.party.OrganisationForTopModelGb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.services.clock.ClockService;

public class AgreementRolesTest extends EstatioIntegrationTest {

    public static class FindByAgreementAndPartyAndTypeAndContainsDate extends AgreementRolesTest {

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
        private AgreementRoleTypes agreementRoleTypes;
        @Inject
        private AgreementRoles agreementRoles;
        @Inject
        private ClockService clockService;

        @Before
        public void setUp() throws Exception {
            artTenant = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
            leaseOxfTopModel = leases.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
            partyTopModel = parties.findPartyByReference(OrganisationForTopModelGb.REF);
        }

        @Test
        public void happyCase() throws Exception {

            // given
            final LocalDate date = clockService.now();

            // when
            AgreementRole role = agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(leaseOxfTopModel, partyTopModel, artTenant, date);

            // then
            Assert.assertNotNull(role);
        }

    }
}
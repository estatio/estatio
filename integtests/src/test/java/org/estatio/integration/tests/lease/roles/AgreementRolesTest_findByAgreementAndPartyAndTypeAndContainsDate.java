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
package org.estatio.integration.tests.lease.roles;

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
import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

public class AgreementRolesTest_findByAgreementAndPartyAndTypeAndContainsDate extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupData() {
        scenarioExecution().install(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsFixture(), executionContext);
                execute("properties", new PropertiesAndUnitsFixture(), executionContext);
                execute("leases", new LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture(), executionContext);
            }
        });
    }

    private Leases leases;
    private Parties parties;
    private AgreementRoleTypes agreementRoleTypes;

    private AgreementRoles agreementRoles;

    @Before
    public void setup() {
        leases = service(Leases.class);
        parties = service(Parties.class);
        agreementRoleTypes = service(AgreementRoleTypes.class);

        agreementRoles = service(AgreementRoles.class);
    }


    @Test
    public void findByAgreementAndPartyAndTypeAndContainsDate() throws Exception {
        // given lease has tenant role
        AgreementRoleType artTenant = agreementRoleTypes.findByTitle(LeaseConstants.ART_TENANT);
        Lease leaseTopModel = leases.findLeaseByReference("OXF-TOPMODEL-001");
        Party party = parties.findPartyByReference("TOPMODEL");

        // TODO: need to fix this date
        final LocalDate date = LocalDate.now();

        // when
        AgreementRole role = agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(leaseTopModel, party, artTenant, date);

        // then
        Assert.assertNotNull(role);
    }

}

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
package org.estatio.integtests.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

public class LeasesTest_renew extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute(new LeaseForOxfTopModel001(), executionContext);
            }
        });
    }

    @Inject
    private Leases leases;

    @Inject
    private AgreementRoles agreementRoles;

    @Inject
    private AgreementRoleTypes agreementRoleTypes;

    @Test
    public void renew() {
        Lease lease = leases.allLeases().get(0);
        String newReference = lease.default0Renew() + "-2";
        String newName = lease.default1Renew() + "-2";
        LocalDate newStartDate = lease.default2Renew();
        LocalDate newEndDate = new LocalDate(2030, 12, 31);
        Lease newLease = lease.renew(
                newReference,
                newName,
                newStartDate,
                newEndDate,
                true);

        // Old lease
        assertThat(lease.getTenancyEndDate(), is(newStartDate.minusDays(1)));
        
        //
        assertThat(newLease.getOccupancies().size(),  is(1));

        // New lease
        assertThat(newLease.getStartDate(), is(newStartDate));
        assertThat(newLease.getEndDate(), is(newEndDate));
        assertThat(newLease.getTenancyStartDate(), is(newStartDate));
        assertThat(newLease.getTenancyEndDate(), is(newEndDate));

        //

        assertThat(agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(lease, lease.getSecondaryParty(), agreementRoleTypes.findByTitle("Tenant"), lease.getStartDate()).getCommunicationChannels().size(), is(2));
//        assertThat(agreementRoles.findByAgreementAndPartyAndTypeAndContainsDate(lease, lease.getSecondaryParty(), agreementRoleTypes.findByTitle("Tenant"), lease.getStartDate()).getCommunicationChannels().size(), is(2));

    }

}

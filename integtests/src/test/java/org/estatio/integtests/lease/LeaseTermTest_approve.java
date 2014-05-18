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

import javax.inject.Inject;
import org.estatio.dom.lease.*;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsFixture;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LeaseTermTest_approve extends EstatioIntegrationTest {

    @Before
    public void setupData() {
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

    @Inject
    private Leases leases;

    private Lease lease;
    private LeaseItem leaseTopModelRentItem;

    @Before
    public void setup() {
        lease = leases.findLeaseByReference("OXF-TOPMODEL-001");
        leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, dt(2010, 7, 15), bi(1));
        assertNotNull(leaseTopModelRentItem);
    }

    @Test
    public void happyCase() throws Exception {

        // given
        lease.verifyUntil(dt(2014, 1, 1));

        LeaseTerm term0 = leaseTopModelRentItem.findTerm(dt(2010, 7, 15));
        LeaseTerm term2 = leaseTopModelRentItem.findTerm(dt(2012, 7, 15));
        assertThat(term2, is(not(sameInstance(term0))));

        assertThat(term0.getStatus(), is(LeaseTermStatus.NEW));
        assertThat(term2.getStatus(), is(LeaseTermStatus.NEW));

        // when
        term0.approve();

        // then only the term that is approved has a changed status
        assertThat(term0.getStatus(), is(LeaseTermStatus.APPROVED));
        assertThat(term2.getStatus(), is(LeaseTermStatus.NEW));
    }

}

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
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertiesAndUnitsForAll;
import org.estatio.fixture.lease.LeasesEtcForAll;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsForAll;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LeaseTest_verifyUntil extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new CompositeFixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);
                execute("parties", new PersonsAndOrganisationsAndCommunicationChannelsForAll(), executionContext);
                execute("properties", new PropertiesAndUnitsForAll(), executionContext);
                execute("leases", new LeasesEtcForAll(), executionContext);
            }
        });
    }

    @Inject
    private Leases leases;

    private Lease leaseTopModel;
    private LeaseItem leaseTopModelRentItem;
    private LeaseItem leaseTopModelServiceChargeItem;

    @Before
    public void setUp() throws Exception {
        leaseTopModel = leases.findLeaseByReference("OXF-TOPMODEL-001");

        leaseTopModelRentItem = leaseTopModel.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
        assertNotNull(leaseTopModelRentItem);

        leaseTopModelServiceChargeItem = leaseTopModel.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
        assertNotNull(leaseTopModelServiceChargeItem);
    }

    /**
     * Compare to tests that verify at the {@link org.estatio.dom.lease.LeaseTerm} level.
     *
     * @see LeaseTermTest_verifyUntil#givenLeaseTermForIndexableRent()
     * @see LeaseTermTest_verifyUntil#givenLeaseTermForServiceCharge()
     */
    @Test
    public void createsTermsForLeaseTermItems() throws Exception {

        // given
        assertNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
        assertNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));

        // when
        leaseTopModel.verifyUntil(VT.ld(2014, 1, 1));

        // then
        assertNotNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
        assertNotNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));
    }

}

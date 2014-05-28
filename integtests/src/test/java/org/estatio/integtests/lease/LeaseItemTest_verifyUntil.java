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
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.lease.*;
import org.estatio.fixture.party.*;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LeaseItemTest_verifyUntil extends EstatioIntegrationTest {



    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new PersonForJohnDoe(), executionContext);
                execute(new PersonForLinusTorvalds(), executionContext);

                execute(new OrganisationForHelloWorld(), executionContext);
                execute(new PropertyForOxf(), executionContext);

                execute(new OrganisationForAcme(), executionContext);
                execute(new PropertyForKal(), executionContext);

                execute(new OrganisationForTopModel(), executionContext);
                execute(new LeaseBreakOptionsForOxfTopModel001(), executionContext);

                execute(new OrganisationForMediaX(), executionContext);
                execute(new LeaseBreakOptionsForOxfMediax002(), executionContext);

                execute(new OrganisationForPoison(), executionContext);
                execute(new LeaseBreakOptionsForOxfPoison003(), executionContext);
                execute(new LeaseItemAndTermsForKalPoison001(), executionContext);

                execute(new OrganisationForPret(), executionContext);
                execute(new LeaseForOxfPret004(), executionContext);

                execute(new OrganisationForMiracle(), executionContext);
                execute(new LeaseItemAndTermsForOxfMiracl005(), executionContext);
            }
        });
    }

    @Inject
    private Leases leases;

    private Lease leaseTopModel;

    private LeaseItem leaseTopModelServiceChargeItem;
    private LeaseItem leaseTopModelRentItem;

    @Before
    public void setUp() throws Exception {
        leaseTopModel = leases.findLeaseByReference("OXF-TOPMODEL-001");

        leaseTopModelServiceChargeItem = leaseTopModel.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
        assertNotNull(leaseTopModelServiceChargeItem);

        leaseTopModelRentItem = leaseTopModel.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
        assertNotNull(leaseTopModelRentItem);
    }

    @Test
    public void givenServiceChargeItem_thenCreatesTermsForThatItemOnly() throws Exception {

        // given
        assertNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
        assertNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));

        // when
        leaseTopModelServiceChargeItem.verify();

        // then
        assertNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
        assertNotNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));
    }

    @Test
    public void givenIndexableRentItem_thenCreatesTermsForThatItemOnly() throws Exception {

        // given
        assertNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
        assertNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));

        // when
        leaseTopModelRentItem.verify();

        // then
        assertNotNull(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15)));
        assertNull(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));
    }

}

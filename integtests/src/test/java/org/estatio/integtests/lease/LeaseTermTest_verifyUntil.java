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

import java.math.BigDecimal;
import java.util.SortedSet;
import javax.inject.Inject;
import org.estatio.dom.lease.*;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKal;
import org.estatio.fixture.asset.PropertyForOxf;
import org.estatio.fixture.lease.*;
import org.estatio.fixture.party.*;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LeaseTermTest_verifyUntil extends EstatioIntegrationTest {

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
                execute(new LeasesEtcForOxfTopModel001(), executionContext);

                execute(new OrganisationForMediaX(), executionContext);
                execute(new LeasesEtcForOxfMediax002(), executionContext);

                execute(new OrganisationForPoison(), executionContext);
                execute(new LeasesEtcForOxfPoison003(), executionContext);
                execute(new LeasesEtcForKalPoison001(), executionContext);

                execute(new OrganisationForPret(), executionContext);
                execute(new LeasesEtcForOxfPret004(), executionContext);

                execute(new OrganisationForMiracle(), executionContext);
                execute(new LeasesEtcForOxfMiracl005(), executionContext);
            }
        });
    }

    @Inject
    private Leases leases;

    private Lease lease;


    @Before
    public void setup() {
        lease = leases.findLeaseByReference("OXF-TOPMODEL-001");
        assertThat(lease.getItems().size(), is(3));
    }

    @Test
    public void givenLeaseTermForIndexableRent() throws Exception {

        // given
        LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
        Assert.assertNotNull(leaseTopModelRentItem);

        assertThat(leaseTopModelRentItem.getTerms().size(), is(1));
        LeaseTermForIndexableRent leaseTopModelRentTerm1 = (LeaseTermForIndexableRent) leaseTopModelRentItem.getTerms().first();
        LeaseTermForIndexableRent leaseTopModelRentTerm = (LeaseTermForIndexableRent) leaseTopModelRentItem.findTerm(VT.ld(2010, 7, 15));
        Assert.assertThat(leaseTopModelRentTerm, is(sameInstance(leaseTopModelRentTerm1)));

        // when
        leaseTopModelRentTerm1.verifyUntil(VT.ld(2014, 1, 1));

        // then
        assertThat(leaseTopModelRentTerm1.getBaseIndexValue(), is(BigDecimal.valueOf(137.6).setScale(4)));
        assertThat(leaseTopModelRentTerm1.getNextIndexValue(), is(VT.bd4(101.2)));
        assertThat(leaseTopModelRentTerm1.getIndexationPercentage(), is(VT.bd1(1)));
        assertThat(leaseTopModelRentTerm1.getIndexedValue(), is(VT.bd2(20200)));
    }

    @Test
    public void givenLeaseTermForServiceCharge() throws Exception {
        // given
        LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
        Assert.assertNotNull(leaseTopModelServiceChargeItem);

        assertThat(leaseTopModelServiceChargeItem.getTerms().size(), is(1));
        final LeaseTerm leaseTopModelServiceChargeTerm1 = leaseTopModelServiceChargeItem.getTerms().first();
        LeaseTerm leaseTopModelServiceChargeTerm = leaseTopModelServiceChargeItem.findTerm(VT.ld(2010, 7, 15));
        Assert.assertThat(leaseTopModelServiceChargeTerm1, is(sameInstance(leaseTopModelServiceChargeTerm)));

        // when
        leaseTopModelServiceChargeTerm1.verifyUntil(VT.ld(2014, 1, 1));

        // then
        SortedSet<LeaseTerm> terms = leaseTopModelServiceChargeItem.getTerms();
        assertNotNull(terms.toString(), leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15)));
    }



}

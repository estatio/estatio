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
import org.estatio.fixture.asset.PropertiesAndUnitsFixture;
import org.estatio.fixture.lease.LeasesAndLeaseUnitsAndLeaseItemsAndLeaseTermsAndTagsAndBreakOptionsFixture;
import org.estatio.fixture.party.PersonsAndOrganisationsAndCommunicationChannelsFixture;
import org.estatio.integtests.EstatioIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LeaseTermTest_verifyUntil extends EstatioIntegrationTest {

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


    @Before
    public void setup() {
        lease = leases.findLeaseByReference("OXF-TOPMODEL-001");
        assertThat(lease.getItems().size(), is(3));
    }

    @Test
    public void givenLeaseTermForIndexableRent() throws Exception {

        // given
        LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, dt(2010, 7, 15), bi(1));
        Assert.assertNotNull(leaseTopModelRentItem);

        assertThat(leaseTopModelRentItem.getTerms().size(), is(1));
        LeaseTermForIndexableRent leaseTopModelRentTerm1 = (LeaseTermForIndexableRent) leaseTopModelRentItem.getTerms().first();
        LeaseTermForIndexableRent leaseTopModelRentTerm = (LeaseTermForIndexableRent) leaseTopModelRentItem.findTerm(dt(2010, 7, 15));
        Assert.assertThat(leaseTopModelRentTerm, is(sameInstance(leaseTopModelRentTerm1)));

        // when
        leaseTopModelRentTerm1.verifyUntil(dt(2014, 1, 1));

        // then
        assertThat(leaseTopModelRentTerm1.getBaseIndexValue(), is(BigDecimal.valueOf(137.6).setScale(4)));
        assertThat(leaseTopModelRentTerm1.getNextIndexValue(), is(bd4(101.2)));
        assertThat(leaseTopModelRentTerm1.getIndexationPercentage(), is(bd1(1)));
        assertThat(leaseTopModelRentTerm1.getIndexedValue(), is(bd2(20200)));
    }

    @Test
    public void givenLeaseTermForServiceCharge() throws Exception {
        // given
        LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, dt(2010, 7, 15), bi(1));
        Assert.assertNotNull(leaseTopModelServiceChargeItem);

        assertThat(leaseTopModelServiceChargeItem.getTerms().size(), is(1));
        final LeaseTerm leaseTopModelServiceChargeTerm1 = leaseTopModelServiceChargeItem.getTerms().first();
        LeaseTerm leaseTopModelServiceChargeTerm = leaseTopModelServiceChargeItem.findTerm(dt(2010, 7, 15));
        Assert.assertThat(leaseTopModelServiceChargeTerm1, is(sameInstance(leaseTopModelServiceChargeTerm)));

        // when
        leaseTopModelServiceChargeTerm1.verifyUntil(dt(2014, 1, 1));

        // then
        SortedSet<LeaseTerm> terms = leaseTopModelServiceChargeItem.getTerms();
        assertNotNull(terms.toString(), leaseTopModelServiceChargeItem.findTerm(dt(2012, 7, 15)));
    }



}

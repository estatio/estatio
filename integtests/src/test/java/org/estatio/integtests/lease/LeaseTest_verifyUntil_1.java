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

import java.util.SortedSet;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LeaseTest_verifyUntil_1 extends EstatioIntegrationTest {

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

    @Test
    public void happyCase1() throws Exception {
        // TODO: what is the variation being tested here ?

        // given
        Lease leaseMediax = leases.findLeaseByReference("OXF-MEDIAX-002");

        LeaseItem leaseMediaXServiceChargeItem = leaseMediax.findItem(LeaseItemType.SERVICE_CHARGE, dt(2008, 1, 1), bi(1));
        LeaseTerm leaseMediaXServiceChargeTerm = leaseMediaXServiceChargeItem.findTerm(dt(2008, 1, 1));
        assertNotNull(leaseMediaXServiceChargeTerm);

        // when
        leaseMediax.verifyUntil(dt(2014, 1, 1));

        // commit to get the BigDecimals to be stored to the correct precision by DN.
        nextTransaction();

        // and reload
        leaseMediax = leases.findLeaseByReference("OXF-MEDIAX-002");
        leaseMediaXServiceChargeItem = leaseMediax.findItem(LeaseItemType.SERVICE_CHARGE, dt(2008, 1, 1), bi(1));

        // then
        leaseMediaXServiceChargeTerm = leaseMediaXServiceChargeItem.findTerm(dt(2008, 1, 1));
        assertNotNull(leaseMediaXServiceChargeTerm);

        final LeaseTerm leaseMediaXServiceChargeTermN = leaseMediaXServiceChargeItem.getTerms().last();
        assertThat(leaseMediaXServiceChargeTermN.getEffectiveValue(), is(bd("6000.00")));
    }

    @Test
    public void happyCase2() throws Exception {
        // TODO: what is the variation being tested here ?

        // given
        Lease leasePoison = leases.findLeaseByReference("OXF-POISON-003");

        LeaseItem leasePoisonRentItem = leasePoison.findItem(LeaseItemType.RENT, dt(2011, 1, 1), bi(1));
        LeaseItem leasePoisonServiceChargeItem = leasePoison.findItem(LeaseItemType.SERVICE_CHARGE, dt(2011, 1, 1), bi(1));
        assertNotNull(leasePoisonServiceChargeItem);

        // when
        leasePoison.verifyUntil(dt(2014, 1, 1));

        // commit to get the BigDecimals to be stored to the correct precision by DN; reload
        nextTransaction();
        leasePoison = leases.findLeaseByReference("OXF-POISON-003");
        leasePoisonRentItem = leasePoison.findItem(LeaseItemType.RENT, dt(2011, 1, 1), bi(1));
        leasePoisonServiceChargeItem = leasePoison.findItem(LeaseItemType.SERVICE_CHARGE, dt(2011, 1, 1), bi(1));

        // then
        final LeaseTerm leaseTerm1 = leasePoisonServiceChargeItem.findTerm(dt(2011, 1, 1));
        assertNotNull(leaseTerm1);

        final LeaseTerm leaseTerm2 = leasePoisonServiceChargeItem.getTerms().last();
        assertThat(leaseTerm2.getEffectiveValue(), is(bd("12400.00")));

        // and then
        SortedSet<LeaseTerm> terms = leasePoisonRentItem.getTerms();

        assertThat(
                leasePoisonServiceChargeItem.getEffectiveInterval().toString()
                .concat(terms.toString()), 
                terms.size(), is(3));
        assertNotNull(leasePoisonRentItem.findTerm(dt(2011, 1, 1)));
    }

}

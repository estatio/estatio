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
import org.estatio.integtests.VT;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.CompositeFixtureScript;

import static org.hamcrest.CoreMatchers.is;

public class LeaseItemTest_getTerms extends EstatioIntegrationTest {

    private Lease lease;

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

    @Before
    public void setUp() throws Exception {
        lease = leases.findLeaseByReference("OXF-TOPMODEL-001");
    }

    @Test
    public void whenExists_andFirstIsIndexableRent() throws Exception {
        // this is just really asserting on the fixture

        // given
        LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));

        // when
        final SortedSet<LeaseTerm> terms = leaseTopModelRentItem.getTerms();

        // then
        Assert.assertThat(terms.size(), is(1));
        final LeaseTerm term0 = terms.first();

        LeaseTermForIndexableRent indexableRent = assertType(term0, LeaseTermForIndexableRent.class);

        Assert.assertNotNull(indexableRent.getFrequency());
        Assert.assertNotNull(indexableRent.getFrequency().nextDate(VT.ld(2012, 1, 1)));

        BigDecimal baseValue = indexableRent.getBaseValue();
        Assert.assertEquals(VT.bd("20000.00"), baseValue);
    }

    @Test
    public void whenExists_andFirstIsLeaseTermForServiceChargeBudgetAuditLineItem() throws Exception {
        // this is just really asserting on the fixture

        LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));

        final SortedSet<LeaseTerm> terms = leaseTopModelServiceChargeItem.getTerms();
        Assert.assertThat(terms.size(), Is.is(1));
        final LeaseTerm term0 = terms.first();

        LeaseTermForServiceCharge leaseTopModelServiceChargeTerm = assertType(term0, LeaseTermForServiceCharge.class);
        Assert.assertThat(leaseTopModelServiceChargeTerm.getBudgetedValue(), Is.is(VT.bd("6000.00")));
    }

}

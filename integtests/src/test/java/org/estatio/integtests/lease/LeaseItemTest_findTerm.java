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
import org.estatio.fixture.lease.LeaseForOxfTopModel001;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LeaseItemTest_findTerm extends EstatioIntegrationTest {

    private Lease lease;

    @Before
    public void setupData() {
        runScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new EstatioBaseLineFixture(), executionContext);

                execute(new LeaseItemAndTermsForOxfTopModel001(), executionContext);
            }
        });
    }

    @Inject
    private Leases leases;

    @Before
    public void setUp() throws Exception {
        lease = leases.findLeaseByReference(LeaseForOxfTopModel001.LEASE_REFERENCE);
    }

    @Test
    public void whenExists_forRent() throws Exception {

        // this is mostly just asserting on the baseline fixture

        // given
        LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));

        // and given
        final SortedSet<LeaseTerm> terms = leaseTopModelRentItem.getTerms();
        Assert.assertThat(terms.size(), is(1));
        final LeaseTerm term0 = terms.first();

        // when
        final LeaseTerm term = leaseTopModelRentItem.findTerm(VT.ld(2010, 7, 15));
        LeaseTermForIndexableRent leaseTopModelRentTerm = assertType(term, LeaseTermForIndexableRent.class);

        // then
        Assert.assertNotNull(leaseTopModelRentTerm);
        assertThat(leaseTopModelRentTerm, is(term0));

        // and then
        Assert.assertNotNull(leaseTopModelRentTerm.getFrequency());
        Assert.assertNotNull(leaseTopModelRentTerm.getFrequency().nextDate(VT.ld(2012, 1, 1)));

        BigDecimal baseValue = leaseTopModelRentTerm.getBaseValue();
        Assert.assertEquals(VT.bd("20000.00"), baseValue);
    }

    @Test
    public void whenExists_forServiceCharge() throws Exception {

        // given
        LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));

        // when
        LeaseTermForServiceCharge leaseTopModelServiceChargeTerm = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.findTerm(VT.ld(2010, 7, 15));

        // then
        assertThat(leaseTopModelServiceChargeTerm.getBudgetedValue(), Is.is(VT.bd("6000.00")));
    }


}

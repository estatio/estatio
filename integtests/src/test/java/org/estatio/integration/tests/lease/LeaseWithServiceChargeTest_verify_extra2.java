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
package org.estatio.integration.tests.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioTransactionalObjectsFixture;
import org.estatio.integration.tests.EstatioIntegrationTest;

public class LeaseWithServiceChargeTest_verify_extra2 extends EstatioIntegrationTest {

    @BeforeClass
    public static void setupTransactionalData() {
        scenarioExecution().install(new EstatioTransactionalObjectsFixture());
    }

    private Leases leases;

    private Lease leasePoison;
    private LeaseItem leasePoisonRentItem;
    private LeaseItem leasePoisonServiceChargeItem;

    @Before
    public void setup() {
        leases = service(Leases.class);

        leasePoison = leases.findLeaseByReference("OXF-POISON-003");
        leasePoisonRentItem = leasePoison.findItem(LeaseItemType.RENT, new LocalDate(2011, 1, 1), BigInteger.valueOf(1));
        leasePoisonServiceChargeItem = leasePoison.findItem(LeaseItemType.SERVICE_CHARGE, new LocalDate(2011, 1, 1), BigInteger.valueOf(1));
    }

    @Test
    public void happyCase() throws Exception {
        // REVIEW: what is the variation being tested here (compared to similar
        // tests with leaseTopModel) ?

        // when
        leasePoison.verifyUntil(new LocalDate(2014, 1, 1));

        // then
        final LeaseTerm leaseTerm1 = leasePoisonServiceChargeItem.findTerm(new LocalDate(2011, 1, 1));
        assertNotNull(leaseTerm1);

        final LeaseTerm leaseTerm2 = leasePoisonServiceChargeItem.getTerms().last();
        assertThat(leaseTerm2.getEffectiveValue(), is(BigDecimal.valueOf(12400).setScale(2)));

        // and then
        SortedSet<LeaseTerm> terms = leasePoisonRentItem.getTerms();
        List<LeaseTerm> temp = new ArrayList<LeaseTerm>(terms);
        temp.toString();
        
        assertThat(
                leasePoisonServiceChargeItem.getEffectiveInterval().toString()
                .concat(terms.toString()), 
                terms.size(), is(3));
        assertNotNull(leasePoisonRentItem.findTerm(new LocalDate(2011, 1, 1)));
        
    }

}

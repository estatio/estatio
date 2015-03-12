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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseItems;
import org.estatio.dom.lease.LeaseTermForTax;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForRentForOxfTopModel001Gb;
import org.estatio.fixture.lease.LeaseItemAndLeaseTermForTaxForOxfTopModel001Gb;
import org.estatio.fixture.lease._LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;

public class LeaseTermForTaxTest extends EstatioIntegrationTest {

    @Inject
    Leases leases;

    @Inject
    LeaseItems leaseItems;

    public static class RentValueForDate extends LeaseTermForTaxTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentForOxfTopModel001Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForTaxForOxfTopModel001Gb());
                }
            });
        }

        private Lease lease;
        private LeaseItem item, taxItem;

        @Before
        public void setup() {
            lease = leases.findLeaseByReference(_LeaseForOxfTopModel001Gb.REF);
            item = leaseItems.findLeaseItemsByType(lease, LeaseItemType.RENT).get(0);
            taxItem = leaseItems.findLeaseItemsByType(lease, LeaseItemType.TAX).get(0);
            assertNotNull(item);
            assertNotNull(item.getStartDate());
            assertNotNull(item.getEndDate());
        }

        @Test
        public void singleRentItem() throws Exception {
            // Given, when
            lease.verifyUntil(new LocalDate(2014, 1, 1));
            final LeaseTermForTax taxTerm = (LeaseTermForTax) taxItem.findTerm(new LocalDate(2012, 7, 15));
            // Then
            assertThat(taxTerm.rentValueForDate(), is(new BigDecimal("20846.40")));
            assertThat(taxTerm.getTaxableValue(), is(new BigDecimal("20846.40")));
        }

        @Test
        public void twoRentItems() throws Exception {
            // Given
            lease.verifyUntil(new LocalDate(2014, 1, 1));
            final LeaseTermForTax taxTerm = (LeaseTermForTax) taxItem.findTerm(new LocalDate(2012, 7, 15));
            // When
            item.copy(new LocalDate(2012, 7, 15), item.getInvoicingFrequency(), item.getPaymentMethod(), item.getCharge());
            lease.verifyUntil(new LocalDate(2014, 1, 1));
            // Then
            assertThat(taxTerm.rentValueForDate(), is(new BigDecimal("20846.40")));
            assertThat(taxTerm.getTaxableValue(), is(new BigDecimal("20846.40")));
        }
    }
}

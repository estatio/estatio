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
package org.estatio.module.lease.integtests.lease;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemRepository;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForTax;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LeaseTermForTax_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    LeaseMenu leaseMenu;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseItemRepository leaseItemRepository;

    public static class RentValueForDate extends LeaseTermForTax_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {

                    executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                    executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
                }
            });
        }

        private Lease lease;
        private LeaseItem item, taxItem;

        @Before
        public void setup() {
            lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            item = leaseItemRepository.findLeaseItemsByType(lease, LeaseItemType.RENT).get(0);
            taxItem = leaseItemRepository.findLeaseItemsByType(lease, LeaseItemType.TAX).get(0);
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
            assertThat(taxItem.getSourceItems().size(), is(1));
            assertThat(taxTerm.rentValueForDate(), is(new BigDecimal("20846.40")));
            assertThat(taxTerm.getTaxableValue(), is(new BigDecimal("20846.40")));
        }

        @Test
        public void twoRentItems() throws Exception {
            // Given
            lease.verifyUntil(new LocalDate(2014, 1, 1));
            final LeaseTermForTax taxTerm = (LeaseTermForTax) taxItem.findTerm(new LocalDate(2012, 7, 15));
            // When
            LeaseItem secondRentItem = item.copy(new LocalDate(2012, 7, 15), item.getInvoicingFrequency(), item.getPaymentMethod(), item.getCharge());
            taxItem.newSourceItem(secondRentItem);
            lease.verifyUntil(new LocalDate(2014, 1, 1));
            // Then
            assertThat(taxItem.getSourceItems().size(), is(2));
            assertThat(taxTerm.rentValueForDate(), is(new BigDecimal("20846.40")));
            assertThat(taxTerm.getTaxableValue(), is(new BigDecimal("20846.40")));
        }
    }
}

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

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.index.dom.Index;
import org.estatio.module.index.dom.IndexRepository;
import org.estatio.module.index.fixtures.enums.Index_enum;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForIndexableRepository;
import org.estatio.module.lease.fixtures.leaseitems.deposits.personas.LeaseItemAndLeaseTermForDepositForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.discount.personas.LeaseItemAndLeaseTermForDiscountForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.entryfee.personas.LeaseItemAndLeaseTermForEntryFeeForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.marketing.personas.LeaseItemAndLeaseTermForMarketingForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.percentage.personas.LeaseItemAndLeaseTermForPercentageForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.rent.personas.LeaseItemAndLeaseTermForRentForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.servicecharge.personas.LeaseItemAndLeaseTermForServiceChargeForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.svcchgbudgeted.personas.LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.tax.personas.LeaseItemAndLeaseTermForTaxForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.turnoverrent.personas.LeaseItemAndLeaseTermForTurnoverRentForOxfTopModel001Gb;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeaseTermsForIndexable_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForServiceChargeForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemForServiceChargeBudgetedForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForTurnoverRentForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForPercentageForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForDiscountForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForEntryFeeForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForTaxForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForDepositForOxfTopModel001Gb());
                executionContext.executeChild(this, new LeaseItemAndLeaseTermForMarketingForOxfTopModel001Gb());

            }
        });
    }

    @Inject
    IndexRepository indexes;

    @Inject
    LeaseTermForIndexableRepository leaseTerms;

    @Before
    public void setUp() throws Exception {
    }

    public static class FindByIndexAndDate extends LeaseTermsForIndexable_IntegTest {

        Index index;

        @Before
        public void setUp() throws Exception {
            index = Index_enum.IStatFoi.findUsing(serviceRegistry);
        }

        @Test
        public void findByIndexAndDate() throws Exception {
            // Given
            List<LeaseTermForIndexable> results = leaseTerms.findByIndexAndDate(index, new LocalDate(2011, 1, 1));
            assertThat(results.size(), is(1));
            // When
            results.get(0).verifyUntil(new LocalDate(2014, 1, 1));
            // Then
            assertThat(leaseTerms.findByIndexAndDate(index, new LocalDate(2011, 1, 1)).size(), is(2));
        }

    }

}
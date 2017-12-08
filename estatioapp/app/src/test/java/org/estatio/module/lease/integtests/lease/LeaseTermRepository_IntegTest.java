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
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.base.integtests.VT;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForTax;
import org.estatio.module.lease.dom.LeaseTermRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.deposits.personas.LeaseItemAndLeaseTermForDepositForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.discount.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.entryfee.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.marketing.personas.LeaseItemAndLeaseTermForMarketingForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.percentage.enums.LeaseItemForPercentage_enum;
import org.estatio.module.lease.fixtures.leaseitems.rent.personas.LeaseItemAndLeaseTermForRentForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.servicecharge.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.svcchgbudgeted.enums.LeaseItemForServiceChargeBudgeted_enum;
import org.estatio.module.lease.fixtures.leaseitems.tax.personas.LeaseItemAndLeaseTermForTaxForOxfTopModel001Gb;
import org.estatio.module.lease.fixtures.leaseitems.turnoverrent.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.hamcrest.CoreMatchers.is;
import static org.incode.module.unittestsupport.dom.assertions.Asserting.assertType;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeaseTermRepository_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {


                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA.builder());
                executionContext.executeChild(this, LeaseItemForServiceChargeBudgeted_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForPercentage_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForEntryFee_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForMarketing_enum.OxfTopModel001Gb.builder());

            }
        });
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseTermRepository leaseTermRepository;

    Lease lease;

    @Before
    public void setUp() throws Exception {
        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
    }

    public static class AllLeaseTermRepository extends LeaseTermRepository_IntegTest {

        @Test
        public void whenExists() throws Exception {

            // when
            List<LeaseTerm> allLeaseTerms = leaseTermRepository.allLeaseTerms();

            // then
            Assert.assertThat(allLeaseTerms.isEmpty(), is(false));
            LeaseTerm term = allLeaseTerms.get(0);

            // and then
            Assert.assertNotNull(term.getFrequency());
            Assert.assertNotNull(term.getFrequency().nextDate(VT.ld(2012, 1, 1)));

            final LeaseTermForIndexable indexableRent = assertType(term, LeaseTermForIndexable.class);
            BigDecimal baseValue = indexableRent.getBaseValue();
            Assert.assertEquals(VT.bd("20000.00"), baseValue);
        }

    }

    public static class FindByPropertyAndTypeAndStartDate extends LeaseTermRepository_IntegTest {

        @Test
        public void findByPropertyAndTypeAndStartDate() throws Exception {
            Property property = lease.getProperty();
            List<LeaseTerm> results = leaseTermRepository.findByPropertyAndTypeAndStartDate(property, LeaseItemType.RENT, lease.getStartDate());
            assertThat(results.size(), is(1));
            assertThat(results.get(0), is(lease.getItems().first().getTerms().first()));
        }
    }

    public static class FindStartDatesByPropertyAndType extends LeaseTermRepository_IntegTest {

        @Test
        public void findStartDatesByPropertyAndType() throws Exception {
            Property property = lease.getProperty();
            List<LocalDate> results = leaseTermRepository.findStartDatesByPropertyAndType(property, LeaseItemType.RENT);
            assertThat(results.size(), is(1));
            assertThat(results.get(0), is(lease.getItems().first().getTerms().first().getStartDate()));
        }
    }

    public static class NewLeaseTermWithMandatoryEndDate extends LeaseTermRepository_IntegTest {

        @Test
        public void newLeaseTermWithMandatoryEndDateTest() {
            // given
            LeaseItem taxItem = lease.findFirstItemOfType(LeaseItemType.TAX);
            // when
            LeaseTermForTax taxTerm = (LeaseTermForTax) taxItem.newTerm(new LocalDate(2016, 01, 01), null);
            // then
            Assertions.assertThat(taxTerm.getEndDate()).isEqualTo(new LocalDate(2016, 12, 31));
        }

    }
}
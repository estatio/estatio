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

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.integtests.VT;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.LeaseTermForTax;
import org.estatio.module.lease.dom.LeaseTermRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForMarketing_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForPercentage_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceChargeBudgeted_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.incode.module.unittestsupport.dom.assertions.Asserting.assertType;

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
            assertThat(allLeaseTerms.isEmpty()).isFalse();
            LeaseTerm term = allLeaseTerms.get(0);

            // and then
            assertThat(term.getFrequency()).isNotNull();
            assertThat(term.getFrequency().nextDate(VT.ld(2012, 1, 1))).isNotNull();

            final LeaseTermForIndexable indexableRent = assertType(term, LeaseTermForIndexable.class);
            BigDecimal baseValue = indexableRent.getBaseValue();
            assertThat(baseValue).isEqualTo(VT.bd("20000.00"));
        }

    }

    public static class FindByPropertyAndTypeAndStartDate extends LeaseTermRepository_IntegTest {

        @Test
        public void findByPropertyAndTypeAndStartDate() throws Exception {
            Property property = lease.getProperty();
            List<LeaseTerm> results = leaseTermRepository.findByPropertyAndTypeAndStartDate(property, LeaseItemType.RENT, lease.getStartDate());
            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0)).isEqualTo(lease.getItems().first().getTerms().first());
        }
    }

    public static class FindStartDatesByPropertyAndType extends LeaseTermRepository_IntegTest {

        @Test
        public void findStartDatesByPropertyAndType() throws Exception {
            Property property = lease.getProperty();
            List<LocalDate> results = leaseTermRepository.findStartDatesByPropertyAndType(property, LeaseItemType.RENT);
            assertThat(results.size()).isEqualTo(1);
            assertThat(results.get(0)).isEqualTo(lease.getItems().first().getStartDate());
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
            assertThat(taxTerm.getEndDate()).isEqualTo(new LocalDate(2016, 12, 31));
        }

    }

    public static class FindOrCreateWithStartDate extends LeaseTermRepository_IntegTest {

        @Test
        public void find_or_create_with_startDate_works_when_not_found() {

            // given
            LocalDate startDate = new LocalDate(2017, 01,01);
            LocalDate endDate = new LocalDate(2017,12, 31);
            LeaseItem serviceChargeItem = LeaseItemForServiceCharge_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            assertThat(serviceChargeItem.getTerms().size()).isEqualTo(1);

            // when
            LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseTermRepository.findOrCreateWithStartDate(serviceChargeItem, new LocalDateInterval(startDate, endDate));

            // then
            assertThat(serviceChargeItem.getTerms().size()).isEqualTo(2);
            assertThat(leaseTerm.getStartDate()).isEqualTo(startDate);
            assertThat(leaseTerm.getEndDate()).isEqualTo(endDate);
            assertThat(leaseTerm.getPrevious().getEndDate()).isEqualTo(startDate.minusDays(1));

        }

        @Test
        public void find_or_create_with_startDate_works_when_found() {

            // given
            LocalDate startDate = new LocalDate(2010, 7,15);
            LocalDate endDate = new LocalDate(2010,12, 31);
            LeaseItem serviceChargeItem = LeaseItemForServiceCharge_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            assertThat(serviceChargeItem.getTerms().size()).isEqualTo(1);
            assertThat(serviceChargeItem.getTerms().first().getStartDate()).isEqualTo(startDate);
            assertThat(serviceChargeItem.getTerms().first().getEndDate()).isNull();

            // when
            LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseTermRepository.findOrCreateWithStartDate(serviceChargeItem, new LocalDateInterval(startDate, endDate));

            // then
            assertThat(serviceChargeItem.getTerms().size()).isEqualTo(1);
            assertThat(leaseTerm.getStartDate()).isEqualTo(startDate);
            assertThat(serviceChargeItem.getTerms().first().getEndDate()).isNull();

        }

    }
}
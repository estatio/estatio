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
import org.estatio.dom.asset.Property;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexable;
import org.estatio.dom.lease.LeaseTermForTax;
import org.estatio.dom.lease.LeaseTermRepository;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfTopModel001;
import org.estatio.fixture.lease.LeaseForOxfTopModel001Gb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LeaseTermsTest extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                executionContext.executeChild(this, new EstatioBaseLineFixture());

                executionContext.executeChild(this, new LeaseItemAndTermsForOxfTopModel001());
            }
        });
    }

    @Inject
    Leases leases;

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseTermRepository leaseTermRepository;

    Lease lease;

    @Before
    public void setUp() throws Exception {
        lease = leaseRepository.findLeaseByReference(LeaseForOxfTopModel001Gb.REF);
    }

    public static class AllLeaseTerms extends LeaseTermsTest {

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

    public static class FindByPropertyAndTypeAndStartDate extends LeaseTermsTest {

        @Test
        public void findByPropertyAndTypeAndStartDate() throws Exception {
            Property property = lease.getProperty();
            List<LeaseTerm> results = leaseTermRepository.findByPropertyAndTypeAndStartDate(property, LeaseItemType.RENT, lease.getStartDate());
            assertThat(results.size(), is(1));
            assertThat(results.get(0), is(lease.getItems().first().getTerms().first()));
        }
    }

    public static class FindStartDatesByPropertyAndType extends LeaseTermsTest {

        @Test
        public void findStartDatesByPropertyAndType() throws Exception {
            Property property = lease.getProperty();
            List<LocalDate> results = leaseTermRepository.findStartDatesByPropertyAndType(property, LeaseItemType.RENT);
            assertThat(results.size(), is(1));
            assertThat(results.get(0), is(lease.getItems().first().getTerms().first().getStartDate()));
        }
    }

    public static class NewLeaseTermWithMandatoryEndDate extends LeaseTermsTest {

        @Test
        public void newLeaseTermWithMandatoryEndDateTest() {
            // given
            LeaseItem taxItem = lease.findFirstItemOfType(LeaseItemType.TAX);
            // when
            LeaseTermForTax taxTerm = (LeaseTermForTax) taxItem.newTerm(new LocalDate(2016,01,01), null);
            // then
            Assertions.assertThat(taxTerm.getEndDate()).isEqualTo(new LocalDate(2016,12,31));
        }

    }
}
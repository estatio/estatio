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
package org.estatio.dom.lease;

import java.math.BigDecimal;

import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester;
import org.estatio.dom.index.Index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

public class LeaseTermForServiceChargeTest {

    Lease lease;
    LeaseItem item;
    LeaseTermForServiceCharge term;

    Index index;

    @Mock
    LeaseTermRepository mockLeaseTermRepository;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        lease = new Lease();
        lease.setStartDate(new LocalDate(2000,1,1));

        item = new LeaseItem();
        item.leaseTermRepository = mockLeaseTermRepository;

        lease.getItems().add(item);
        item.setLease(lease);

        item.setType(LeaseItemType.SERVICE_CHARGE);

        term = new LeaseTermForServiceCharge();

        item.getTerms().add(term);
        term.setLeaseItem(item);

        // when
        term.doInitialize();

        // then
        term.setStartDate(new LocalDate(2011, 1, 1));
        term.setBudgetedValue(BigDecimal.valueOf(6000).setScale(4));
    }

    public static class Align extends LeaseTermForServiceChargeTest {

        @Test
        public void testUpdate() {
            term.align();
            assertThat(term.getEffectiveValue()).isEqualTo(term.getBudgetedValue());
            LeaseTermForServiceCharge nextTerm = new LeaseTermForServiceCharge();

            item.getTerms().add(nextTerm);
            nextTerm.setLeaseItem(item);

            nextTerm.setPrevious(term);
            nextTerm.doInitialize();

            nextTerm.align();
            assertThat(nextTerm.getBudgetedValue()).isEqualTo(term.getBudgetedValue());
        }
    }

    public static class ValueForDate extends LeaseTermForServiceChargeTest {

        @Test
        public void testValueForDueDate() throws Exception {

            // given
            LeaseTermForServiceCharge term = new LeaseTermForServiceCharge();
            item.getTerms().add(term);
            term.setLeaseItem(item);
            term.setBudgetedValue(BigDecimal.valueOf(6000));
            term.setAuditedValue(BigDecimal.valueOf(6600));

            // when, then
            assertThat(term.valueForDate(new LocalDate(2011, 1, 1))).isEqualTo(BigDecimal.valueOf(6000));
            assertThat(term.valueForDate(new LocalDate(2011, 4, 1))).isEqualTo(BigDecimal.valueOf(6000));
            assertThat(term.valueForDate(new LocalDate(2011, 7, 1))).isEqualTo(BigDecimal.valueOf(6000));
            assertThat(term.valueForDate(new LocalDate(2011, 10, 1))).isEqualTo(BigDecimal.valueOf(6000));
            assertThat(term.valueForDate(new LocalDate(2012, 1, 1))).isEqualTo(BigDecimal.valueOf(6000));
            assertThat(term.valueForDate(new LocalDate(2012, 4, 1))).isEqualTo(BigDecimal.valueOf(6000));

            // and given
            term.setEndDate(new LocalDate(2011, 12, 31));

            // when, then
            assertThat(term.valueForDate(new LocalDate(2011, 1, 1))).isEqualTo(BigDecimal.valueOf(6000));
            assertThat(term.valueForDate(new LocalDate(2012, 4, 1))).isEqualTo(BigDecimal.valueOf(6600));

        }
    }

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(LeaseItem.class))
                    .withFixture(pojos(LeaseTerm.class, LeaseTermForTesting.class))
                    .withFixture(statii())
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new LeaseTermForServiceCharge());
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static PojoTester.FixtureDatumFactory<LeaseTermStatus> statii() {
            return new PojoTester.FixtureDatumFactory(LeaseTermStatus.class, (Object[])LeaseTermStatus.values());
        }

    }

    public static class AllowsOpenEndate extends LeaseTermForServiceChargeTest {

        LeaseTermForServiceCharge term;
        LeaseItem item;

        @Before
        public void setUp(){
            term = new LeaseTermForServiceCharge();
            item = new LeaseItem();
            term.setLeaseItem(item);
        }

        @Test
        public void noOpenEndDateAllowed() {

            // when
            item.setType(LeaseItemType.SERVICE_CHARGE_BUDGETED);
            // then
            assertFalse(term.allowOpenEndDate());

        }

    }

}

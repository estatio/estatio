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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.with.WithIntervalMutable;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester.FixtureDatumFactory;
import org.incode.module.unittestsupport.dom.with.WithIntervalMutableContractTestAbstract_changeDates;

import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.tax.Tax;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseItemTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    LeaseItem leaseItem;

    public static class GetCurrentValue extends LeaseItemTest {

        private final LocalDate now = LocalDate.now();

        private LeaseTermForTesting leaseTerm;

        private LocalDate getCurrentValueDateArgument;

        @Mock
        private ClockService mockClockService;

        @Before
        public void setUp() throws Exception {
            context.checking(new Expectations() {
                {
                    oneOf(mockClockService).now();
                    will(returnValue(now));
                }
            });

            leaseTerm = new LeaseTermForTesting();
            leaseTerm.setValue(BigDecimal.TEN);

            leaseItem = new LeaseItem() {
                @Override
                @Property(hidden = Where.EVERYWHERE)
                public LeaseTerm currentTerm(LocalDate date) {
                    GetCurrentValue.this.getCurrentValueDateArgument = date;
                    return leaseTerm;
                }
            };
            leaseItem.clockService = mockClockService;
        }

        @Test
        public void test() {
            assertThat(leaseItem.getValue()).isEqualTo(BigDecimal.TEN);
            assertThat(getCurrentValueDateArgument).isEqualTo(now);
        }

    }

    public static class Remove extends LeaseItemTest {

        private Lease lease;
        private LeaseTermForTesting leaseTerm;

        @Mock
        private DomainObjectContainer mockContainer;

        private boolean leaseTermSuccessfullyRemoved;

        @Before
        public void setUp() throws Exception {
            lease = new Lease();
            leaseItem = new LeaseItem();

            leaseItem.setLease(lease);
            leaseItem.setContainer(mockContainer);

            leaseTerm = new LeaseTermForTesting() {
                @Override
                public boolean doRemove() {
                    return leaseTermSuccessfullyRemoved;
                }
            };

        }

        @Test
        public void whenConfirmedAndNoChildTerms() throws Exception {
            expectingRemoveAndFlush(leaseItem);

            Object returned = leaseItem.remove();
            assertThat(returned).isEqualTo((Object) lease);
        }

        @Test
        public void whenConfirmedAndChildTermsThatVeto() throws Exception {

            leaseItem.getTerms().add(leaseTerm);
            leaseTermSuccessfullyRemoved = false;

            Object returned = leaseItem.remove();
            assertThat(returned).isEqualTo((Object) leaseItem);
        }

        @Test
        public void whenConfirmedAndChildTermsThatDontVeto() throws Exception {
            leaseItem.getTerms().add(leaseTerm);

            leaseTermSuccessfullyRemoved = true;
            expectingRemoveAndFlush(leaseItem);

            Object returned = leaseItem.remove();
            assertThat(returned).isEqualTo((Object) lease);
        }

        private void expectingRemoveAndFlush(final LeaseItem obj) {
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).remove(obj);
                    oneOf(mockContainer).flush();
                }
            });
        }

    }

    public static class ToString extends LeaseItemTest {

        private Lease lease;

        @Before
        public void setUp() throws Exception {
            lease = new Lease();
            AgreementType type = new AgreementType();
            type.setTitle("Lease");
            lease.setType(type);
            lease.setReference("A");
            leaseItem = new LeaseItem();
            leaseItem.setLease(lease);
        }

        @Test
        public void happyCase() throws Exception {
            assertThat(lease.toString()).isEqualTo("Lease{type=Lease, reference=A}");
            assertThat(leaseItem.toString()).isEqualTo("LeaseItem{lease=A, type=null, charge=null, startDate=null, sequence=null}");
        }
    }

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Charge.class))
                    .withFixture(pojos(Lease.class))
                    .withFixture(statii())
                    .withFixture(pojos(Tax.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new LeaseItem());
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static FixtureDatumFactory<LeaseItemStatus> statii() {
            return new FixtureDatumFactory(LeaseItemStatus.class, (Object[]) LeaseItemStatus.values());
        }
    }

    public static class ChangeDates extends WithIntervalMutableContractTestAbstract_changeDates<LeaseItem> {

        private LeaseItem leaseItem;

        @Before
        public void setUp() throws Exception {
            leaseItem = withIntervalMutable;
        }

        protected LeaseItem doCreateWithIntervalMutable(final WithIntervalMutable.Helper<LeaseItem> mockChangeDates) {
            return new LeaseItem() {
                @Override WithIntervalMutable.Helper<LeaseItem> getChangeDates() {
                    return mockChangeDates;
                }
            };
        }

        @Test
        public void changeDatesDelegate() {
            leaseItem = new LeaseItem();
            assertThat(leaseItem.getChangeDates()).isNotNull();
        }

    }

    public static class ChoicesNewSourceItem extends LeaseItemTest {

        @Mock
        LeaseItemSourceRepository mockLeaseItemSourceRepository;

        LeaseItem itemLinked;
        LeaseItem itemNotLinked;
        Lease lease;

        @Before
        public void setup() {

            itemLinked = new LeaseItem();
            itemLinked.setType(LeaseItemType.RENT);
            itemNotLinked = new LeaseItem();
            itemNotLinked.setType(LeaseItemType.RENT_DISCOUNT);

            lease = new Lease() {
                @Override public SortedSet<LeaseItem> getItems() {
                    return new TreeSet<>(Arrays.asList(itemLinked, itemNotLinked, leaseItem));
                }
            };
        }

        @Test
        public void testChoices() throws Exception {

            // given
            leaseItem = new LeaseItem();
            leaseItem.setLease(lease);
            leaseItem.leaseItemSourceRepository = mockLeaseItemSourceRepository;

            LeaseItemSource leaseItemSource = new LeaseItemSource();
            leaseItemSource.setItem(leaseItem);
            leaseItemSource.setSourceItem(itemLinked);

            context.checking(new Expectations() {
                {
                    allowing(mockLeaseItemSourceRepository).findByItem(leaseItem);
                    will(returnValue(Arrays.asList(leaseItemSource)));
                }
            });

            // when
            List<LeaseItem> choices = leaseItem.choices0NewSourceItem(leaseItem);

            // then
            assertThat(leaseItem.getLease().getItems()).hasSize(3);
            assertThat(choices).hasSize(1);
            assertThat(choices.get(0)).isEqualTo(itemNotLinked);

        }

    }

    public static class CompareTo extends ComparableContractTest_compareTo<LeaseItem> {

        private Lease lease1;
        private Lease lease2;

        @Before
        public void setUp() throws Exception {
            lease1 = new Lease();
            lease2 = new Lease();

            lease1.setReference("A");
            lease2.setReference("B");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<LeaseItem>> orderedTuples() {
            return listOf(
                    listOf(
                            newLeaseItem(null, null, null),
                            newLeaseItem(lease1, null, null),
                            newLeaseItem(lease1, null, null),
                            newLeaseItem(lease2, null, null)
                    ),
                    listOf(
                            newLeaseItem(lease1, null, null),
                            newLeaseItem(lease1, LeaseItemType.RENT, null),
                            newLeaseItem(lease1, LeaseItemType.RENT, null),
                            newLeaseItem(lease1, LeaseItemType.SERVICE_CHARGE, null)
                    ),
                    listOf(
                            newLeaseItem(lease1, LeaseItemType.RENT, null),
                            newLeaseItem(lease1, LeaseItemType.RENT, 1),
                            newLeaseItem(lease1, LeaseItemType.RENT, 1),
                            newLeaseItem(lease1, LeaseItemType.RENT, 2)
                    )
            );
        }

        private LeaseItem newLeaseItem(
                Lease lease,
                LeaseItemType type, Integer sequence) {
            final LeaseItem li = new LeaseItem();
            li.setLease(lease);
            li.setType(type);
            li.setSequence(sequence != null ? BigInteger.valueOf(sequence.longValue()) : null);
            return li;
        }

    }

}
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
package org.estatio.dom.agreement;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

import static org.assertj.core.api.Assertions.assertThat;

public class AgreementRoleTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final AgreementRole agreement = new AgreementRole();
            newPojoTester()
                    .withFixture(pojos(AgreementRoleType.class))
                    .withFixture(pojos(Agreement.class, AgreementForTesting.class))
                    .withFixture(pojos(Party.class, PartyForTesting.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(agreement);
        }
    }

    public static class CompareTo extends ComparableContractTest_compareTo<AgreementRole> {

        private Agreement agreement1;
        private Agreement agreement2;

        private Party party1;
        private Party party2;

        private AgreementRoleType type1;
        private AgreementRoleType type2;

        @Before
        public void setup() {

            agreement1 = new AgreementForTesting();
            agreement2 = new AgreementForTesting();
            agreement1.setReference("A");
            agreement2.setReference("B");

            party1 = new PartyForTesting();
            party2 = new PartyForTesting();
            party1.setName("A");
            party2.setName("B");

            type1 = new AgreementRoleType();
            type2 = new AgreementRoleType();
            type1.setTitle("Abc");
            type2.setTitle("Def");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<AgreementRole>> orderedTuples() {
            return listOf(
                    listOf(
                            newAgreementRole(null, null, null, null),
                            newAgreementRole(agreement1, null, null, null),
                            newAgreementRole(agreement1, null, null, null),
                            newAgreementRole(agreement2, null, null, null)
                    )
                    , listOf(
                            newAgreementRole(agreement1, new LocalDate(2013, 4, 1), null, null),
                            newAgreementRole(agreement1, new LocalDate(2013, 3, 1), null, null),
                            newAgreementRole(agreement1, new LocalDate(2013, 3, 1), null, null),
                            newAgreementRole(agreement1, null, null, null)
                    )
                    , listOf(
                            newAgreementRole(agreement1, new LocalDate(2013, 4, 1), null, null),
                            newAgreementRole(agreement1, new LocalDate(2013, 4, 1), type1, null),
                            newAgreementRole(agreement1, new LocalDate(2013, 4, 1), type1, null),
                            newAgreementRole(agreement1, new LocalDate(2013, 4, 1), type2, null)
                    )
                    , listOf(
                            newAgreementRole(agreement1, new LocalDate(2013, 4, 1), type1, null),
                            newAgreementRole(agreement1, new LocalDate(2013, 4, 1), type1, party1),
                            newAgreementRole(agreement1, new LocalDate(2013, 4, 1), type1, party1),
                            newAgreementRole(agreement1, new LocalDate(2013, 4, 1), type1, party2)
                    )
            );
        }

        private AgreementRole newAgreementRole(Agreement agreement, LocalDate startDate, AgreementRoleType art, Party party) {
            final AgreementRole ar = new AgreementRole();
            ar.setAgreement(agreement);
            ar.setParty(party);
            ar.setStartDate(startDate);
            ar.setType(art);
            return ar;
        }
    }

    public static class IsCurrent {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        private ClockService clockService;

        private LocalDate now;

        private AgreementRole agreementRole;

        @Before
        public void setUp() throws Exception {
            now = LocalDate.now();

            context.checking(new Expectations() {
                {
                    oneOf(clockService).now();
                    will(returnValue(now));
                }
            });

            agreementRole = new AgreementRole();
            agreementRole.clockService = clockService;
        }

        @Test
        public void whenWithinOnStart() {
            assertIsCurrent(now, now.plusDays(1), true);
        }

        @Test
        public void whenJustWithinOnEnd() {
            assertIsCurrent(now.minusDays(1), now, true);
        }

        @Test
        public void whenOpenEnd() {
            assertIsCurrent(now, null, true);
        }

        @Test
        public void whenOpenStart() {
            assertIsCurrent(null, now, true);
        }

        @Test
        public void whenJustBefore() {
            assertIsCurrent(now.plusDays(1), now.plusDays(2), false);
        }

        @Test
        public void whenJustAfter() {
            assertIsCurrent(now.minusDays(2), now.minusDays(1), false);
        }

        private void assertIsCurrent(final LocalDate start, final LocalDate end, final boolean expect) {
            agreementRole.setStartDate(start);
            agreementRole.setEndDate(end);
            assertThat(agreementRole.isCurrent()).isEqualTo(expect);
        }

    }

}
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
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.WithIntervalMutableContractTestAbstract_changeDates;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;
import org.estatio.dom.party.Person;
import org.estatio.services.clock.ClockService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class AgreementTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);


    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final Agreement agreement = new AgreementForTesting();
            newPojoTester()
                    .withFixture(pojos(Agreement.class, AgreementForTesting.class))
                    .withFixture(pojos(AgreementType.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(agreement);
        }
    }

    public static class ChangeDates extends WithIntervalMutableContractTestAbstract_changeDates<Agreement> {

        private Agreement agreement;

        @Before
        public void setUp() throws Exception {
            agreement = withIntervalMutable;
        }

        protected Agreement doCreateWithIntervalMutable(final WithIntervalMutable.Helper<Agreement> mockChangeDates) {
            return new AgreementForTesting() {
                @Override
                org.estatio.dom.WithIntervalMutable.Helper<Agreement> getChangeDates() {
                    return mockChangeDates;
                }
            };
        }


        @Test
        public void changeDatesDelegate() {
            agreement = new AgreementForTesting();
            assertThat(agreement.getChangeDates(), is(not(nullValue())));
        }
    }

    public static class FindCurrentOrMostRecentParty extends AgreementTest {

        @Mock
        private ClockService mockClockService;

        private Agreement agreement;

        private AgreementRoleType landlordArt;
        private AgreementRoleType tenantArt;

        private Party landlord;
        private Party tenant1;
        private Party tenant2;

        private AgreementRole arLandlord;
        private AgreementRole arTenant1;
        private AgreementRole arTenant2;

        private final LocalDate clockDate = new LocalDate(2013, 4, 1);

        @Before
        public void setup() {

            landlord = new PartyForTesting();
            tenant1 = new PartyForTesting();
            tenant2 = new PartyForTesting();

            landlordArt = new AgreementRoleType();
            landlordArt.setTitle("Landlord");

            tenantArt = new AgreementRoleType();
            tenantArt.setTitle("Tenant");

            agreement = new AgreementForTesting();

            arLandlord = new AgreementRole();
            arLandlord.setType(landlordArt);
            arLandlord.setParty(landlord);
            arLandlord.injectClockService(mockClockService);

            arTenant1 = new AgreementRole();
            arTenant1.setType(tenantArt);
            arTenant1.setParty(tenant1);
            arTenant1.injectClockService(mockClockService);

            arTenant2 = new AgreementRole();
            arTenant2.setType(tenantArt);
            arTenant2.setParty(tenant2);
            arTenant2.injectClockService(mockClockService);

            // tenant 1 superceded by tenant 2
            arTenant1.setEndDate(clockDate.minusMonths(1));
            arTenant2.setStartDate(arTenant1.getEndDate().plusDays(1));

            context.checking(new Expectations() {
                {
                    allowing(mockClockService).now();
                    will(returnValue(clockDate));
                }
            });
        }

        @Test
        public void whenNone() {
            assertThat(agreement.findCurrentOrMostRecentParty(landlordArt), is(nullValue()));
        }

        @Test
        public void whenStillCurrentOnlyOnePartyOfType() {
            addAllRoles();
            assertThat(agreement.findCurrentOrMostRecentParty(landlordArt), is(landlord));
        }

        @Test
        public void whenStillCurrentTwoPartiesOfType() {
            addAllRoles();
            assertThat(agreement.findCurrentOrMostRecentParty(tenantArt), is(tenant2));
        }

        @Test
        public void whenTerminated() {
            addAllRoles();
            agreement.setEndDate(clockDate.minusDays(2));
            assertThat(agreement.findCurrentOrMostRecentParty(landlordArt), is(landlord));
            assertThat(agreement.findCurrentOrMostRecentParty(tenantArt), is(tenant2));
        }

        private void addAllRoles() {
            agreement.getRoles().add(arLandlord);
            agreement.getRoles().add(arTenant1);
            agreement.getRoles().add(arTenant2);
        }
    }


    public static class FindParty extends AgreementTest {

        @Mock
        private AgreementRoleTypes mockAgreementRoleTypes;

        @Mock
        private ClockService mockClockService;

        private AgreementRoleType creditorAgreementRoleType;
        private AgreementRoleType debtorAgreementRoleType;

        private AgreementForSubtypeTesting agreement;

        private AgreementRole creditorAr;
        private AgreementRole creditorAr2;
        private AgreementRole debtorAr;
        private AgreementRole debtorAr2;

        private Party creditor;
        private Party debtor;

        public static class AgreementForSubtypeTesting extends Agreement {

            public AgreementForSubtypeTesting() {
                super(null, null);
            }

            public ApplicationTenancy getApplicationTenancy() {
                return null;
            }

        }

        @Before
        public void setUp() throws Exception {
            creditorAgreementRoleType = new AgreementRoleType();
            debtorAgreementRoleType = new AgreementRoleType();

            creditor = new Organisation();
            debtor = new Person();

            creditorAr = new AgreementRole();
            creditorAr.setType(creditorAgreementRoleType);
            creditorAr.setParty(creditor);
            creditorAr.setStartDate(new LocalDate(2013, 7, 1)); // current
            creditorAr.injectClockService(mockClockService);

            creditorAr2 = new AgreementRole();
            creditorAr2.setType(creditorAgreementRoleType);
            creditorAr2.setParty(creditor);
            creditorAr2.setStartDate(new LocalDate(2012, 7, 1)); // not current
            creditorAr2.setEndDate(new LocalDate(2013, 6, 30));
            creditorAr2.injectClockService(mockClockService);

            debtorAr = new AgreementRole();
            debtorAr.setType(debtorAgreementRoleType);
            debtorAr.setParty(debtor);
            debtorAr.setStartDate(new LocalDate(2013, 7, 1)); // current
            debtorAr.injectClockService(mockClockService);

            debtorAr2 = new AgreementRole();
            debtorAr2.setType(debtorAgreementRoleType);
            debtorAr2.setParty(debtor);
            debtorAr2.setStartDate(new LocalDate(2012, 7, 1)); // not current
            debtorAr2.setEndDate(new LocalDate(2013, 6, 30));
            debtorAr2.injectClockService(mockClockService);

            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypes).findByTitle("Creditor");
                    will(returnValue(creditorAgreementRoleType));

                    allowing(mockAgreementRoleTypes).findByTitle("Debtor");
                    will(returnValue(creditorAgreementRoleType));

                    allowing(mockClockService).now();
                    will(returnValue(new LocalDate(2013, 8, 1)));
                }
            });

            agreement = new AgreementForSubtypeTesting();
            agreement.injectAgreementRoleTypes(mockAgreementRoleTypes);
        }

        @Test
        public void test() {

            agreement.getRoles().add(creditorAr);
            agreement.getRoles().add(creditorAr2);
            agreement.getRoles().add(debtorAr);
            agreement.getRoles().add(debtorAr2);

            assertThat(agreement.findCurrentOrMostRecentParty("Creditor"), is(creditor));
        }

    }


    public static class FindRole extends AgreementTest {

        @Mock
        private AgreementRoles mockAgreementRoles;

        private AgreementRoleType art;
        private Party party;
        private LocalDate date;

        private Agreement agreement;


        @Before
        public void setUp() throws Exception {
            art = new AgreementRoleType();
            party = new PartyForTesting();

            date = new LocalDate(2013, 4, 1);

            agreement = new AgreementForTesting();
            agreement.injectAgreementRoles(mockAgreementRoles);
        }

        @Test
        public void findRoles() {
            final AgreementRole agreementRole = new AgreementRole();
            context.checking(new Expectations() {
                {
                    oneOf(mockAgreementRoles).findByAgreementAndPartyAndTypeAndContainsDate(agreement, party, art, date);
                    will(returnValue(agreementRole));
                }
            });
            assertThat(agreement.findRole(party, art, date), is(agreementRole));
        }

    }

    public static class Inject extends AgreementTest {

        @Mock
        private Agreements mockAgreements;
        @Mock
        private AgreementRoleTypes mockAgreementRoleTypes;
        @Mock
        private AgreementTypes mockAgreementTypes;
        @Mock
        private AgreementRoles mockAgreementRoles;

        private Agreement agreement;

        @Before
        public void setup() {
            agreement = new AgreementForTesting();
        }

        @Test
        public void injected() {
            agreement.injectAgreementTypes(mockAgreementTypes);
            agreement.injectAgreementRoleTypes(mockAgreementRoleTypes);
            agreement.injectAgreements(mockAgreements);
            agreement.injectAgreementRoles(mockAgreementRoles);

            assertThat(agreement.agreements, is(mockAgreements));
            assertThat(agreement.agreementRoles, is(mockAgreementRoles));
            assertThat(agreement.agreementRoleTypes, is(mockAgreementRoleTypes));
            assertThat(agreement.agreementTypes, is(mockAgreementTypes));
        }
    }


    public static class IsCurrent extends AgreementTest {

        @Mock
        private ClockService mockClockService;

        private Agreement agreement;

        @Before
        public void setUp() throws Exception {
            agreement = new AgreementForTesting();
            agreement.injectClockService(mockClockService);
        }

        @Test
        public void whenNoTerminationDate() {
            agreement.setStartDate(new LocalDate(2013, 4, 1));
            agreement.setEndDate(new LocalDate(2013, 6, 30));


            // before
            expectClockNowToReturn(new LocalDate(2013, 3, 31));
            assertThat(agreement.isCurrent(), is(false));

            // within
            expectClockNowToReturn(new LocalDate(2013, 4, 1));
            assertThat(agreement.isCurrent(), is(true));

            expectClockNowToReturn(new LocalDate(2013, 5, 15));
            assertThat(agreement.isCurrent(), is(true));

            expectClockNowToReturn(new LocalDate(2013, 6, 30));
            assertThat(agreement.isCurrent(), is(true));

            // after
            expectClockNowToReturn(new LocalDate(2013, 7, 1));
            assertThat(agreement.isCurrent(), is(false));
        }

        @Ignore // TO REVIEW
        @Test
        public void whenTerminationDate() {
            agreement.setStartDate(new LocalDate(2013, 4, 1));
            agreement.setEndDate(new LocalDate(2013, 5, 20));

            // before
            expectClockNowToReturn(new LocalDate(2013, 3, 31));
            assertThat(agreement.isCurrent(), is(false));

            // within
            expectClockNowToReturn(new LocalDate(2013, 4, 1));
            assertThat(agreement.isCurrent(), is(true));

            expectClockNowToReturn(new LocalDate(2013, 5, 15));
            assertThat(agreement.isCurrent(), is(true));

            expectClockNowToReturn(new LocalDate(2013, 5, 20));
            assertThat(agreement.isCurrent(), is(true));

            // after
            expectClockNowToReturn(new LocalDate(2013, 5, 21));
            assertThat(agreement.isCurrent(), is(false));
        }


        private void expectClockNowToReturn(final LocalDate result) {
            context.checking(new Expectations() {
                {
                    oneOf(mockClockService).now();
                    will(returnValue(result));
                }
            });
        }

    }


    public static class NewRole extends AgreementTest {

        public static class ActionExecution extends NewRole {

            @Mock
            private DomainObjectContainer mockContainer;

            private AgreementRoleType art;
            private Party party;

            private Agreement agreement;

            private LocalDate startDate;
            private LocalDate endDate;


            @Before
            public void setUp() throws Exception {
                art = new AgreementRoleType();
                party = new PartyForTesting();

                startDate = new LocalDate(2013, 4, 1);
                endDate = new LocalDate(2023, 3, 30);

                agreement = new AgreementForTesting();
                agreement.setContainer(mockContainer);
            }

            @Test
            public void newRole() {

                final AgreementRole agreementRole = new AgreementRole();
                final Sequence sequence = context.sequence("newRole");
                context.checking(new Expectations() {
                    {
                        oneOf(mockContainer).newTransientInstance(AgreementRole.class);
                        inSequence(sequence);
                        will(returnValue(agreementRole));

                        oneOf(mockContainer).persistIfNotAlready(with(initialized(agreementRole)));

                    }

                    private Matcher<AgreementRole> initialized(final AgreementRole agreementRole) {
                        return new TypeSafeMatcher<AgreementRole>() {

                            @Override
                            public void describeTo(Description description) {
                                description.appendText("correctly initialized");
                            }

                            @Override
                            protected boolean matchesSafely(AgreementRole item) {
                                return item.getParty() == party &&
                                        item.getType() == art &&
                                        item.getAgreement() == agreement &&
                                        Objects.equal(item.getStartDate(), startDate) &&
                                        Objects.equal(item.getEndDate(), endDate);
                            }
                        };
                    }
                });

                Agreement x = agreement.newRole(art, party, startDate, endDate);
                assertThat(x, is(agreement));
            }

        }

        public static class Choices extends NewRole {

            private AgreementType art;
            private Agreement agreement;

            @Mock
            private AgreementRoleTypes mockAgreementRoleTypes;

            @Before
            public void setUp() throws Exception {

                art = new AgreementType();

                agreement = new AgreementForTesting();
                agreement.setType(art);
                agreement.injectAgreementRoleTypes(mockAgreementRoleTypes);
            }

            @Test
            public void choices() {
                final List<AgreementRoleType> list = Lists.newArrayList();
                context.checking(new Expectations() {
                    {
                        oneOf(mockAgreementRoleTypes).findApplicableTo(art);
                        will(returnValue(list));
                    }
                });

                assertThat(agreement.choices0NewRole(), is(list));;
            }
        }

        public static class Defaults extends NewRole  {

            private Agreement agreement;

            private LocalDate effectiveStartDate;
            private LocalDate effectiveEndDate;


            @Before
            public void setUp() throws Exception {
                effectiveStartDate = new LocalDate(2013,4,1);
                effectiveEndDate = new LocalDate(2023,3,30);

                agreement = new AgreementForTesting() {
                    @Override
                    public LocalDate getStartDate() {
                        return effectiveStartDate;
                    }
                    @Override
                    public LocalDate getEndDate() {
                        return effectiveEndDate;
                    }
                };
            }

            @Test
            public void defaultStart() {
                assertThat(agreement.default2NewRole(), is(effectiveStartDate));
            }

            @Test
            public void defaultEnd() {
                assertThat(agreement.default3NewRole(), is(effectiveEndDate));
            }

        }

        public static class ValidateNewRole extends NewRole {

            @Mock
            private DomainObjectContainer mockContainer;

            @Mock
            private ClockService mockClockService;

            private AgreementRoleType art;
            private AgreementRoleType artOther;
            private Party party;

            private Agreement agreement;

            private LocalDate startDate;
            private LocalDate endDate;


            @Before
            public void setUp() throws Exception {
                art = new AgreementRoleType();
                artOther = new AgreementRoleType();

                party = new PartyForTesting();

                startDate = new LocalDate(2013,4,1);
                endDate = new LocalDate(2023,3,30);

                agreement = new AgreementForTesting();
                agreement.setContainer(mockContainer);
            }



            @Test
            public void validateNewRole_valid_nullStart_noExistingRoles() {
                assertThat(
                        agreement.validateNewRole(art, party, null, endDate),
                        is(nullValue()));
            }

            @Test
            public void validateNewRole_valid_nullEnd_noExistingRoles() {
                assertThat(
                        agreement.validateNewRole(art, party, startDate, null),
                        is(nullValue()));
            }

            @Test
            public void validateNewRole_valid_startBeforeEnd_noExistingRoles() {
                assertThat(
                        agreement.validateNewRole(art, party, startDate, endDate),
                        is(nullValue()));
            }

            @Test
            public void validateNewRole_valid_startSameAsEnd_noExistingRoles() {
                startDate = endDate;
                assertThat(
                        agreement.validateNewRole(art, party, startDate, endDate),
                        is(nullValue()));
            }

            @Test
            public void validateNewRole_invalid_startAfterEnd_noExistingRoles() {
                startDate = endDate.plusDays(1);
                assertThat(
                        agreement.validateNewRole(art, party, startDate, endDate),
                        is("End date cannot be earlier than start date"));
            }

            @Test
            public void validateNewRole_valid_nullStartAnd_existingRolesDoNotContainType() {
                final AgreementRole existingRole = new AgreementRole();
                existingRole.setType(artOther);
                agreement.getRoles().add(existingRole);
                assertThat(
                        agreement.validateNewRole(art, party, null, null),
                        is(nullValue()));
            }


            @Test
            public void validateNewRole_invalid_nullStartAnd_existingRolesDoesContainType() {
                final AgreementRole existingRole = new AgreementRole();
                existingRole.injectClockService(mockClockService);

                context.checking(new Expectations() {{
                    allowing(mockClockService).now();
                    will(returnValue(new LocalDate(2013,4,2)));
                }});

                existingRole.setType(art);
                agreement.getRoles().add(existingRole);
                assertThat(
                        agreement.validateNewRole(art, party, null, null),
                        is("Add a successor/predecessor to existing agreement role"));
            }

        }

    }
}

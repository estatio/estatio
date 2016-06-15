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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.core.commons.matchers.IsisMatchers;
import org.apache.isis.core.unittestsupport.jmocking.IsisActions;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.PojoTester;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.agreement.AgreementRepository;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleRepository;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.agreement.AgreementTest;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypeRepository;
import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandateConstants;
import org.estatio.dom.bankmandate.BankMandateRepository;
import org.estatio.dom.bankmandate.Scheme;
import org.estatio.dom.bankmandate.SequenceType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccounts;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;
import org.estatio.services.clock.ClockService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LeaseTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    Lease lease;

    public static class AddUnit extends LeaseTest {

        @Mock
        private DomainObjectContainer mockContainer;

        private Occupancies occupancies;

        private Unit unit;

        @Before
        public void setUp() throws Exception {
            unit = new Unit();
            // this is actually a mini-integration test...
            occupancies = new Occupancies();
            occupancies.setContainer(mockContainer);
            lease = new Lease();
            lease.occupanciesRepo = occupancies;
        }

        @Test
        public void test() {
            final Occupancy leaseUnit = new Occupancy();
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(Occupancy.class);
                    will(returnValue(leaseUnit));
                    oneOf(mockContainer).persistIfNotAlready(leaseUnit);
                }
            });

            final Occupancy addedUnit = occupancies.newOccupancy(lease, unit, null);
            assertThat(addedUnit, is(leaseUnit));
            assertThat(leaseUnit.getLease(), is(lease));
            assertThat(leaseUnit.getUnit(), is(unit));
        }

    }

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Agreement.class, AgreementForTesting.class))
                    .withFixture(pojos(Party.class, PartyForTesting.class))
                    .withFixture(pojos(AgreementType.class))
                    .withFixture(pojos(LeaseType.class))
                    .withFixture(pojos(BankMandate.class))
                    .withFixture(statii())
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new Lease());
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static PojoTester.FixtureDatumFactory<LeaseStatus> statii() {
            return new PojoTester.FixtureDatumFactory(LeaseStatus.class, (Object[]) LeaseStatus.values());
        }

    }

    public static class ChangeStatus extends LeaseTest {

        @Mock
        private DomainObjectContainer mockContainer;

        private LeaseMenu leaseMenu;

        @Before
        public void setUp() throws Exception {

            leaseMenu = new LeaseMenu();
            leaseMenu.setContainer(mockContainer);

            lease = new Lease();

            context.checking(new Expectations() {
                {
                    allowing(mockContainer).newTransientInstance(with(IsisMatchers.anySubclassOf(Object.class)));
                    will(IsisActions.returnNewTransientInstance());
                    ignoring(mockContainer);
                }
            });

        }

        @Test
        public void testWhenActived() {
            lease.setStatus(LeaseStatus.ACTIVE);
            assertTrue(lease.hideResumeAll());
            assertFalse(lease.hideSuspendAll());
            assertFalse(lease.hideTerminate());
        }

        @Test
        public void testWhenSuspended() {
            lease.setStatus(LeaseStatus.SUSPENDED);
            assertFalse(lease.hideResumeAll());
            assertTrue(lease.hideSuspendAll());
            assertTrue(lease.hideTerminate());
        }

        @Test
        public void testWhenTerminated() {
            lease.setStatus(LeaseStatus.TERMINATED);
            assertTrue(lease.hideResumeAll());
            assertTrue(lease.hideSuspendAll());
            assertTrue(lease.hideTerminate());
        }

        @Test
        public void testEffectiveStatus() {
            LeaseItem item1 = new LeaseItem();
            LeaseItem item2 = new LeaseItem();
            item1.setStatus(LeaseItemStatus.SUSPENDED);
            item1.setSequence(new BigInteger("1"));
            item2.setStatus(LeaseItemStatus.SUSPENDED);
            item2.setSequence(new BigInteger("2"));
            lease.getItems().add(item1);
            lease.getItems().add(item2);
            assertThat(lease.getEffectiveStatus(), Matchers.is(LeaseStatus.SUSPENDED));
            item2.setStatus(LeaseItemStatus.ACTIVE);
            assertThat(lease.getEffectiveStatus(), Matchers.is(LeaseStatus.SUSPENDED_PARTIALLY));
        }
    }

    public static class GetEffectiveInterval extends LeaseTest {

        @Before
        public void setup() {
            lease = new Lease();
            lease.setStartDate(new LocalDate(2012, 1, 1));
        }

        @Test
        public void getEffectiveInterval() {
            Assert.assertNull(lease.getEffectiveInterval().endDateExcluding());
            lease.setTenancyEndDate(new LocalDate(2012, 6, 30));
            Assert.assertThat(lease.getEffectiveInterval().endDateExcluding(), Is.is(new LocalDate(2012, 7, 1)));
        }

    }

    public static class NewItem extends LeaseTest {

        @Mock
        private DomainObjectContainer mockContainer;

        @Mock
        private EstatioApplicationTenancyRepository mockApplicationTenancyRepository;

        private LeaseItems leaseItems;

        @Before
        public void setUp() throws Exception {

            // this is actually a mini-integration test...
            leaseItems = new LeaseItems();
            leaseItems.setContainer(mockContainer);
            leaseItems.estatioApplicationTenancyRepository = mockApplicationTenancyRepository;

            Property property = new Property();
            Party party = new Party() {
                @Override public ApplicationTenancy getApplicationTenancy() {
                    return null;
                }
            };

            lease = new Lease() {
                @Override public Party getPrimaryParty() {
                    return party;
                }

                @Override public Property getProperty() {
                    return property;
                }
            };
            lease.leaseItems = leaseItems;
        }

        @SuppressWarnings("unchecked")
        @Test
        public void test() {
            assertThat(lease.getItems(), Matchers.empty());

            final ApplicationTenancy leaseItemApplicationTenancy = new ApplicationTenancy();
            leaseItemApplicationTenancy.setPath("/it/XXX/_");

            final LeaseItem leaseItem = new LeaseItem();
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(LeaseItem.class);
                    will(returnValue(leaseItem));

                    oneOf(mockContainer).persistIfNotAlready(leaseItem);
                    oneOf(mockContainer).allMatches(with(any(QueryDefault.class)));
                    will(returnValue(new ArrayList<LeaseItem>()));

                    oneOf(mockApplicationTenancyRepository);
                    will(returnValue(leaseItemApplicationTenancy));

                }
            });

            final LeaseItem newItem = lease.newItem(LeaseItemType.RENT, new Charge(), InvoicingFrequency.MONTHLY_IN_ADVANCE, PaymentMethod.BANK_TRANSFER, null);
            assertThat(newItem, is(leaseItem));
            assertThat(leaseItem.getLease(), is(lease));
            assertThat(leaseItem.getSequence(), is(BigInteger.ONE));
            assertThat(leaseItem.getApplicationTenancyPath(), is("/it/XXX/_"));

            // this assertion not true for unit tests, because we rely on JDO
            // to manage the bidir relationship for us.
            // assertThat(lease.getItems(), Matchers.contains(newItem));
        }

    }

    public static class DisableNewItem extends LeaseTest {

        @Test
        public void test() {
            // given
            Lease lease = new Lease();

            // when
            Assertions.assertThat(lease.getProperty()).isEqualTo(null);

            // then
            Assertions.assertThat(lease.disableNewItem()).isEqualTo("Please set occupancy first");

        }

    }

    public static class NewMandate extends LeaseTest {

        @Mock
        private AgreementRoleTypeRepository mockAgreementRoleTypeRepository;
        @Mock
        private AgreementRoleRepository mockAgreementRoleRepository;
        @Mock
        private AgreementTypeRepository mockAgreementTypeRepository;
        @Mock
        private BankAccounts mockFinancialAccounts;
        @Mock
        private ClockService mockClockService;

        @Mock
        private DomainObjectContainer mockContainer;

        @Mock
        private AgreementRepository agreementRepository;

        private BankMandate bankMandate;

        private BankAccount bankAccount;
        private BankAccount someOtherBankAccount;

        private Party tenant;
        private Party landlord;

        private AgreementRoleType landlordAgreementRoleType;
        private AgreementRoleType tenantAgreementRoleType;
        private AgreementRoleType creditorAgreementRoleType;
        private AgreementRoleType debtorAgreementRoleType;

        private AgreementRole tenantAgreementRole;
        private AgreementRole landlordAgreementRole;

        private AgreementType bankMandateAgreementType;

        private LocalDate startDate;
        private LocalDate endDate;

        private SequenceType sequenceType;
        private Scheme scheme;
        private LocalDate signatureDate;

        @Before
        public void setUp() throws Exception {

            tenantAgreementRoleType = new AgreementRoleType();
            tenantAgreementRoleType.setTitle(LeaseConstants.ART_LANDLORD);
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).findByTitle(LeaseConstants.ART_LANDLORD);
                    will(returnValue(landlordAgreementRoleType));
                }
            });

            tenantAgreementRoleType = new AgreementRoleType();
            tenantAgreementRoleType.setTitle(LeaseConstants.ART_TENANT);
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).findByTitle(LeaseConstants.ART_TENANT);
                    will(returnValue(tenantAgreementRoleType));
                }
            });

            debtorAgreementRoleType = new AgreementRoleType();
            debtorAgreementRoleType.setTitle(BankMandateConstants.ART_DEBTOR);
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).findByTitle(BankMandateConstants.ART_DEBTOR);
                    will(returnValue(debtorAgreementRoleType));
                }
            });
            context.checking(new Expectations() {
                {
                    allowing(agreementRepository).findAgreementByReference("MANDATEREF");
                    will(returnValue(null));
                }
            });

            creditorAgreementRoleType = new AgreementRoleType();
            creditorAgreementRoleType.setTitle(BankMandateConstants.ART_CREDITOR);
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).findByTitle(BankMandateConstants.ART_CREDITOR);
                    will(returnValue(creditorAgreementRoleType));
                }
            });

            bankMandateAgreementType = new AgreementType();
            bankMandateAgreementType.setTitle(BankMandateConstants.AT_MANDATE);
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementTypeRepository).find(BankMandateConstants.AT_MANDATE);
                    will(returnValue(bankMandateAgreementType));
                }
            });

            context.checking(new Expectations() {
                {
                    allowing(mockClockService).now();
                    will(returnValue(new LocalDate(2013, 4, 2)));
                }
            });

            final ApplicationTenancy tenantApplicationTenancy = new ApplicationTenancy();
            tenantApplicationTenancy.setPath("/it");
            tenant = new PartyForTesting() {
                @Override
                public ApplicationTenancy getApplicationTenancy() {
                    return tenantApplicationTenancy;
                }
            };

            tenantAgreementRole = new AgreementRole();
            tenantAgreementRole.setParty(tenant);
            tenantAgreementRole.setType(tenantAgreementRoleType);
            tenantAgreementRole.clockService = mockClockService;

            landlord = new PartyForTesting();
            landlordAgreementRole = new AgreementRole();
            landlordAgreementRole.setParty(landlord);
            landlordAgreementRole.setType(landlordAgreementRoleType);
            landlordAgreementRole.clockService = mockClockService;

            bankMandate = new BankMandate();
            bankMandate.setContainer(mockContainer);

            bankAccount = new BankAccount();
            bankAccount.setReference("REF1");
            someOtherBankAccount = new BankAccount();

            startDate = new LocalDate(2013, 4, 1);
            endDate = new LocalDate(2013, 5, 2);

            sequenceType = SequenceType.FIRST;
            scheme = Scheme.CORE;
            signatureDate = new LocalDate(2013, 4, 1);

            // a mini integration test, since using the real BankMandateMenu and BankMandateRepository impl

            BankMandateRepository mandateRepository = new BankMandateRepository() {{
                setContainer(mockContainer);
                this.agreementTypeRepository = mockAgreementTypeRepository;
                this.agreementRoleTypeRepository = mockAgreementRoleTypeRepository;
            }};

            // the main class under test
            lease = new Lease();
            lease.setApplicationTenancyPath("/it");

            lease.agreementRoleTypeRepository = mockAgreementRoleTypeRepository;
            lease.agreementRoleRepository = mockAgreementRoleRepository;
            lease.agreementTypeRepository = mockAgreementTypeRepository;
            lease.financialAccounts = mockFinancialAccounts;
            lease.bankMandateRepository = mandateRepository;
            lease.setContainer(mockContainer);
            lease.clockService = mockClockService;
            lease.agreementRepository = agreementRepository;
        }

        @Test
        public void whenSecondaryPartyIsUnknown_isDisabled() {

            // given
            assertThat(lease.getRoles(), Matchers.empty());

            // when
            final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);

            // then
            assertThat(disabledReason, is("Could not determine the tenant (secondary party) of this lease"));
        }

        @Test
        public void whenSecondaryPartyIsKnownButNotCurrent_isDisabled() {

            // given
            tenantAgreementRole.setAgreement(lease);
            lease.getRoles().add(tenantAgreementRole);

            tenantAgreementRole.setEndDate(new LocalDate(2013, 4, 1));

            // when
            final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);

            // then
            assertThat(disabledReason, is(not(nullValue())));

            // and when/then
            // (defaultXxx wouldn't get called, but for coverage...)
            assertThat(lease.default0PaidBy(), is(nullValue()));
        }

        @Test
        public void whenSecondaryPartyIsKnownButNoBankAccounts_isDisabled() {

            // given
            tenantAgreementRole.setAgreement(lease);
            lease.getRoles().add(tenantAgreementRole);

            context.checking(new Expectations() {
                {
                    oneOf(mockFinancialAccounts).findBankAccountsByOwner(tenant);
                    will(returnValue(Collections.emptyList()));
                }
            });

            // when, then
            final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);
            assertThat(disabledReason, is(not(nullValue())));
        }

        @Test
        public void whenSecondaryPartyIsKnownAndHasBankAccounts_canInvoke() {

            // given
            tenantAgreementRole.setAgreement(lease);
            lease.getRoles().add(tenantAgreementRole);
            lease.getRoles().add(landlordAgreementRole);

            context.checking(new Expectations() {
                {
                    allowing(mockFinancialAccounts).findBankAccountsByOwner(tenant);
                    will(returnValue(Lists.newArrayList(bankAccount)));
                }
            });

            // when/then
            final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);
            assertThat(disabledReason, is(nullValue()));

            // and when/then
            final List<BankAccount> bankAccounts = lease.choices0NewMandate();
            assertThat(bankAccounts, Matchers.contains(bankAccount));

            // and when/then
            final BankAccount defaultBankAccount = lease.default0NewMandate();
            assertThat(defaultBankAccount, is(bankAccount));

            // and when/then
            final String validateReason = lease.validateNewMandate(defaultBankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);
            assertThat(validateReason, is(nullValue()));

            // and given
            assertThat(lease.getPaidBy(), is(nullValue()));
            final AgreementRole newBankMandateAgreementRoleForCreditor = new AgreementRole();
            final AgreementRole newBankMandateAgreementRoleForDebtor = new AgreementRole();

            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(BankMandate.class);
                    will(returnValue(bankMandate));

                    oneOf(mockContainer).persistIfNotAlready(bankMandate);

                    oneOf(mockContainer).newTransientInstance(AgreementRole.class);
                    will(returnValue(newBankMandateAgreementRoleForCreditor));

                    oneOf(mockContainer).newTransientInstance(AgreementRole.class);
                    will(returnValue(newBankMandateAgreementRoleForDebtor));

                    oneOf(mockContainer).persistIfNotAlready(newBankMandateAgreementRoleForCreditor);

                    oneOf(mockContainer).persistIfNotAlready(newBankMandateAgreementRoleForDebtor);
                }
            });

            // when
            final Lease returned = lease.newMandate(defaultBankAccount, "MANDATEREF", startDate, endDate, SequenceType.FIRST, Scheme.CORE, startDate);

            // then
            assertThat(returned, is(lease));

            assertThat(lease.getPaidBy(), is(bankMandate));
            Assertions.assertThat(lease.getPaidBy().getApplicationTenancyPath()).isEqualTo(lease.getApplicationTenancyPath());
            assertThat(bankMandate.getType(), is(bankMandateAgreementType));
            assertThat(bankMandate.getBankAccount(), is((FinancialAccount) bankAccount));
            assertThat(bankMandate.getStartDate(), is(startDate));
            assertThat(bankMandate.getEndDate(), is(endDate));
            assertThat(bankMandate.getReference(), is("MANDATEREF"));
            assertThat(bankMandate.getSequenceType(), is(SequenceType.FIRST));
            assertThat(bankMandate.getScheme(), is(Scheme.CORE));
            assertThat(bankMandate.getSignatureDate(), is(startDate));

            assertThat(newBankMandateAgreementRoleForCreditor.getAgreement(), is((Agreement) bankMandate));
            assertThat(newBankMandateAgreementRoleForCreditor.getParty(), is(landlord));
            assertThat(newBankMandateAgreementRoleForDebtor.getAgreement(), is((Agreement) bankMandate));
            assertThat(newBankMandateAgreementRoleForDebtor.getParty(), is(tenant));
        }

        @Test
        public void whenPrereqs_validateWithIncorrectBankAccount() {

            // given
            tenantAgreementRole.setAgreement(lease);
            lease.getRoles().add(tenantAgreementRole);

            context.checking(new Expectations() {
                {
                    oneOf(mockFinancialAccounts).findBankAccountsByOwner(tenant);
                    will(returnValue(Lists.newArrayList(bankAccount)));
                }
            });

            // when/then
            final String validateReason = lease.validateNewMandate(someOtherBankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);
            assertThat(validateReason, is("Bank account is not owned by this lease's tenant"));
        }

    }

    public static class PaidBy extends LeaseTest {

        @Mock
        private AgreementRoleTypeRepository mockAgreementRoleTypeRepository;
        @Mock
        private AgreementTypeRepository mockAgreementTypeRepository;
        @Mock
        private AgreementRepository mockAgreementRepository;
        @Mock
        private ClockService mockClockService;

        @Mock
        private DomainObjectContainer mockContainer;

        private BankMandate bankMandate;
        private BankMandate someOtherBankMandate;

        private Party tenant;
        private AgreementRoleType tenantAgreementRoleType;
        private AgreementRoleType debtorAgreementRoleType;
        private AgreementRole arTenant;

        private AgreementType bankMandateAgreementType;

        @Before
        public void setUp() throws Exception {

            tenantAgreementRoleType = new AgreementRoleType();
            tenantAgreementRoleType.setTitle(LeaseConstants.ART_TENANT);
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).findByTitle(LeaseConstants.ART_TENANT);
                    will(returnValue(tenantAgreementRoleType));
                }
            });

            debtorAgreementRoleType = new AgreementRoleType();
            debtorAgreementRoleType.setTitle(BankMandateConstants.ART_DEBTOR);
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).findByTitle(BankMandateConstants.ART_DEBTOR);
                    will(returnValue(debtorAgreementRoleType));
                }
            });

            bankMandateAgreementType = new AgreementType();
            bankMandateAgreementType.setTitle(BankMandateConstants.AT_MANDATE);
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementTypeRepository).find(BankMandateConstants.AT_MANDATE);
                    will(returnValue(bankMandateAgreementType));
                }
            });

            context.checking(new Expectations() {
                {
                    allowing(mockClockService).now();
                    will(returnValue(new LocalDate(2013, 4, 2)));
                }
            });

            tenant = new PartyForTesting();
            arTenant = new AgreementRole();
            arTenant.setParty(tenant);
            arTenant.setType(tenantAgreementRoleType);
            arTenant.clockService = mockClockService;

            bankMandate = new BankMandate();
            someOtherBankMandate = new BankMandate();

            lease = new Lease();
            lease.agreementRoleTypeRepository = mockAgreementRoleTypeRepository;
            lease.agreementRepository = mockAgreementRepository;
            lease.agreementTypeRepository = mockAgreementTypeRepository;
            lease.clockService = mockClockService;
        }

        @Test
        public void whenSecondaryPartyIsUnknown_isDisabled() {

            // given
            assertThat(lease.getRoles(), Matchers.empty());

            // when
            final String reason = lease.disablePaidBy(bankMandate);

            // then
            assertThat(reason, is("There are no valid mandates; set one up using 'New Mandate'"));
        }

        @Test
        public void whenSecondaryPartyIsKnownButNoMandates_isDisabled() {

            // given
            arTenant.setAgreement(lease);
            lease.getRoles().add(arTenant);

            context.checking(new Expectations() {
                {
                    oneOf(mockAgreementRepository).findByAgreementTypeAndRoleTypeAndParty(bankMandateAgreementType, debtorAgreementRoleType, tenant);
                    will(returnValue(Collections.emptyList()));
                }
            });

            // when, then
            final String disabledReason = lease.disablePaidBy(bankMandate);
            assertThat(disabledReason, is(not(nullValue())));
        }

        @Test
        public void whenSecondaryPartyIsKnownButNotCurrent_isDisabled() {

            // given
            arTenant.setAgreement(lease);
            lease.getRoles().add(arTenant);

            arTenant.setEndDate(new LocalDate(2013, 4, 1));

            context.checking(new Expectations() {
                {
                    never(mockAgreementRepository);
                }
            });

            final String reason = lease.disablePaidBy(bankMandate);

            // then
            assertThat(reason, is(not(nullValue())));

            // and when/then
            // (defaultXxx wouldn't get called, but for coverage...)
            assertThat(lease.default0PaidBy(), is(nullValue()));
        }

        @Test
        public void whenSecondaryPartyIsKnownAndHasMandates_canInvoke() {

            // given
            arTenant.setAgreement(lease);
            lease.getRoles().add(arTenant);

            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRepository).findByAgreementTypeAndRoleTypeAndParty(bankMandateAgreementType, debtorAgreementRoleType, tenant);
                    will(returnValue(Lists.newArrayList(bankMandate)));
                }
            });

            // when/then
            final String disabledReason = lease.disablePaidBy(bankMandate);
            assertThat(disabledReason, is(nullValue()));

            // and when/then
            final List<BankMandate> bankMandates = lease.choices0PaidBy();
            assertThat(bankMandates, Matchers.contains(bankMandate));

            // and when/then
            final BankMandate defaultBankMandate = lease.default0PaidBy();
            assertThat(defaultBankMandate, is(bankMandate));

            // and when/then
            final String validateReason = lease.validatePaidBy(bankMandate);
            assertThat(validateReason, is(nullValue()));

            // and given
            assertThat(lease.getPaidBy(), is(nullValue()));

            // when
            final Lease returned = lease.paidBy(bankMandate);

            // then
            assertThat(lease.getPaidBy(), is(bankMandate));
            assertThat(returned, is(lease));
        }

        @Test
        public void whenPrereqs_butValidateWithOtherBankMandate_isInvalid() {

            // given
            arTenant.setAgreement(lease);
            lease.getRoles().add(arTenant);

            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRepository).findByAgreementTypeAndRoleTypeAndParty(bankMandateAgreementType, debtorAgreementRoleType, tenant);
                    will(returnValue(Lists.newArrayList(bankMandate)));
                }
            });

            // when/then
            final String validateReason = lease.validatePaidBy(someOtherBankMandate);
            assertThat(validateReason, is("Invalid mandate; the mandate's debtor must be this lease's tenant"));
        }

    }

    public static class TenancyDuration extends LeaseTest {

        @Mock
        private DomainObjectContainer mockContainer;

        @Before
        public void setUp() throws Exception {
            lease = new Lease();
        }

        @Test
        public void testAllDatesSet() {
            LocalDate startDate = new LocalDate(2012, 8, 01);
            LocalDate endDate = new LocalDate(2016, 7, 30);

            lease.setStartDate(startDate);
            lease.setEndDate(endDate);
            lease.setTenancyStartDate(startDate);
            lease.setTenancyEndDate(endDate);

            assertThat(lease.getTenancyDuration(), is("3y11m30d"));
        }

        @Test
        public void testNoLeaseEndDateSet() {
            LocalDate startDate = new LocalDate(2012, 8, 01);
            lease.setStartDate(startDate);
            lease.setTenancyStartDate(startDate);

            assertNull(lease.getTenancyDuration());
        }

        @Test
        public void testNoLeaseStartDateSet() {
            LocalDate endDate = new LocalDate(2016, 7, 31);
            lease.setEndDate(endDate);
            lease.setTenancyEndDate(endDate);

            assertNull(lease.getTenancyDuration());
        }

        @Test
        public void testNoTenancyDatesSet() {
            LocalDate startDate = new LocalDate(2012, 8, 01);
            LocalDate endDate = new LocalDate(2016, 7, 31);

            lease.setStartDate(startDate);
            lease.setEndDate(endDate);

            assertNull(lease.getTenancyDuration());
        }

        @Test
        public void testEndDateBeforeStartDate() {
            LocalDate startDate = new LocalDate(2012, 8, 01);
            LocalDate endDate = new LocalDate(2016, 7, 31);

            lease.setStartDate(startDate);
            lease.setEndDate(endDate);

            lease.setTenancyStartDate(endDate);
            lease.setTenancyEndDate(startDate);

            assertNull(lease.getTenancyDuration());
        }
    }

    public static class ValidateChangePrevious extends AgreementTest {

        Lease agreement;
        Lease otherAgreement;
        Lease previousAgreement;

        @Before
        public void setUp() throws Exception {
            agreement = new Lease();
            otherAgreement = new Lease();
            previousAgreement = new Lease();
        }

        @Test
        public void nullValueIsOK() {

            // given
            previousAgreement.setNext(otherAgreement);

            // when, then
            assertNull(agreement.validateChangePrevious(null));

        }

        @Test
        public void previousHasNextAlready() {

            // given
            previousAgreement.setNext(otherAgreement);

            // when, then
            assertThat(agreement.validateChangePrevious(previousAgreement), is("Not allowed: the agreement chosen already is already linked to a next."));

        }

        @Test
        public void overlappingIntervals() {

            // given
            previousAgreement.setEndDate(new LocalDate("2000-01-01"));
            previousAgreement.setApplicationTenancyPath("/SomePath");
            agreement.setApplicationTenancyPath("/SomePath");

            // when
            agreement.setStartDate(new LocalDate("2000-01-01"));
            // then
            assertThat(agreement.validateChangePrevious(previousAgreement), is("Not allowed: overlapping date intervals"));

            // when
            agreement.setStartDate(new LocalDate("2000-01-02"));
            // then
            assertNull(agreement.validateChangePrevious(previousAgreement));

        }

        @Test
        public void previousAgreementIntervalNotBeforeStartdate() {

            // given
            previousAgreement.setStartDate(new LocalDate("1999-01-01"));
            previousAgreement.setEndDate(new LocalDate("1999-12-31"));
            previousAgreement.setApplicationTenancyPath("/SomePath");
            agreement.setApplicationTenancyPath("/SomePath");
            agreement.setStartDate(new LocalDate("1998-01-01"));
            agreement.setEndDate(new LocalDate("1998-12-31"));

            // then
            assertThat(agreement.validateChangePrevious(previousAgreement), is("Not allowed: previous agreement interval should be before this agreements interval"));

        }

        @Test
        public void atPathNotTheSame() {

            // given
            previousAgreement.setEndDate(new LocalDate("2000-01-01"));
            previousAgreement.setApplicationTenancyPath("/SomePath");
            agreement.setStartDate(new LocalDate("2000-01-02"));

            // when
            agreement.setApplicationTenancyPath("/SomeOtherPath");
            // then
            assertThat(agreement.validateChangePrevious(previousAgreement), is("Not allowed: application tenancy should be equal"));

            // when
            agreement.setApplicationTenancyPath("/SomePath");
            // then
            assertNull(agreement.validateChangePrevious(previousAgreement));

        }

    }

}
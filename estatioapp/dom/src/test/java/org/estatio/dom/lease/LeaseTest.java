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
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester;
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
import org.estatio.dom.asset.Unit;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandateConstants;
import org.estatio.dom.bankmandate.BankMandateRepository;
import org.estatio.dom.bankmandate.Scheme;
import org.estatio.dom.bankmandate.SequenceType;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LeaseTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    Lease lease;

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

        @Before
        public void setUp() throws Exception {
            lease = new Lease();
        }

        @Test
        public void testWhenActived() {
            lease.setStatus(LeaseStatus.ACTIVE);
            assertTrue(lease.hideResumeAll());
            assertFalse(lease.hideSuspendAll());
            assertNull(lease.disableTerminate());
        }

        @Test
        public void testWhenSuspendedPartially() {
            lease.setStatus(LeaseStatus.SUSPENDED_PARTIALLY);
            assertNull(lease.disableTerminate());
        }

        @Test
        public void testWhenSuspended() {
            lease.setStatus(LeaseStatus.SUSPENDED);
            assertFalse(lease.hideResumeAll());
            assertTrue(lease.hideSuspendAll());
            assertThat(lease.disableTerminate()).isEqualTo("Status is not Active or Suspended Partially");
        }

        @Test
        public void testWhenTerminated() {
            lease.setStatus(LeaseStatus.TERMINATED);
            assertTrue(lease.hideResumeAll());
            assertTrue(lease.hideSuspendAll());
            assertThat(lease.disableTerminate()).isEqualTo("Status is not Active or Suspended Partially");
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
            assertThat(lease.getEffectiveStatus()).isEqualTo(LeaseStatus.SUSPENDED);
            item2.setStatus(LeaseItemStatus.ACTIVE);
            assertThat(lease.getEffectiveStatus()).isEqualTo(LeaseStatus.SUSPENDED_PARTIALLY);
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
            assertThat(lease.getEffectiveInterval().endDateExcluding()).isEqualTo(new LocalDate(2012, 7, 1));
        }

    }

    public static class NewItem extends LeaseTest {

        @Mock
        private LeaseItemRepository mockLeaseItemRepository;

        @Before
        public void setUp() throws Exception {
            lease = new Lease();
            lease.leaseItemRepository = mockLeaseItemRepository;
        }

        @Test
        public void happy_case() {
            //Given

            final LeaseItemType leaseItemType = LeaseItemType.RENT;
            final Charge charge = new Charge();
            final InvoicingFrequency invoicingFrequency = InvoicingFrequency.MONTHLY_IN_ADVANCE;
            final PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;
            final LocalDate startDate = new LocalDate(2013, 1, 1);

            // Then
            context.checking(new Expectations() {
                {
                    oneOf(mockLeaseItemRepository).newLeaseItem(lease, leaseItemType, charge, invoicingFrequency, paymentMethod, startDate);
                }
            });

            // When
            lease.newItem(leaseItemType, charge, invoicingFrequency, paymentMethod, startDate);
        }

    }

    public static class DisableNewItem extends LeaseTest {

        @Test
        public void test() {
            // given
            Lease lease = new Lease();

            // when
            assertThat(lease.getProperty()).isNull();

            // then
            assertThat(lease.disableNewItem()).isEqualTo("Please set occupancy first");

        }

    }

    public static class DefaultPaymentMehodForNewItem extends LeaseTest {

        @Test
        public void test() {

            // given
            Lease lease = new Lease();
            LeaseItem itemFirst = new LeaseItem();
            itemFirst.setPaymentMethod(PaymentMethod.CHEQUE);
            LeaseItem itemLast = new LeaseItem();
            itemLast.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            lease.setItems(new TreeSet<>(Arrays.asList(itemLast, itemFirst)));

            // when
            // then
            assertThat(lease.default3NewItem()).isEqualTo(itemLast.getPaymentMethod());

        }

        @Test
        public void testNoItems() {

            // given
            Lease lease = new Lease();
            lease.setItems(new TreeSet<>(Arrays.asList()));

            // when
            // then
            assertThat(lease.default3NewItem()).isEqualTo(null);

        }

    }

    public static class ChangePaymentMethodForAll extends LeaseTest {

        PaymentMethod newPaymentMethodForAll;

        @Test
        public void test() {

            // given
            newPaymentMethodForAll = PaymentMethod.DIRECT_DEBIT;

            LeaseItem item1 = new LeaseItem();
            LeaseItem item2 = new LeaseItem();
            LeaseItem item3 = new LeaseItem();
            item1.setPaymentMethod(PaymentMethod.CHEQUE);
            item2.setPaymentMethod(PaymentMethod.BANK_TRANSFER);
            Lease lease = new Lease() {
                @Override
                public SortedSet<LeaseItem> getItems() {
                    return new TreeSet<>(Arrays.asList(item3, item2, item1));
                }
            };

            // when
            lease.changePaymentMethodForAll(newPaymentMethodForAll);

            // then
            for (LeaseItem item : lease.getItems()) {
                assertThat(item.getPaymentMethod()).isEqualTo(newPaymentMethodForAll);
            }

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
        private BankAccountRepository mockBankAccountRepository;
        @Mock
        private ClockService mockClockService;
        @Mock
        private DomainObjectContainer mockContainer;
        @Mock
        private BankMandateRepository mockBankMandateRepository;

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
                    allowing(mockBankMandateRepository).findByReference("MANDATEREF");
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

            // the main class under test
            lease = new Lease();
            lease.setApplicationTenancyPath("/it");

            lease.agreementRoleTypeRepository = mockAgreementRoleTypeRepository;
            lease.agreementRoleRepository = mockAgreementRoleRepository;
            lease.agreementTypeRepository = mockAgreementTypeRepository;
            lease.bankAccountRepository = mockBankAccountRepository;
            lease.clockService = mockClockService;
            lease.bankMandateRepository = mockBankMandateRepository;
        }

        @Test
        public void whenSecondaryPartyIsUnknown_isDisabled() {

            // given
            assertThat(lease.getRoles()).hasSize(0);

            // when
            final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);

            // then
            assertThat(disabledReason).isEqualTo("Could not determine the tenant (secondary party) of this lease");
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
            assertThat(disabledReason).isNotNull();

            // and when/then
            // (defaultXxx wouldn't get called, but for coverage...)
            assertThat(lease.default0PaidBy()).isNull();
        }

        @Test
        public void whenSecondaryPartyIsKnownButNoBankAccounts_isDisabled() {

            // given
            tenantAgreementRole.setAgreement(lease);
            lease.getRoles().add(tenantAgreementRole);

            context.checking(new Expectations() {
                {
                    oneOf(mockBankAccountRepository).findBankAccountsByOwner(tenant);
                    will(returnValue(Collections.emptyList()));
                }
            });

            // when, then
            final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);
            assertThat(disabledReason).isNotNull();
        }

        @Test
        public void whenSecondaryPartyIsKnownAndHasBankAccounts_canInvoke() {

            // given
            tenantAgreementRole.setAgreement(lease);
            lease.getRoles().add(tenantAgreementRole);
            lease.getRoles().add(landlordAgreementRole);

            context.checking(new Expectations() {
                {
                    allowing(mockBankAccountRepository).findBankAccountsByOwner(tenant);
                    will(returnValue(Lists.newArrayList(bankAccount)));
                }
            });

            // when/then
            final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);
            assertThat(disabledReason).isNull();

            // and when/then
            final List<BankAccount> bankAccounts = lease.choices0NewMandate();
            assertThat(bankAccounts).contains(bankAccount);

            // and when/then
            final BankAccount defaultBankAccount = lease.default0NewMandate();
            assertThat(defaultBankAccount).isEqualTo(bankAccount);

            // and when/then
            final String validateReason = lease.validateNewMandate(defaultBankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);
            assertThat(validateReason).isNull();

            // and given
            assertThat(lease.getPaidBy()).isNull();
            final AgreementRole newBankMandateAgreementRoleForCreditor = new AgreementRole();
            final AgreementRole newBankMandateAgreementRoleForDebtor = new AgreementRole();

            context.checking(new Expectations() {
                {
                    oneOf(mockBankMandateRepository)
                            .newBankMandate(with(any(String.class)), with(any(String.class)), with(any(LocalDate.class)), with(any(LocalDate.class)), with(any(Party.class)), with(any(Party.class)), with(any(BankAccount.class)), with(any(SequenceType.class)), with(any(Scheme.class)),
                                    with(any(LocalDate.class)));
                    will(returnValue(bankMandate));

                }
            });

            // when
            final Lease returned = lease.newMandate(defaultBankAccount, "MANDATEREF", startDate, endDate, SequenceType.FIRST, Scheme.CORE, startDate);

            // then
            assertThat(returned).isEqualTo(lease);

            assertThat(lease.getPaidBy()).isEqualTo(bankMandate);
        }

        @Test
        public void whenPrereqs_validateWithIncorrectBankAccount() {

            // given
            tenantAgreementRole.setAgreement(lease);
            lease.getRoles().add(tenantAgreementRole);

            context.checking(new Expectations() {
                {
                    oneOf(mockBankAccountRepository).findBankAccountsByOwner(tenant);
                    will(returnValue(Lists.newArrayList(bankAccount)));
                }
            });

            // when/then
            final String validateReason = lease.validateNewMandate(someOtherBankAccount, "MANDATEREF", startDate, endDate, sequenceType, scheme, signatureDate);
            assertThat(validateReason).isEqualTo("Bank account is not owned by this lease's tenant");
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
            assertThat(lease.getRoles()).isEmpty();

            // when
            final String reason = lease.disablePaidBy(bankMandate);

            // then
            assertThat(reason).isEqualTo("There are no valid mandates; set one up using 'New Mandate'");
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
            assertThat(disabledReason).isNotNull();
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
            assertThat(reason).isNotNull();

            // and when/then
            // (defaultXxx wouldn't get called, but for coverage...)
            assertThat(lease.default0PaidBy()).isNull();
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
            assertThat(disabledReason).isNull();

            // and when/then
            final List<BankMandate> bankMandates = lease.choices0PaidBy();
            assertThat(bankMandates).contains(bankMandate);

            // and when/then
            final BankMandate defaultBankMandate = lease.default0PaidBy();
            assertThat(defaultBankMandate).isEqualTo(bankMandate);

            // and when/then
            final String validateReason = lease.validatePaidBy(bankMandate);
            assertThat(validateReason).isNull();

            // and given
            assertThat(lease.getPaidBy()).isNull();

            // when
            final Lease returned = lease.paidBy(bankMandate);

            // then
            assertThat(lease.getPaidBy()).isEqualTo(bankMandate);
            assertThat(returned).isEqualTo(lease);
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
            assertThat(validateReason).isEqualTo("Invalid mandate; the mandate's debtor must be this lease's tenant");
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

            assertThat(lease.getTenancyDuration()).isEqualTo("3y11m30d");
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
            assertThat(agreement.validateChangePrevious(previousAgreement)).isEqualTo("Not allowed: the agreement chosen already is already linked to a next.");

        }

        @Test
        public void overlappingIntervals() {

            // given
            previousAgreement.setEndDate(new LocalDate("2000-01-01"));
            previousAgreement.setTenancyEndDate(new LocalDate("2000-01-01"));
            previousAgreement.setApplicationTenancyPath("/SomePath");
            agreement.setApplicationTenancyPath("/SomePath");

            // when
            agreement.setStartDate(new LocalDate("2000-01-01"));
            agreement.setTenancyStartDate(new LocalDate("2000-01-01"));
            // then
            assertThat(agreement.validateChangePrevious(previousAgreement)).isEqualTo("Not allowed: overlapping date intervals");

            // when
            agreement.setStartDate(new LocalDate("2000-01-02"));
            agreement.setTenancyStartDate(new LocalDate("2000-01-02"));
            // then
            assertThat(previousAgreement.getEffectiveInterval().toString()).isEqualTo("----------/2000-01-02");
            assertThat(agreement.getEffectiveInterval().toString()).isEqualTo("2000-01-02/----------");
            assertNull(agreement.validateChangePrevious(previousAgreement));

        }

        @Test
        public void previousAgreementIntervalNotBeforeStartdate() {

            // given
            previousAgreement.setTenancyStartDate(new LocalDate("1999-01-01"));
            previousAgreement.setTenancyEndDate(new LocalDate("1999-12-31"));
            previousAgreement.setApplicationTenancyPath("/SomePath");
            agreement.setApplicationTenancyPath("/SomePath");
            agreement.setTenancyStartDate(new LocalDate("1998-01-01"));
            agreement.setTenancyEndDate(new LocalDate("1998-12-31"));

            // then
            assertThat(agreement.validateChangePrevious(previousAgreement)).isEqualTo("Not allowed: previous agreement interval should be before this agreements interval");

        }

        @Test
        public void atPathNotTheSame() {

            // given
            previousAgreement.setTenancyEndDate(new LocalDate("2000-01-01"));
            previousAgreement.setApplicationTenancyPath("/SomePath");
            agreement.setTenancyStartDate(new LocalDate("2000-01-02"));

            // when
            agreement.setApplicationTenancyPath("/SomeOtherPath");
            // then

            assertThat(agreement.validateChangePrevious(previousAgreement)).isEqualTo("Not allowed: application tenancy should be equal");

            // when
            agreement.setApplicationTenancyPath("/SomePath");
            // then
            assertNull(agreement.validateChangePrevious(previousAgreement));

        }

    }

    public static class PrimaryOccupancy {

        @Test
        public void highest_start_date_first() throws Exception {
            Lease lease = new Lease();
            lease.getOccupancies().add(newOccupancy(new LocalDate(2014, 1, 1), "100.00"));
            lease.getOccupancies().add(newOccupancy(new LocalDate(2015, 1, 1), "100.00"));
            lease.getOccupancies().add(newOccupancy(new LocalDate(2013, 1, 1), "100.00"));
            assertThat(lease.getOccupancies()).hasSize(3);
            assertThat(lease.primaryOccupancy().get().getStartDate()).isEqualTo(new LocalDate(2015, 1, 1));
        }

        @Test
        public void null_start_date_first() throws Exception {
            Lease lease = new Lease();
            lease.getOccupancies().add(newOccupancy(new LocalDate(2014, 1, 1), "100.00"));
            lease.getOccupancies().add(newOccupancy(null, "100.00"));
            lease.getOccupancies().add(newOccupancy(new LocalDate(2013, 1, 1), "100.00"));
            assertThat(lease.getOccupancies()).hasSize(3);
            assertThat(lease.primaryOccupancy().get().getStartDate()).isNull();
        }

        @Test
        public void largest_area_first() throws Exception {
            Lease lease = new Lease();
            lease.getOccupancies().add(newOccupancy(new LocalDate(2014, 1, 1), "99.00"));
            lease.getOccupancies().add(newOccupancy(new LocalDate(2014, 1, 1), "100.00"));
            lease.getOccupancies().add(newOccupancy(new LocalDate(2014, 1, 1), "88.00"));
            assertThat(lease.getOccupancies()).hasSize(3);
            assertThat(lease.primaryOccupancy().get().getUnit().getArea()).isEqualTo(new BigDecimal("100.00"));
        }

        @Test
        public void null_when_no_occupancies() throws Exception {
            Lease lease = new Lease();
            assertThat(lease.getOccupancies()).hasSize(0);
            assertThat(lease.primaryOccupancy().isPresent()).isFalse();
        }

        private Occupancy newOccupancy(final LocalDate startDate, final String area) {
            Occupancy o = new Occupancy();
            o.setStartDate(startDate);
            Unit unit = new Unit();
            unit.setReference(area);
            unit.setName(area);
            unit.setArea(new BigDecimal(area));
            o.setUnit(unit);
            return o;
        }

    }

    public static class Renew extends LeaseTest {

        @Test
        public void disabled_when_there_is_a_next_lease() throws Exception {
           //Given
            Lease lease = new Lease();
            lease.setNext(new Lease());

            //When, Then
            assertThat(lease.disableRenew()).isNotNull();
        }
    }


    public static class RenewKeepingThis extends LeaseTest {

        @Mock
        LeaseRepository mockLeaseRepository;

        @Mock
        ApplicationTenancyRepository mockApplicationTenancyRepository;

        @Test
        public void happy_case() throws Exception {
            //Given
            final Organisation tenant = new Organisation();
            final Organisation landlord = new Organisation();
            LeaseForTest lease = new LeaseForTest(){
                @Override public Party getPrimaryParty() {
                    return landlord;
                }
                @Override public Party getSecondaryParty() {
                    return tenant;
                }
            };
            lease.setReference("OXF-HELLO-123");
            lease.setName("OXF-HELLO-123");
            lease.setComments("Comments");
            final LocalDate startDate = new LocalDate(2000, 1, 1);
            lease.setStartDate(startDate);
            final LocalDate endDate = new LocalDate(2009, 12, 31);
            lease.setEndDate(endDate);
            lease.setTenancyStartDate(startDate);
            lease.leaseRepository = mockLeaseRepository;
            lease.setSecurityApplicationTenancyRepository(mockApplicationTenancyRepository);

            Lease previousLease = new Lease();

            final LocalDate newStartDate = new LocalDate(2010, 1, 1);
            final LocalDate newEndDate = new LocalDate(2019, 12, 31);

            //Then
            context.checking(new Expectations() {
                {
                    allowing(mockApplicationTenancyRepository).findByPathCached(with(aNull(String.class)));
                    final ApplicationTenancy applicationTenancy = new ApplicationTenancy();
                    will(returnValue(applicationTenancy));
                    allowing(mockLeaseRepository).newLease(
                            applicationTenancy,
                            "OXF-HELLO-123_",
                            "OXF-HELLO-123 - Archived",
                            null,
                            startDate,
                            endDate,
                            startDate,
                            newStartDate.minusDays(1),
                            landlord,
                            tenant);
                    will(returnValue(previousLease));
                }
            });

            //When
            lease.renewKeepingThis(newStartDate, newEndDate);

            //Then
            assertThat(previousLease.getNext()).isEqualTo(lease);
            assertThat(previousLease.getComments()).isEqualTo("Comments");

            assertThat(lease.getStartDate()).isEqualTo(newStartDate);
            assertThat(lease.getEndDate()).isEqualTo(newEndDate);
            assertThat(lease.getTenancyEndDate()).isEqualTo(newEndDate);

        }

        @Test
        public void disabled_when_previous_lease_found() throws Exception {
            //Given
            Lease lease =  new Lease();
            lease.setNext(new Lease());
            //When, Then
            assertThat(lease.disableRenewKeepingThis()).isNotNull();

        }

        @Test
        public void disabled_when_next_lease_found() throws Exception {
            //Given
            Lease lease =  new Lease();
            lease.setPrevious(new Lease());
            //When, Then
            assertThat(lease.disableRenewKeepingThis()).isNotNull();

        }


    }

}
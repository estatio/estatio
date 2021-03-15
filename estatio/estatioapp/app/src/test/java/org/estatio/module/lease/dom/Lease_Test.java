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
package org.estatio.module.lease.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Lists;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementForTesting;
import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.Agreement_Test;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.Unit;
import org.estatio.module.asset.dom.UnitRepository;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateAgreementRoleTypeEnum;
import org.estatio.module.bankmandate.dom.BankMandateAgreementTypeEnum;
import org.estatio.module.bankmandate.dom.BankMandateRepository;
import org.estatio.module.bankmandate.dom.Scheme;
import org.estatio.module.bankmandate.dom.SequenceType;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyForTesting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Lease_Test {

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

    public static class ChangeStatus extends Lease_Test {

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

    public static class GetEffectiveInterval extends Lease_Test {

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

    public static class NewItem extends Lease_Test {

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
                    oneOf(mockLeaseItemRepository).newLeaseItem(lease, leaseItemType, LeaseAgreementRoleTypeEnum.LANDLORD, charge, invoicingFrequency, paymentMethod, startDate);
                }
            });

            // When
            lease.newItem(leaseItemType, LeaseAgreementRoleTypeEnum.LANDLORD, charge, invoicingFrequency, paymentMethod, startDate);
        }

    }

    public static class DisableNewItem extends Lease_Test {

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

    public static class DefaultPaymentMethod extends Lease_Test {

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
            assertThat(lease.defaultPaymentMethod()).isEqualTo(itemLast.getPaymentMethod());

        }

        @Test
        public void testNoItems() {

            // given
            Lease lease = new Lease();
            lease.setItems(new TreeSet<>(Arrays.asList()));

            // when
            // then
            assertThat(lease.defaultPaymentMethod()).isEqualTo(null);

        }

    }

    public static class ChangePaymentMethodForAll extends Lease_Test {

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

    public static class NewMandate extends Lease_Test {

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
            tenantAgreementRoleType.setTitle(LeaseAgreementRoleTypeEnum.LANDLORD.getTitle());
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).findByTitle(LeaseAgreementRoleTypeEnum.LANDLORD.getTitle());
                    will(returnValue(landlordAgreementRoleType));
                }
            });

            tenantAgreementRoleType = new AgreementRoleType();
            tenantAgreementRoleType.setTitle(LeaseAgreementRoleTypeEnum.TENANT.getTitle());
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).findByTitle(LeaseAgreementRoleTypeEnum.TENANT.getTitle());
                    will(returnValue(tenantAgreementRoleType));
                }
            });

            debtorAgreementRoleType = new AgreementRoleType();
            debtorAgreementRoleType.setTitle(BankMandateAgreementRoleTypeEnum.DEBTOR.getTitle());
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).find(BankMandateAgreementRoleTypeEnum.DEBTOR);
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
            creditorAgreementRoleType.setTitle(BankMandateAgreementRoleTypeEnum.CREDITOR.getTitle());
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).find(BankMandateAgreementRoleTypeEnum.CREDITOR);
                    will(returnValue(creditorAgreementRoleType));
                }
            });

            bankMandateAgreementType = new AgreementType();
            bankMandateAgreementType.setTitle(BankMandateAgreementTypeEnum.MANDATE.getTitle());
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementTypeRepository).find(BankMandateAgreementTypeEnum.MANDATE);
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
            final String disabledReason = lease.disableNewMandate();

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
            final String disabledReason = lease.disableNewMandate();

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
            final String disabledReason = lease.disableNewMandate();
            assertThat(disabledReason).isNotNull();
        }

        @Test
        public void whenSecondaryPartyIsKnownAndHasBankAccounts_canInvoke() {

            // given
            tenantAgreementRole.setAgreement(lease);
            lease.getRoles().add(tenantAgreementRole);
            lease.getRoles().add(landlordAgreementRole);
            LeaseItem leaseItem = new LeaseItem();
            leaseItem.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            lease.getItems().add(leaseItem);

            context.checking(new Expectations() {
                {
                    allowing(mockBankAccountRepository).findBankAccountsByOwner(tenant);
                    will(returnValue(Lists.newArrayList(bankAccount)));
                }
            });

            // when/then
            final String disabledReason = lease.disableNewMandate();
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

    public static class PaidBy extends Lease_Test {

        @Mock
        private AgreementRoleTypeRepository mockAgreementRoleTypeRepository;
        @Mock
        private AgreementTypeRepository mockAgreementTypeRepository;
        @Mock
        private AgreementRepository mockAgreementRepository;
        @Mock
        private ClockService mockClockService;

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
            tenantAgreementRoleType.setTitle(LeaseAgreementRoleTypeEnum.TENANT.getTitle());
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).findByTitle(LeaseAgreementRoleTypeEnum.TENANT.getTitle());
                    will(returnValue(tenantAgreementRoleType));
                }
            });

            debtorAgreementRoleType = new AgreementRoleType();
            debtorAgreementRoleType.setTitle(BankMandateAgreementRoleTypeEnum.DEBTOR.getTitle());
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementRoleTypeRepository).find(BankMandateAgreementRoleTypeEnum.DEBTOR);
                    will(returnValue(debtorAgreementRoleType));
                }
            });

            bankMandateAgreementType = new AgreementType();
            bankMandateAgreementType.setTitle(BankMandateAgreementTypeEnum.MANDATE.getTitle());
            context.checking(new Expectations() {
                {
                    allowing(mockAgreementTypeRepository).find(BankMandateAgreementTypeEnum.MANDATE);
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
            final String reason = lease.disablePaidBy();

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
            final String disabledReason = lease.disablePaidBy();
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

            final String reason = lease.disablePaidBy();

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
            final String disabledReason = lease.disablePaidBy();
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

    public static class TenancyDuration extends Lease_Test {

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

    public static class ValidateChangePrevious extends Agreement_Test {

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

    public static class ChoicesNewOccupancy extends Lease_Test {

        @Mock
        UnitRepository mockUnitRepository;

        Property property;
        Occupancy occupancy;

        @Test
        public void choices_first_new_occupancy_uses_all_units() {

            //expect
            context.checking(new Expectations() {
                {
                    oneOf(mockUnitRepository).allUnits();
                }
            });

            // given
            Lease newLease = new Lease(){
                @Override
                public SortedSet<Occupancy> getOccupancies(){
                    return Collections.emptySortedSet();
                }
            };
            newLease.unitRepository = mockUnitRepository;

            // when
            newLease.choices1NewOccupancy();

        }

        @Before
        public void setupOccupancy(){
            property = new Property();
            Unit unit = new Unit();
            unit.setProperty(property);
            occupancy = new Occupancy();
            occupancy.setUnit(unit);
        }

        @Test
        public void choices_not_first_new_occupancy_uses_find_by_property() {

            //expect
            context.checking(new Expectations() {
                {
                    oneOf(mockUnitRepository).findByProperty(property);
                }
            });

            // given
            Lease newLease = new Lease(){
                @Override
                public SortedSet<Occupancy> getOccupancies(){
                    SortedSet<Occupancy> occupancies = new TreeSet<>(Arrays.asList(occupancy));
                    return occupancies;
                }
            };
            newLease.unitRepository = mockUnitRepository;

            // when
            newLease.choices1NewOccupancy();

        }

    }

    public static class Renew extends Lease_Test {

        @Test
        public void disabled_when_there_is_a_next_lease() throws Exception {
           //Given
            Lease lease = new Lease();
            lease.setNext(new Lease());

            //When, Then
            assertThat(lease.disableRenew()).isNotNull();
        }
    }

    public static class CopyToNewLease extends Lease_Test {

        Lease newLease;
        LeaseItem newLeaseItem;
        LocalDate epochDate;

        @Before
        public void setUp(){
            newLeaseItem = new LeaseItem();
            newLease = new Lease(){
                @Override
                public LeaseItem newItem(
                        final LeaseItemType type,
                        final LeaseAgreementRoleTypeEnum invoicedBy,
                        final Charge charge,
                        final InvoicingFrequency invoicingFrequency,
                        final PaymentMethod paymentMethod,
                        final LocalDate startDate) {
                    return  newLeaseItem;
                }
                @Override
                public SortedSet<LeaseItem> getItems(){
                    return new TreeSet<>(Arrays.asList(newLeaseItem));
                }
            };
        }

        @Test
        public void epochDate_is_copied() throws Exception {

            // given
            epochDate = new LocalDate(2010, 01, 01);
            Lease lease = new Lease();
            LeaseItem leaseItem = new LeaseItem();
            leaseItem.setEpochDate(epochDate);
            lease.getItems().add(leaseItem);

            // when
            lease.copyItemsAndTerms(newLease, null, true);

            // then
            assertThat(newLease.getItems().first().getEpochDate()).isEqualTo(epochDate);

        }

        @Test
        public void empty_epochDate_is_copied() throws Exception {

            // given
            Lease lease = new Lease();
            LeaseItem leaseItem = new LeaseItem();
            leaseItem.setEpochDate(null);
            lease.getItems().add(leaseItem);

            // when
            lease.copyItemsAndTerms(newLease, null, true);

            // then
            assertThat(newLease.getItems().first().getEpochDate()).isNull();

        }

        @Test
        public void epochDate_is_not_copied() throws Exception {

            // given
            Lease lease = new Lease();
            LeaseItem leaseItem = new LeaseItem();
            leaseItem.setEpochDate(epochDate);
            lease.getItems().add(leaseItem);

            // when
            lease.copyItemsAndTerms(newLease, null, false);

            // then
            assertThat(newLease.getItems().first().getEpochDate()).isNull();

        }

        @Mock
        Lease mockLease;

        @Test
        public void invoice_by_is_copied_for_each_item() throws Exception {

            // given
            Lease lease = new Lease();
            LeaseItem itemInvoiceByLandlord = new LeaseItem();
            itemInvoiceByLandlord.setInvoicedBy(LeaseAgreementRoleTypeEnum.LANDLORD);
            itemInvoiceByLandlord.setType(LeaseItemType.RENT);
            lease.getItems().add(itemInvoiceByLandlord);
            LeaseItem itemInvoiceByManager = new LeaseItem();
            itemInvoiceByManager.setType(LeaseItemType.PROPERTY_TAX);
            itemInvoiceByManager.setInvoicedBy(LeaseAgreementRoleTypeEnum.MANAGER);
            lease.getItems().add(itemInvoiceByManager);

            // expect
            context.checking(new Expectations(){{
                oneOf(mockLease).newItem(LeaseItemType.RENT, LeaseAgreementRoleTypeEnum.LANDLORD, null, null, null, null);
                oneOf(mockLease).newItem(LeaseItemType.PROPERTY_TAX, LeaseAgreementRoleTypeEnum.MANAGER, null, null, null, null);
            }});

            // when
            lease.copyItemsAndTerms(mockLease, null, false);



        }

        @Test
        public void items_closed_before_or_on_start_date_are_not_copied() throws Exception {

            // given
            LocalDate startDate = new LocalDate(2020,1,1);
            Lease lease = new Lease();
            LeaseItem itemEndingBeforeStartDate = new LeaseItem();
            itemEndingBeforeStartDate.setType(LeaseItemType.RENT);
            itemEndingBeforeStartDate.setEndDate(startDate.minusDays(1));
            lease.getItems().add(itemEndingBeforeStartDate);

            // expect nothing
            // when
            lease.copyItemsAndTerms(mockLease, startDate, false);

            // and when
            LeaseItem itemEndingOnStartDate = new LeaseItem();
            itemEndingOnStartDate.setEndDate(startDate);
            itemEndingOnStartDate.setType(LeaseItemType.RENT_DISCOUNT);
            lease.getItems().add(itemEndingOnStartDate);

            // still expect nothing
            // when
            lease.copyItemsAndTerms(mockLease, startDate, false);

            // and when
            LeaseItem itemEndingAfterStartDate = new LeaseItem();
            itemEndingAfterStartDate.setEndDate(startDate.plusDays(1));
            itemEndingAfterStartDate.setType(LeaseItemType.SERVICE_CHARGE);
            lease.getItems().add(itemEndingAfterStartDate);

            // and expect
            context.checking(new Expectations(){{
                oneOf(mockLease).newItem(LeaseItemType.SERVICE_CHARGE, null, null, null, null, null);
            }});

            // when
            lease.copyItemsAndTerms(mockLease, startDate, false);

        }


        @Test
        public void end_date_is_copied() throws Exception {

            // given
            Lease lease = new Lease();
            LeaseItem leaseItem = new LeaseItem();
            final LocalDate originalItemEndDate = new LocalDate(2020, 1, 1);
            leaseItem.setEndDate(originalItemEndDate);
            lease.getItems().add(leaseItem);

            // when
            lease.copyItemsAndTerms(newLease, originalItemEndDate.minusDays(1), false);

            // then
            assertThat(newLease.getItems().first().getEndDate()).isEqualTo(originalItemEndDate);

        }

    }

    public static class Finders extends Lease_Test {

        @Test
        public void findFirstActiveItemOfTypeAndChargeInInterval_works() throws Exception {

            // given
            Lease lease = new Lease();
            LeaseItemType leaseItemType = LeaseItemType.SERVICE_CHARGE;
            Charge charge = new Charge();
            LocalDate date = new LocalDate(2018,1,1);

            LeaseItem expiredItem = new LeaseItem();
            expiredItem.setEndDate(new LocalDate(2017, 12, 31));
            expiredItem.setCharge(charge);
            expiredItem.setType(leaseItemType);
            expiredItem.setSequence(BigInteger.valueOf(1));

            LeaseItem activeItem = new LeaseItem();
            activeItem.setCharge(charge);
            activeItem.setType(leaseItemType);
            activeItem.setSequence(BigInteger.valueOf(2));

            LeaseItem secondActiveItem = new LeaseItem();
            secondActiveItem.setCharge(charge);
            secondActiveItem.setType(leaseItemType);
            secondActiveItem.setSequence(BigInteger.valueOf(3));

            lease.getItems().addAll(new TreeSet(Arrays.asList(expiredItem, activeItem, secondActiveItem)));
            assertThat(lease.getItems().size()).isEqualTo(3);

            // when
            LeaseItem itemFound = lease.findFirstActiveItemOfTypeAndChargeInInterval(leaseItemType, charge, LocalDateInterval.including(date, null));

            // then
            assertThat(itemFound).isEqualTo(activeItem);
            assertThat(itemFound).isNotEqualTo(secondActiveItem);
        }
        
        
    }

    public static class OtherTests extends Lease_Test {

        @Mock
        LeaseRepository mockLeaseRepository;

        @Test
        public void validate_Assign_checks_start_date() throws Exception {

            // given
            String reference = "Some Lease Reference";
            final LocalDate startDate = new LocalDate(2018,01,01);
            lease = new Lease();
            lease.setStartDate(startDate);
            lease.leaseRepository = mockLeaseRepository;

            // expect
            context.checking(new Expectations(){{
                allowing(mockLeaseRepository).findLeaseByReferenceElseNull(reference);
                will(returnValue(null));
            }});

            // when, then
            assertThat(lease.validateAssign(reference, null, null,  startDate.minusDays(1))).isEqualTo("The start date cannot be before the start date of the lease");
            assertThat(lease.validateAssign(reference, null, null,  startDate)).isNull();

        }

        @Mock
        BankAccountRepository mockBankAccountRepository;
        @Mock
        BankMandateRepository mockBankMandateRepository;

        @Test
        public void validate_new_mandate_checks_payment_method_lease_items() throws Exception {

            // given
            String reference = "Some mandate reference";
            Party tenant = new Organisation();
            BankAccount bankAccount = new BankAccount();
            lease = new Lease(){
                @Override
                public Party getSecondaryParty(){
                    return tenant;
                }
            };
            lease.bankAccountRepository = mockBankAccountRepository;
            lease.bankMandateRepository = mockBankMandateRepository;

            // expect
            context.checking(new Expectations(){{
                allowing(mockBankAccountRepository).findBankAccountsByOwner(tenant);
                will(returnValue(Arrays.asList(bankAccount)));
                allowing(mockBankMandateRepository).findByReference(reference);
                will(returnValue(null));
            }});

            // when
            LeaseItem leaseItemCheque = new LeaseItem();
            leaseItemCheque.setPaymentMethod(PaymentMethod.CHEQUE);
            lease.getItems().add(leaseItemCheque);

            // then
            assertThat(lease.validateNewMandate(bankAccount, reference, null, null,null, null, null)).isEqualTo("No items with payment method direct debit found");

            // and when
            LeaseItem leaseItemDirectDebit = new LeaseItem();
            leaseItemCheque.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            lease.getItems().add(leaseItemDirectDebit);

            // then
            assertThat(lease.validateNewMandate(bankAccount, reference, null, null,null, null, null)).isNull();

        }

        @Mock MessageService mockMessageService;

        @Test
        public void validate_new_item_works_for_sweden() throws Exception {

            // given
            Charge charge = new Charge();
            charge.setReference("Some reference");
            Charge chargeForServiceCharges = new Charge();
            chargeForServiceCharges.setReference("SERVICE_CHARGE");
            Charge chargeForMarketing = new Charge();
            chargeForMarketing.setReference("MARKETING_CONTRIBUTION");
            Charge chargeForPropertyTax = new Charge();
            chargeForPropertyTax.setReference("PROPERTY_TAX");
            Charge chargeForTurnoverRent = new Charge();
            chargeForTurnoverRent.setReference("TURNOVER_RENT");
            lease = new Lease(){
                @Override
                public ApplicationTenancy getApplicationTenancy() {
                    ApplicationTenancy applicationTenancy = new ApplicationTenancy();
                    applicationTenancy.setPath("/SWE/etc/etc...");
                    return applicationTenancy;
                }
                @Override
                public List<Charge> choices2NewItem() {
                    return Arrays.asList(charge, chargeForServiceCharges, chargeForMarketing, chargeForPropertyTax, chargeForTurnoverRent);
                }
            };
            lease.messageService = mockMessageService;

            // expect
            context.checking(new Expectations(){{
                oneOf(mockMessageService).warnUser("Are you sure? Items of type RENT_FIXED are normally are not entered manually.");
            }});
            // when
            lease.validateNewItem(LeaseItemType.RENT_FIXED, null, charge, null, null, null);

            // and expect
            context.checking(new Expectations(){{
                oneOf(mockMessageService).warnUser("Are you sure? Charge normally is 'service charge' for type service charge");
            }});
            // when
            lease.validateNewItem(LeaseItemType.SERVICE_CHARGE, null, charge, null, null, null);

            // and expect
            context.checking(new Expectations(){{
                oneOf(mockMessageService).warnUser("Are you sure? Charge normally is 'marketing contribution' for type marketing");
            }});
            // when
            lease.validateNewItem(LeaseItemType.MARKETING, null, charge, null, null, null);

            // and expect
            context.checking(new Expectations(){{
                oneOf(mockMessageService).warnUser("Are you sure? Charge normally is 'property tax' for type property tax");
            }});
            // when
            lease.validateNewItem(LeaseItemType.PROPERTY_TAX, null, charge, null, null, null);

            // and expect
            context.checking(new Expectations(){{
                oneOf(mockMessageService).warnUser("Are you sure? Charge normally is 'turnover rent' for type turnover rent");
            }});
            // when
            lease.validateNewItem(LeaseItemType.TURNOVER_RENT, null, charge, null, null, null);

            // and expect nothing
            // when
            lease.validateNewItem(LeaseItemType.SERVICE_CHARGE, null, chargeForServiceCharges, null, null, null);
            lease.validateNewItem(LeaseItemType.MARKETING, null, chargeForMarketing, null, null, null);
            lease.validateNewItem(LeaseItemType.PROPERTY_TAX, null, chargeForPropertyTax, null, null, null);
            lease.validateNewItem(LeaseItemType.TURNOVER_RENT, null, chargeForTurnoverRent, null, null, null);

        }

        @Test
        public void hasOverlappingOccupancies_works() throws Exception {

            // given
            Lease lease = new Lease();
            Occupancy o1 = new Occupancy();
            Unit u1 = new Unit();
            u1.setName("u1");
            o1.setUnit(u1);
            Occupancy o2 = new Occupancy();
            Unit u2 = new Unit();
            u2.setName("u2");
            o2.setUnit(u2);
            o2.setStartDate(new LocalDate(2019,1,1));
            Occupancy o3 = new Occupancy();
            o3.setStartDate(new LocalDate(2019,7,15));

            lease.getOccupancies().addAll(Arrays.asList(o1, o2, o3));
            Assertions.assertThat(lease.getOccupancies()).hasSize(3);

            // when
            Assertions.assertThat(o1.getInterval().toString()).isEqualTo("----------/----------");
            Assertions.assertThat(o2.getInterval().toString()).isEqualTo("2019-01-01/----------");
            Assertions.assertThat(o3.getInterval().toString()).isEqualTo("2019-07-15/----------");
            // then
            assertThat(lease.hasOverlappingOccupancies()).isTrue();

            // when
            o1.setEndDate(new LocalDate(2018,12,31));
            Assertions.assertThat(o1.getInterval().toString()).isEqualTo("----------/2019-01-01");
            Assertions.assertThat(o2.getInterval().toString()).isEqualTo("2019-01-01/----------");
            Assertions.assertThat(o3.getInterval().toString()).isEqualTo("2019-07-15/----------");
            // then
            assertThat(lease.hasOverlappingOccupancies()).isTrue();

            // when
            o2.setEndDate(new LocalDate(2019,1,2));
            Assertions.assertThat(o1.getInterval().toString()).isEqualTo("----------/2019-01-01");
            Assertions.assertThat(o2.getInterval().toString()).isEqualTo("2019-01-01/2019-01-03");
            Assertions.assertThat(o3.getInterval().toString()).isEqualTo("2019-07-15/----------");

            // then
            assertThat(lease.hasOverlappingOccupancies()).isFalse();

        }

        @Mock
        ClockService mockClockService;

        @Test
        public void recent_And_Future_Items_works() throws Exception {

            // given
            Lease lease = new Lease();
            lease.clockService = mockClockService;
            LocalDate now = new LocalDate(2020,1,2);

            // when, then
            assertThat(lease.getRecentAndFutureItems()).isEmpty();

            // expect
            context.checking(new Expectations(){{
                allowing(mockClockService).now();
                will(returnValue(now));
            }});

            // when
            LeaseItem itemWithoutDates = new LeaseItem();
            itemWithoutDates.setSequence(BigInteger.valueOf(1));
            lease.getItems().add(itemWithoutDates);
            // then
            assertThat(lease.getRecentAndFutureItems()).contains(itemWithoutDates);

            // when
            LeaseItem futureItem = new LeaseItem();
            futureItem.setStartDate(now.plusYears(1));
            lease.getItems().add(futureItem);
            // then
            assertThat(lease.getRecentAndFutureItems()).contains(futureItem);
            assertThat(lease.getRecentAndFutureItems()).hasSize(2);

            // when
            LeaseItem pastItemLessOneYearAgo = new LeaseItem();
            pastItemLessOneYearAgo.setEndDate(now.minusYears(1));
            lease.getItems().add(pastItemLessOneYearAgo);
            // then
            assertThat(lease.getRecentAndFutureItems()).contains(pastItemLessOneYearAgo);
            assertThat(lease.getRecentAndFutureItems()).hasSize(3);

            // when
            LeaseItem pastItemMoreThanOneYearAgo = new LeaseItem();
            pastItemMoreThanOneYearAgo.setEndDate(now.minusYears(1).minusDays(1));
            lease.getItems().add(pastItemMoreThanOneYearAgo);
            // then
            assertThat(lease.getRecentAndFutureItems()).doesNotContain(pastItemMoreThanOneYearAgo);
            assertThat(lease.getRecentAndFutureItems()).hasSize(3);

        }

    }

}
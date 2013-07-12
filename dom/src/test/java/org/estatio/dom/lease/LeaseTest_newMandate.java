/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import com.google.common.collect.Lists;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.financial.BankAccount;
import org.estatio.dom.financial.BankMandate;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.FinancialConstants;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;
import org.estatio.services.clock.ClockService;

public class LeaseTest_newMandate {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private AgreementRoleTypes mockAgreementRoleTypes;
    @Mock
    private AgreementRoles mockAgreementRoles;
    @Mock
    private AgreementTypes mockAgreementTypes;
    @Mock
    private FinancialAccounts mockFinancialAccounts;
    @Mock
    private ClockService mockClockService;
    
    @Mock
    private DomainObjectContainer mockContainer;

    private Lease lease;

    private BankMandate bankMandate;

    private BankAccount bankAccount;
    private BankAccount someOtherBankAccount;
    
    private Party tenant;
    private AgreementRoleType tenantAgreementRoleType;
    private AgreementRoleType debtorAgreementRoleType;
    private AgreementRole tenantAgreementRole;

    private AgreementType bankMandateAgreementType;


    private LocalDate startDate;
    private LocalDate endDate;

    @Before
    public void setUp() throws Exception {

        tenantAgreementRoleType = new AgreementRoleType();
        tenantAgreementRoleType.setTitle(LeaseConstants.ART_TENANT);
        context.checking(new Expectations() {
            {
                allowing(mockAgreementRoleTypes).findByTitle(LeaseConstants.ART_TENANT);
                will(returnValue(tenantAgreementRoleType));
            }
        });

        debtorAgreementRoleType = new AgreementRoleType();
        debtorAgreementRoleType.setTitle(FinancialConstants.ART_DEBTOR);
        context.checking(new Expectations() {
            {
                allowing(mockAgreementRoleTypes).findByTitle(FinancialConstants.ART_DEBTOR);
                will(returnValue(debtorAgreementRoleType));
            }
        });
        
        bankMandateAgreementType = new AgreementType();
        bankMandateAgreementType.setTitle(FinancialConstants.AT_MANDATE);
        context.checking(new Expectations() {
            {
                allowing(mockAgreementTypes).find(FinancialConstants.AT_MANDATE);
                will(returnValue(bankMandateAgreementType));
            }
        });

        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(new LocalDate(2013,4,2)));
            }
        });
        
        tenant = new PartyForTesting();
        tenantAgreementRole = new AgreementRole();
        tenantAgreementRole.setParty(tenant);
        tenantAgreementRole.setType(tenantAgreementRoleType);
        tenantAgreementRole.injectClockService(mockClockService);

        bankMandate = new BankMandate();
        bankMandate.setContainer(mockContainer);
        
        bankAccount = new BankAccount();
        bankAccount.setReference("REF1");
        someOtherBankAccount = new BankAccount();
        
        startDate = new LocalDate(2013,4,1);
        endDate = new LocalDate(2013,5,2);
        
        lease = new Lease();
        lease.injectAgreementRoleTypes(mockAgreementRoleTypes);
        lease.injectAgreementRoles(mockAgreementRoles);
        lease.injectAgreementTypes(mockAgreementTypes);
        lease.injectFinancialAccounts(mockFinancialAccounts);
        lease.setContainer(mockContainer);
    }
    


    @Test
    public void whenSecondaryPartyIsUnknown_isDisabled() {

        // given
        assertThat(lease.getRoles(), Matchers.empty());
        
        // when
        final String disabledReason = lease.disableNewMandate(bankAccount, startDate, endDate);
        
        // then
        assertThat(disabledReason, is("Could not determine the tenant (secondary party) of this lease"));
    }
    
    @Test
    public void whenSecondaryPartyIsKnownButNotCurrent_isDisabled() {
        
        // given
        lease.addToRoles(tenantAgreementRole);
        tenantAgreementRole.setEndDate(new LocalDate(2013,4,1));
        
        // when
        final String disabledReason = lease.disableNewMandate(bankAccount, startDate, endDate);
        
        // then
        assertThat(disabledReason, is(not(nullValue())));
        
        // and when/then
        // (defaultXxx wouldn't get called, but for coverage...)
        assertThat(lease.default0PaidBy(), is(nullValue()));
    }

    @Test
    public void whenSecondaryPartyIsKnownButNoBankAccounts_isDisabled() {

        // given
        lease.addToRoles(tenantAgreementRole);

        context.checking(new Expectations() {
            {
                oneOf(mockFinancialAccounts).findBankAccountsByParty(tenant);
                will(returnValue(Collections.emptyList()));
            }
        });
        
        // when, then
        final String disabledReason = lease.disableNewMandate(bankAccount, startDate, endDate);
        assertThat(disabledReason, is(not(nullValue())));
    }

    
    @Test
    public void whenSecondaryPartyIsKnownAndHasBankAccounts_canInvoke() {

        // given
        lease.addToRoles(tenantAgreementRole);

        context.checking(new Expectations() {
            {
                allowing(mockFinancialAccounts).findBankAccountsByParty(tenant);
                will(returnValue(Lists.newArrayList(bankAccount)));
            }
        });
        
        // when/then
        final String disabledReason = lease.disableNewMandate(bankAccount, startDate, endDate);
        assertThat(disabledReason, is(nullValue()));
        
        // and when/then
        final List<BankAccount> bankAccounts = lease.choices0NewMandate();
        assertThat(bankAccounts, Matchers.contains(bankAccount));
        
        // and when/then
        final BankAccount defaultBankAccount = lease.default0NewMandate();
        assertThat(defaultBankAccount, is(bankAccount));
        
        // and when/then
        final String validateReason = lease.validateNewMandate(defaultBankAccount, startDate, endDate);
        assertThat(validateReason, is(nullValue()));
        
        // and given
        assertThat(lease.getPaidBy(), is(nullValue()));
        final AgreementRole newBankMandateAgreementRole = new AgreementRole();

        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(BankMandate.class);
                will(returnValue(bankMandate));
                
                oneOf(mockContainer).persist(bankMandate);

                oneOf(mockContainer).newTransientInstance(AgreementRole.class);
                will(returnValue(newBankMandateAgreementRole));
                
                oneOf(mockContainer).persistIfNotAlready(newBankMandateAgreementRole);
            }
        });

        // when
        final Lease returned = lease.newMandate(defaultBankAccount, startDate, endDate);
        
        // then
        assertThat(returned, is(lease));
        
        assertThat(lease.getPaidBy(), is(bankMandate));
        assertThat(bankMandate.getAgreementType(), is(bankMandateAgreementType));
        assertThat(bankMandate.getBankAccount(), is((FinancialAccount)bankAccount));
        assertThat(bankMandate.getStartDate(), is(startDate));
        assertThat(bankMandate.getEndDate(), is(endDate));
        assertThat(bankMandate.getReference(), is("REF1-20130401"));

        // we don't assert on the bankMandate's roles,because that is the responsibility of the AgreementRoles#newRole()
        // (mocked out above).
    }
    
    @Test
    public void whenPrereqs_validateWithIncorrectBankAccount() {
        
        // given
        lease.addToRoles(tenantAgreementRole);
        
        context.checking(new Expectations() {
            {
                oneOf(mockFinancialAccounts).findBankAccountsByParty(tenant);
                will(returnValue(Lists.newArrayList(bankAccount)));
            }
        });
        
        // when/then
        final String validateReason = lease.validateNewMandate(someOtherBankAccount, startDate, endDate);
        assertThat(validateReason, is("Bank account is not owned by this lease's tenant"));
    }
    
}


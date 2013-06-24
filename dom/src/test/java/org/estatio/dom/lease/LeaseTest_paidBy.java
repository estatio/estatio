package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

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

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.financial.BankMandate;
import org.estatio.dom.financial.FinancialConstants;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;
import org.estatio.services.clock.ClockService;

public class LeaseTest_paidBy {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private AgreementRoleTypes mockAgreementRoleTypes;
    @Mock
    private AgreementTypes mockAgreementTypes;
    @Mock
    private Agreements mockAgreements;
    @Mock
    private ClockService mockClockService;
    
    @Mock
    private DomainObjectContainer mockContainer;

    private Lease lease;

    private BankMandate bankMandate;

    private Party tenant;
    private AgreementRoleType tenantAgreementRoleType;
    private AgreementRoleType debtorAgreementRoleType;
    private AgreementRole tenantAgreementRole;
    private AgreementRole debtorAgreementRole;

    private AgreementType bankMandateAgreementType;

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
        
        lease = new Lease();
        lease.injectAgreementRoleTypes(mockAgreementRoleTypes);
        lease.injectAgreements(mockAgreements);
        lease.injectAgreementTypes(mockAgreementTypes);
    }
    
    @Test
    public void happyCase() {
        
        // given
        assertThat(lease.getPaidBy(), is(nullValue()));

        // when
        final Lease returned = lease.paidBy(bankMandate);
        
        // then
        assertThat(lease.getPaidBy(), is(bankMandate));
        assertThat(returned, is(lease));
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
        lease.addToRoles(tenantAgreementRole);

        context.checking(new Expectations() {
            {
                oneOf(mockAgreements).findByAgreementTypeAndRoleTypeAndParty(bankMandateAgreementType, debtorAgreementRoleType, tenant);
                will(returnValue(Collections.emptyList()));
            }
        });
        
        final String reason = lease.disablePaidBy(bankMandate);
        
        // then
        assertThat(reason, is("There are no valid mandates; set one up using 'New Mandate'"));
    }

    @Test
    public void whenSecondaryPartyIsKnownAndHasMandates_isNotDisabled() {

        // given
        lease.addToRoles(tenantAgreementRole);

        context.checking(new Expectations() {
            {
                oneOf(mockAgreements).findByAgreementTypeAndRoleTypeAndParty(bankMandateAgreementType, debtorAgreementRoleType, tenant);
                will(returnValue(Lists.newArrayList(bankMandate)));
            }
        });
        
        final String reason = lease.disablePaidBy(bankMandate);
        
        // then
        assertThat(reason, is(nullValue()));
    }
    
    @Test
    public void whenSecondaryPartyIsKnownButNotCurrent_isDisabled() {
        
        // given
        lease.addToRoles(tenantAgreementRole);
        tenantAgreementRole.setEndDate(new LocalDate(2013,4,1));
        
        context.checking(new Expectations() {
            {
                never(mockAgreements);
            }
        });
        
        final String reason = lease.disablePaidBy(bankMandate);
        
        // then
        assertThat(reason, is(not(nullValue())));
    }

}

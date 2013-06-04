package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;


import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Person;
import org.estatio.services.clock.ClockService;

public class AgreementTest_findParty  {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
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

        @Override
        public Party getPrimaryParty() {
            return null;
        }

        @Override
        public Party getSecondaryParty() {
            return null;
        }
        
        @Override
        public Party findParty(String agreementRoleTypeTitle) {
            return super.findParty(agreementRoleTypeTitle);
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
        creditorAr.setStartDate(new LocalDate(2013,7,1)); // current
        creditorAr.injectClockService(mockClockService);
        
        creditorAr2 = new AgreementRole();
        creditorAr2.setType(creditorAgreementRoleType);
        creditorAr2.setParty(creditor);
        creditorAr2.setStartDate(new LocalDate(2012,7,1)); // not current
        creditorAr2.setEndDate(new LocalDate(2013,6,30));
        creditorAr2.injectClockService(mockClockService);
        
        debtorAr = new AgreementRole();
        debtorAr.setType(debtorAgreementRoleType);
        debtorAr.setParty(debtor);
        debtorAr.setStartDate(new LocalDate(2013,7,1)); // current
        debtorAr.injectClockService(mockClockService);
        
        debtorAr2 = new AgreementRole();
        debtorAr2.setType(debtorAgreementRoleType);
        debtorAr2.setParty(debtor);
        debtorAr2.setStartDate(new LocalDate(2012,7,1)); // not current
        debtorAr2.setEndDate(new LocalDate(2013,6,30));
        debtorAr2.injectClockService(mockClockService);
        
        context.checking(new Expectations() {
            {
                allowing(mockAgreementRoleTypes).find("Creditor");
                will(returnValue(creditorAgreementRoleType));
                
                allowing(mockAgreementRoleTypes).find("Debtor");
                will(returnValue(creditorAgreementRoleType));
                
                allowing(mockClockService).now();
                will(returnValue(new LocalDate(2013,8,1)));
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
        
        assertThat(agreement.findParty("Creditor"), is(creditor));
    }

}

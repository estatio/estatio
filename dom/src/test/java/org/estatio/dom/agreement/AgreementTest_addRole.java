package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementTest_addRole {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private AgreementRoles mockAgreementRoles;
    
    private Agreement agreement;
    private Party party;
    private AgreementRoleType type;
    private LocalDate startDate;
    private LocalDate endDate;
    
    private AgreementRole agreementRole;

    @Before
    public void setup() {
        agreement = new AgreementForTesting();
        agreement.injectAgreementRoles(mockAgreementRoles);
        
        party = new PartyForTesting();
        type = new AgreementRoleType();
        startDate = new LocalDate(2013,4,1);
        endDate = new LocalDate(2013,5,2);
        
        agreementRole = new AgreementRole();
    }

    @Test
    public void whenRoleDoesNotYetExist() {
        context.checking(new Expectations() {
            {
                oneOf(mockAgreementRoles).findAgreementRole(agreement, party, type, startDate);
                will(returnValue(null));
                
                oneOf(mockAgreementRoles).newAgreementRole(agreement, party, type, startDate, endDate);
            }
        });
        agreement.addRole(party, type, startDate, endDate);
    }
    
    @Test
    public void whenRoleDoesExist() {
        context.checking(new Expectations() {
            {
                oneOf(mockAgreementRoles).findAgreementRole(agreement, party, type, startDate);
                will(returnValue(agreementRole));
                
                never(mockAgreementRoles);
            }
        });
        final AgreementRole role = agreement.addRole(party, type, startDate, endDate);
        assertThat(role, is(agreementRole));
    }

}

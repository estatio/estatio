package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
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

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementRolesTest_newAgreementRole {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockDomainObjectContainer;
    
    private Agreement agreement;
    
    private AgreementRoles agreementRoles;

    private Party party;

    private AgreementRoleType type;

    private LocalDate startDate;

    private LocalDate endDate;
    

    @Before
    public void setup() {
        agreement = new AgreementForTesting();
        party = new PartyForTesting();
        type = new AgreementRoleType();
        startDate = new LocalDate(2013,4,1);
        endDate = new LocalDate(2013,5,2);
        
        agreementRoles = new AgreementRoles();
        agreementRoles.setContainer(mockDomainObjectContainer);
    }

    @Test
    public void happyCase() {
        
        final AgreementRole role = new AgreementRole();
        
        assertThat(agreement.getRoles(), Matchers.empty());

        context.checking(new Expectations() {
            {
                oneOf(mockDomainObjectContainer).newTransientInstance(AgreementRole.class);
                will(returnValue(role));
                
                oneOf(mockDomainObjectContainer).persistIfNotAlready(role);
            }
        });
        
        // when
        final AgreementRole agreementRole = agreementRoles.newAgreementRole(agreement, party, type, startDate, endDate);
        
        // then
        assertThat(agreementRole.getAgreement(), is(agreement));
        assertThat(agreementRole.getParty(), is(party));
        assertThat(agreementRole.getStartDate(), is(startDate));
        assertThat(agreementRole.getEndDate(), is(endDate));
        
        assertThat(agreement.getRoles(), Matchers.contains(agreementRole));
    }

}

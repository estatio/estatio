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
package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.SortedSet;

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

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoles;
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
        
        assertThat(agreement.getRoles().size(), is(0));

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
        
        assertThat((SortedSet<AgreementRole>) agreement.getRoles(), Matchers.contains(agreementRole));
    }

}

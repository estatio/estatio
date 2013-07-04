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
                oneOf(mockAgreementRoles).findByAgreementAndPartyAndTypeAndStartDate(agreement, party, type, startDate);
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
                oneOf(mockAgreementRoles).findByAgreementAndPartyAndTypeAndStartDate(agreement, party, type, startDate);
                will(returnValue(agreementRole));
                
                never(mockAgreementRoles);
            }
        });
        final AgreementRole role = agreement.addRole(party, type, startDate, endDate);
        assertThat(role, is(agreementRole));
    }

}

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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;
import org.estatio.services.clock.ClockService;

public class AgreementTest_findCurrentOrMostRecentParty {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private ClockService mockClockService;
    
    private Agreement<?> agreement;
    
    private AgreementRoleType landlordArt;
    private AgreementRoleType tenantArt;
    
    private Party landlord;
    private Party tenant1;
    private Party tenant2;
    
    private AgreementRole arLandlord;
    private AgreementRole arTenant1;
    private AgreementRole arTenant2;
    
    private final LocalDate clockDate = new LocalDate(2013,4,1);

    @Before
    public void setup() {
        
        landlord = new PartyForTesting();
        tenant1 = new PartyForTesting();
        tenant2 = new PartyForTesting();
        
        landlordArt = new AgreementRoleType();
        landlordArt.setTitle("Landlord");
        
        tenantArt = new AgreementRoleType();
        tenantArt.setTitle("Tenant");
        
        agreement = new AgreementForTesting();
        
        arLandlord = new AgreementRole();
        arLandlord.setType(landlordArt);
        arLandlord.setParty(landlord);
        arLandlord.injectClockService(mockClockService);
        
        arTenant1= new AgreementRole();
        arTenant1.setType(tenantArt);
        arTenant1.setParty(tenant1);
        arTenant1.injectClockService(mockClockService);
        
        arTenant2 = new AgreementRole();
        arTenant2.setType(tenantArt);
        arTenant2.setParty(tenant2);
        arTenant2.injectClockService(mockClockService);
        
        // tenant 1 superceded by tenant 2
        arTenant1.setEndDate(clockDate.minusMonths(1));
        arTenant2.setStartDate(arTenant1.getEndDate().plusDays(1));
        
        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(clockDate));
            }
        });
    }

    @Test
    public void whenNone() {
        assertThat(agreement.findCurrentOrMostRecentParty(landlordArt), is(nullValue()));
    }
    
    @Test
    public void whenStillCurrentOnlyOnePartyOfType() {
        addAllRoles();
        assertThat(agreement.findCurrentOrMostRecentParty(landlordArt), is(landlord));
    }

    @Test
    public void whenStillCurrentTwoPartiesOfType() {
        addAllRoles();
        assertThat(agreement.findCurrentOrMostRecentParty(tenantArt), is(tenant2));
    }
    
    @Test
    public void whenTerminated() {
        addAllRoles();
        agreement.setEndDate(clockDate.minusDays(2));
        assertThat(agreement.findCurrentOrMostRecentParty(landlordArt), is(landlord));
        assertThat(agreement.findCurrentOrMostRecentParty(tenantArt), is(tenant2));
    }
    
    private void addAllRoles() {
        agreement.getRoles().add(arLandlord);
        agreement.getRoles().add(arTenant1);
        agreement.getRoles().add(arTenant2);
    }
    

}

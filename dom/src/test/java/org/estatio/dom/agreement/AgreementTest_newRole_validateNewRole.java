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

import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementTest_newRole_validateNewRole  {

    @Mock
    private DomainObjectContainer mockContainer;
    
    private AgreementRoleType art;
    private AgreementRoleType artOther;
    private Party party;
    
    private Agreement agreement;

    private LocalDate startDate;
    private LocalDate endDate;

    
    @Before
    public void setUp() throws Exception {
        art = new AgreementRoleType();
        artOther = new AgreementRoleType();
        
        party = new PartyForTesting();

        startDate = new LocalDate(2013,4,1);
        endDate = new LocalDate(2023,3,30);

        agreement = new AgreementForTesting();
        agreement.setContainer(mockContainer);
    }
    


    @Test
    public void validateNewRole_valid_nullStart_noExistingRoles() {
        assertThat(
                agreement.validateNewRole(art, party, null, endDate), 
                is(nullValue()));
    }

    @Test
    public void validateNewRole_valid_nullEnd_noExistingRoles() {
        assertThat(
                agreement.validateNewRole(art, party, startDate, null), 
                is(nullValue()));
    }
    
    @Test
    public void validateNewRole_valid_startBeforeEnd_noExistingRoles() {
        assertThat(
                agreement.validateNewRole(art, party, startDate, endDate), 
                is(nullValue()));
    }
    
    @Test
    public void validateNewRole_valid_startSameAsEnd_noExistingRoles() {
        startDate = endDate;
        assertThat(
                agreement.validateNewRole(art, party, startDate, endDate), 
                is(nullValue()));
    }
    
    @Test
    public void validateNewRole_invalid_startAfterEnd_noExistingRoles() {
        startDate = endDate.plusDays(1);
        assertThat(
                agreement.validateNewRole(art, party, startDate, endDate), 
                is("End date cannot be earlier than start date"));
    }

    @Test
    public void validateNewRole_valid_nullStartAnd_existingRolesDoNotContainType() {
        final AgreementRole existingRole = new AgreementRole();
        existingRole.setType(artOther);
        agreement.getRoles().add(existingRole);
        assertThat(
                agreement.validateNewRole(art, party, null, null), 
                is(nullValue()));
    }

    
    @Test
    public void validateNewRole_invalid_nullStartAnd_existingRolesDoesContainType() {
        final AgreementRole existingRole = new AgreementRole();
        existingRole.setType(art);
        agreement.getRoles().add(existingRole);
        assertThat(
                agreement.validateNewRole(art, party, null, null), 
                is("Add a successor/predecessor to existing agreement role"));
    }
    
}

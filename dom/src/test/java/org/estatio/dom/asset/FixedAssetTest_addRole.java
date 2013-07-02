/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.dom.asset;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class FixedAssetTest_addRole {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private FixedAssetRoles mockFixedAssetRoles;
    
    private Party party;
    private FixedAssetRoleType type;
    private LocalDate startDate;
    private LocalDate endDate;
    
    private FixedAsset fixedAsset;

    private FixedAssetRole role;
    
    @Before
    public void setUp() throws Exception {
        party = new PartyForTesting();
        type = FixedAssetRoleType.ASSET_MANAGER;
        startDate = new LocalDate(2013,4,1);
        endDate = new LocalDate(2013,5,2);
        
        role = new FixedAssetRole();
        
        fixedAsset = new FixedAssetForTesting();
        fixedAsset.injectFixedAssetRoles(mockFixedAssetRoles);
    }
    
    @Test
    public void addRole_whenDoesNotExistAlready() {
        context.checking(new Expectations() {
            {
                oneOf(mockFixedAssetRoles).findRole(fixedAsset, party, type, startDate, endDate);
                will(returnValue(null));
                
                oneOf(mockFixedAssetRoles).newRole(fixedAsset, party, type, startDate, endDate);
                will(returnValue(role));
            }
        });
        
        final FixedAssetRole addedRole = fixedAsset.addRole(party, type, startDate, endDate);
        assertThat(addedRole, is(role));
    }
    
    @Test
    public void addRole_whenDoesExist() {
        context.checking(new Expectations() {
            {
                oneOf(mockFixedAssetRoles).findRole(fixedAsset, party, type, startDate, endDate);
                will(returnValue(role));

                never(mockFixedAssetRoles);
            }
        });
        
        final FixedAssetRole addedRole = fixedAsset.addRole(party, type, startDate, endDate);
        assertThat(addedRole, is(role));
    }

    // behaviour not fully specified; see comments in code
    @Ignore 
    @Test
    public void choices0AddRole() {
    }
}

package org.estatio.dom.asset;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
    public void whenDoesNotExistAlready() {
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
    public void whenDoesExist() {
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

}

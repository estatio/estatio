package org.estatio.dom.asset;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;


public class FixedAssetRoleTest_compareTo extends ComparableContractTest_compareTo<FixedAssetRole> {

    private FixedAsset asset1;
    private FixedAsset asset2;
    
    private Party party1;
    private Party party2;
    
    @Before
    public void setUp() throws Exception {
        asset1 = new FixedAssetForTesting();
        asset2 = new FixedAssetForTesting();
        asset1.setName("A");
        asset2.setName("B");
        party1 = new PartyForTesting();
        party2 = new PartyForTesting();
        party1.setName("A");
        party2.setName("B");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected List<List<FixedAssetRole>> orderedTuples() {
        return listOf(
                 listOf(
                        newFixedAssetRole(null, null, null, null),
                        newFixedAssetRole(asset1, null, null, null),
                        newFixedAssetRole(asset1, null, null, null),
                        newFixedAssetRole(asset2, null, null, null))
                ,listOf(
                        newFixedAssetRole(asset1, null, null, null),
                        newFixedAssetRole(asset1, party1, null, null),
                        newFixedAssetRole(asset1, party1, null, null),
                        newFixedAssetRole(asset1, party2, null, null))
                ,listOf(
                        newFixedAssetRole(asset1, party1, null, null),
                        newFixedAssetRole(asset1, party1, new LocalDate(2012,4,2), null),
                        newFixedAssetRole(asset1, party1, new LocalDate(2012,4,2), null),
                        newFixedAssetRole(asset1, party1, new LocalDate(2012,3,1), null))
                ,listOf(
                        newFixedAssetRole(asset1, party1, new LocalDate(2012,4,2), null),
                        newFixedAssetRole(asset1, party1, new LocalDate(2012,4,2), FixedAssetRoleType.ASSET_MANAGER),
                        newFixedAssetRole(asset1, party1, new LocalDate(2012,4,2), FixedAssetRoleType.ASSET_MANAGER),
                        newFixedAssetRole(asset1, party1, new LocalDate(2012,4,2), FixedAssetRoleType.PROPERTY_CONTACT))
                );
    }

    private FixedAssetRole newFixedAssetRole(FixedAsset asset, Party party, LocalDate startDate, FixedAssetRoleType type) {
        final FixedAssetRole far = new FixedAssetRole();
        far.setAsset(asset);
        far.setParty(party);
        far.setStartDate(startDate);
        far.setType(type);
        return far;
    }

}

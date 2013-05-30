package org.estatio.dom.asset;

import java.util.List;

import com.google.common.collect.Lists;

import org.estatio.dom.ComparableContractTest_compareTo;


public class FixedAssetRoleTest_compareTo extends ComparableContractTest_compareTo<FixedAssetRole> {

    @SuppressWarnings("unchecked")
    @Override
    protected List<List<FixedAssetRole>> orderedTuples() {
        return Lists.<List<FixedAssetRole>>newArrayList(
                Lists.newArrayList(
                    newFixedAssetRole(null),
                    newFixedAssetRole(FixedAssetRoleType.ASSET_MANAGER),
                    newFixedAssetRole(FixedAssetRoleType.ASSET_MANAGER),
                    newFixedAssetRole(FixedAssetRoleType.PROPERTY_CONTACT)
                ));
    }

    private FixedAssetRole newFixedAssetRole(FixedAssetRoleType type) {
        final FixedAssetRole far = new FixedAssetRole();
        far.setType(type);
        return far;
    }

}

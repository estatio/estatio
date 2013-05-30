package org.estatio.dom.asset;

import com.google.common.collect.Ordering;

public enum FixedAssetRoleType {

    PROPERTY_OWNER("Property Owner"), 
    PROPERTY_MANAGER("Property Manager"), 
    ASSET_MANAGER("Asset Manager"), 
    PROPERTY_CONTACT("Property Contact");

    private final String title;

    private FixedAssetRoleType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
    
    public static Ordering<FixedAssetRoleType> ORDERING_BY_TYPE = new Ordering<FixedAssetRoleType>() {
        public int compare(FixedAssetRoleType p, FixedAssetRoleType q) {
            return Ordering.<FixedAssetRoleType> natural().nullsFirst().compare(p, q);
        }
    };

}

package org.estatio.dom.asset;

import org.estatio.dom.utils.StringUtils;

public enum FixedAssetRoleType {

    PROPERTY_OWNER, 
    PROPERTY_MANAGER, 
    ASSET_MANAGER, 
    PROPERTY_CONTACT;

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }
    
}

package org.estatio.dom.party;

import org.estatio.dom.utils.StringUtils;

// TODO: this is only sketched at the moment...
public enum PartyRoleType {

    OWNER, 
    TENANT, 
    LANDLORD,
    BANK;

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

}

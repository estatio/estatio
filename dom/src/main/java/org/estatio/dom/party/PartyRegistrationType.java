package org.estatio.dom.party;

import org.estatio.dom.utils.StringUtils;

public enum PartyRegistrationType {

    VAT, 
    CHAMBER_OF_COMMERCE;

    public String title() {
        return StringUtils.enumTitle(name());
    }

}

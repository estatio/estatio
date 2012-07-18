package com.eurocommercialproperties.estatio.dom.party;

import com.eurocommercialproperties.estatio.dom.utils.StringUtils;

public enum PersonGenderType {

    MALE, FEMALE, UNKNOWN;

    public String title() {
        return StringUtils.enumTitle(name());
    }

}

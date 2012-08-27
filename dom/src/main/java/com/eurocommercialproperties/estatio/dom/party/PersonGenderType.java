package com.eurocommercialproperties.estatio.dom.party;

import com.eurocommercialproperties.estatio.dom.utils.StringUtils;

public enum PersonGenderType {

    UNKNOWN, MALE, FEMALE;

    public String title() {
        return StringUtils.enumTitle(name());
    }

}

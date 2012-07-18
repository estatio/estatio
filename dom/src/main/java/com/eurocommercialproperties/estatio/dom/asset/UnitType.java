package com.eurocommercialproperties.estatio.dom.asset;

import com.eurocommercialproperties.estatio.dom.utils.StringUtils;

public enum UnitType {

    BOUTIQUE, MEDIUM, HYPERMARKET, EXTERNAL, OFFICE, PARKING;

    public String title() {
        return StringUtils.enumTitle(name());
    }

}

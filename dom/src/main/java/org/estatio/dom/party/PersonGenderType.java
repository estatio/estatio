package org.estatio.dom.party;

import org.estatio.dom.utils.StringUtils;

public enum PersonGenderType {

    UNKNOWN, MALE, FEMALE;

    public String title() {
        return StringUtils.enumTitle(name());
    }

}

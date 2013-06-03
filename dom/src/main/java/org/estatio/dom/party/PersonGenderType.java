package org.estatio.dom.party;

import com.google.common.collect.Ordering;

import org.estatio.dom.utils.StringUtils;

public enum PersonGenderType {

    UNKNOWN, MALE, FEMALE;

    public String title() {
        return StringUtils.enumTitle(name());
    }

    public static Ordering<PersonGenderType> ORDERING_BY_TYPE = Ordering.<PersonGenderType> natural().nullsFirst();

}

package org.estatio.dom.party;

import com.google.common.collect.Ordering;

import org.estatio.dom.utils.StringUtils;

public enum PartyRegistrationType {

    VAT, CHAMBER_OF_COMMERCE;

    public String title() {
        return StringUtils.enumTitle(name());
    }

    public static Ordering<PartyRegistrationType> ORDERING_BY_TYPE = Ordering.<PartyRegistrationType> natural().nullsFirst();

}

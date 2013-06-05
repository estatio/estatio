package org.estatio.dom.asset;

import com.google.common.collect.Ordering;

import org.estatio.dom.TitledEnum;
import org.estatio.dom.utils.StringUtils;

public enum UnitType implements TitledEnum {

    BOUTIQUE,
    STORAGE,
    MEDIUM,
    HYPERMARKET,
    EXTERNAL,
    DEHOR,
    CINEMA,
    SERVICES,
    VIRTUAL;

    public String title() {
        return StringUtils.enumTitle(name());
    }

    public static Ordering<UnitType> ORDERING_BY_TYPE = 
            Ordering.<UnitType> natural().nullsFirst();

}

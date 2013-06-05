package org.estatio.dom.asset;

import com.google.common.collect.Ordering;

import org.estatio.dom.TitledEnum;
import org.estatio.dom.utils.StringUtils;


public enum PropertyType implements TitledEnum {

    SHOPPING_CENTER, 
    WAREHOUSE,
    RESIDENTIAL,
    RETAIL_PARK,
    MIXED,
    CINEMA;
    
    public String title() {
        return StringUtils.enumTitle(this.toString());
    }
    
    public static Ordering<PropertyType> ORDERING_BY_TYPE = 
            Ordering.<PropertyType> natural().nullsFirst();

}

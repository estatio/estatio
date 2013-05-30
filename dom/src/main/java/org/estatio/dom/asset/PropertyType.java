package org.estatio.dom.asset;

import org.estatio.dom.Titled;


public enum PropertyType implements Titled<PropertyType> {

    SHOPPING_CENTER("Shopping Center"), 
    WAREHOUSE("Warehouse"),
    RESIDENTIAL("Residential"),
    RETAIL_PARK("Retail Park"),
    MIXED("Mixed"),
    CINEMA("Cinema");
    
    private final String title;

    private PropertyType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}

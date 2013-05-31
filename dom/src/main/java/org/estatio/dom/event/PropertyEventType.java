package org.estatio.dom.event;

import org.estatio.dom.Titled;

//TODO: EST-131: convert to entity, since will vary by location
public enum PropertyEventType implements Titled<PropertyEventType> {

    PROPERTY_DISRUPTION("Disruption"), 
    PROPERTY_EXTENSION("Extension"), 
    PROPERTY_REFURBISHMENT("Extension"), 
    PROPERTY_EVENT("Event"), 
    PROPERTY_TASK("Task");

    private String title;

    private PropertyEventType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}

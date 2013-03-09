package org.estatio.dom.event;

public enum PropertyEventType implements Titled {

    PROPERTY_DISRUPTION("Disruption"), 
    PROPERTY_EXTENSION("Extension"), 
    PROPERTY_REFURBISHMENT("Extension"), 
    PROPERTY_EVENT("Event"), 
    PROPERTY_TASK("Event");

    private String title;

    private PropertyEventType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}

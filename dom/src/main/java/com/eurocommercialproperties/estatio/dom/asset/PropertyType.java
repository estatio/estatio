package com.eurocommercialproperties.estatio.dom.asset;


public enum PropertyType {

    COMMERCIAL("Commercial"), STORAGE("Storage"), MIXED("A bit of everything");

    private final String title;

    private PropertyType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}

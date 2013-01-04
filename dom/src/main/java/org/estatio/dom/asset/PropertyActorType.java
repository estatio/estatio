package org.estatio.dom.asset;

public enum PropertyActorType {

    PROPERTY_OWNER("Property Owner"), 
    PROPERTY_MANAGER("Property Manager"), 
    ASSET_MANAGER("Asset Manager"), 
    PROPERTY_CONTACT("Property Contact");

    private final String title;

    private PropertyActorType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}

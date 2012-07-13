package com.eurocommercialproperties.estatio.dom.party;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public enum PersonGenderType {

    MALE("Male"), FEMALE("Female"), UNKONWN("Uknown");

    private final String title;

    PersonGenderType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }

}

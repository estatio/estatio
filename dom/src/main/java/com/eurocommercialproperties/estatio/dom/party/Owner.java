package com.eurocommercialproperties.estatio.dom.party;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.util.TitleBuffer;

public class Owner extends Party {

    // {{ Title

    public String title() {
        TitleBuffer tb = new TitleBuffer(getReference()).append("-", getName());
        return tb.toString();
    }

    // }}

    // {{ Name (property)
    private String name;

    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

}

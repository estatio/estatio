package com.eurocommercialproperties.estatio.fixture.party;


import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.dom.party.Owner;
import com.eurocommercialproperties.estatio.dom.party.Owners;

public class PartiesFixture extends AbstractFixture {

    @Override
    public void install() {
    	createOwner("ACME Holdings");
    	createOwner("Hello World Properties");
    }

    private Owner createOwner(String name) {
        return owners.newOwner(name);
    }

    private Owners owners;

    public void setOwners(final Owners owners) {
        this.owners = owners;
    }
}

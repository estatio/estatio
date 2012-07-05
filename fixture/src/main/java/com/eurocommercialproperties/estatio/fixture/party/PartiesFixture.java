package com.eurocommercialproperties.estatio.fixture.party;


import org.apache.isis.applib.fixtures.AbstractFixture;

import com.eurocommercialproperties.estatio.dom.party.Owner;
import com.eurocommercialproperties.estatio.dom.party.Owners;

public class PartiesFixture extends AbstractFixture {

    @Override
    public void install() {
    	createOwner("ACME", "ACME Holdings");
    	createOwner("HELLOWORLD", "Hello World Properties");
    }

    private Owner createOwner(String reference, String name) {
        return owners.newOwner(reference, name);
    }

    private Owners owners;

    public void setOwners(final Owners owners) {
        this.owners = owners;
    }
}

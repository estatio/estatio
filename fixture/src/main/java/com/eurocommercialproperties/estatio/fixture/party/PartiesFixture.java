package com.eurocommercialproperties.estatio.fixture.party;

import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;

import org.apache.isis.applib.fixtures.AbstractFixture;

public class PartiesFixture extends AbstractFixture {

    @Override
    public void install() {
        createOrganisation("ACME", "ACME Holdings");
        createOrganisation("HELLOWORLD", "Hello World Properties");
        createOrganisation("TOPMODEL", "Topmodel Fashion");
        createOrganisation("MEDIAX", "Mediax Electronix");
    }

    private Party createOrganisation(String reference, String name) {
        Party p = parties.newOrganisation(name);
        p.setReference(reference);
        return p;
    }

    private Parties parties;

    public void setParties(final Parties parties) {
        this.parties = parties;
    }
}

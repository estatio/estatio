package org.estatio.fixture.party;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;

public class PartiesFixture extends AbstractFixture {

    @Override
    public void install() {
        createOrganisation("ACME", "ACME Holdings");
        createOrganisation("HELLOWORLD", "Hello World Properties");
        createOrganisation("TOPMODEL", "Topmodel Fashion");
        createOrganisation("MEDIAX", "Mediax Electronix");
        createPerson("JDOE", "J", "John", "Doe");
        createPerson("MMAGDALENA", "M", "Maria", "Magdalena");
    }

    private Party createPerson(String reference, String initials, String firstName, String lastName) {
        Party p = parties.newPerson(initials, firstName, lastName);
        p.setReference(reference);
        return p;
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

package org.estatio.dom.party.relationship;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Person;

public class PartyRelationshipTypeTest {

    @Test
    public void availableFor() {
        assertThat(PartyRelationshipType.toTitlesFor(Person.class, Organisation.class).size(), is(2));
        assertThat(PartyRelationshipType.toTitlesFor(Person.class, Person.class).size(), is(3));
    }

    @Test
    public void createFor() {
        Person p = new Person();
        Organisation o = new Organisation();
        PartyRelationship pr = PartyRelationshipType.createWithToTitle(o, p, PartyRelationshipType.EMPLOYMENT.toTitle());
        assertThat((Organisation) pr.getFrom(), is(o));
    }

}

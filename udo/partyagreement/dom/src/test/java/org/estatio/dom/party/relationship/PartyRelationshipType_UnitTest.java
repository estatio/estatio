package org.estatio.dom.party.relationship;

import org.junit.Test;

import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Person;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyRelationshipType_UnitTest {

    @Test
    public void availableFor() {
        assertThat(PartyRelationshipType.toTitlesFor(Person.class, Organisation.class)).hasSize(7);
        assertThat(PartyRelationshipType.toTitlesFor(Person.class, Person.class)).hasSize(3);
    }

    @Test
    public void createFor() {
        Person p = new Person();
        Organisation o = new Organisation();
        PartyRelationship pr = PartyRelationshipType.createWithToTitle(o, p, PartyRelationshipType.EMPLOYMENT.toTitle());
        assertThat((Organisation) pr.getFrom()).isEqualTo(o);
    }

}

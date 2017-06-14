package org.estatio.dom.party.relationship;

import org.junit.Test;

import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Person;

import static org.assertj.core.api.Assertions.assertThat;

public class PartyRelationshipType_Test {

    @Test
    public void availableFor() {
        assertThat(PartyRelationshipTypeEnum.toTitlesFor(Person.class, Organisation.class)).hasSize(7);
        assertThat(PartyRelationshipTypeEnum.toTitlesFor(Person.class, Person.class)).hasSize(3);
    }

    @Test
    public void createFor() {
        Person p = new Person();
        Organisation o = new Organisation();
        PartyRelationship pr = PartyRelationshipTypeEnum.createWithToTitle(o, p, PartyRelationshipTypeEnum.EMPLOYMENT.toTitle());
        assertThat((Organisation) pr.getFrom()).isEqualTo(o);
    }

}

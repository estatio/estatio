package org.estatio.module.party.dom.relationship;

import org.junit.Test;

import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.relationship.PartyRelationship;
import org.estatio.module.party.dom.relationship.PartyRelationshipTypeEnum;

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

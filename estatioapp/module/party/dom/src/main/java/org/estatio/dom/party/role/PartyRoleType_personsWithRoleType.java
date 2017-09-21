package org.estatio.dom.party.role;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.party.Person;

@Mixin(method = "coll")
public class PartyRoleType_personsWithRoleType extends PartyRoleType_partiesWithRoleTypeAbstract<Person> {

    public PartyRoleType_personsWithRoleType(final PartyRoleType partyRoleType) {
        super(partyRoleType, Person.class);
    }


}

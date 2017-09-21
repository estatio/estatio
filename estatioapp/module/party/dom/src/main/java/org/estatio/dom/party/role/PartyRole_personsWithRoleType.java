package org.estatio.dom.party.role;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.party.Person;

@Mixin(method = "coll")
public class PartyRole_personsWithRoleType extends PartyRole_partiesWithRoleTypeAbstract<Person> {

    public PartyRole_personsWithRoleType(final PartyRole partyRole) {
        super(partyRole, Person.class);
    }

}

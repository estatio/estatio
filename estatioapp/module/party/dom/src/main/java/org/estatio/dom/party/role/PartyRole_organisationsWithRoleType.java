package org.estatio.dom.party.role;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.party.Organisation;

@Mixin(method = "coll")
public class PartyRole_organisationsWithRoleType extends PartyRole_partiesWithRoleTypeAbstract<Organisation> {

    public PartyRole_organisationsWithRoleType(final PartyRole partyRole) {
        super(partyRole, Organisation.class);
    }

}

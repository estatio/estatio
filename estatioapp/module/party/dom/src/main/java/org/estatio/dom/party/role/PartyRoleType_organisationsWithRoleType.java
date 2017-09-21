package org.estatio.dom.party.role;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.dom.party.Organisation;

@Mixin(method = "coll")
public class PartyRoleType_organisationsWithRoleType extends PartyRoleType_partiesWithRoleTypeAbstract<Organisation> {

    public PartyRoleType_organisationsWithRoleType(final PartyRoleType partyRoleType) {
        super(partyRoleType, Organisation.class);
    }

}

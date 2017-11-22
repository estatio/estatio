package org.estatio.module.party.dom.role;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.party.dom.Organisation;

@Mixin(method = "coll")
public class PartyRoleType_organisationsWithRoleType extends PartyRoleType_partiesWithRoleTypeAbstract<Organisation> {

    public PartyRoleType_organisationsWithRoleType(final PartyRoleType partyRoleType) {
        super(partyRoleType, Organisation.class);
    }

}

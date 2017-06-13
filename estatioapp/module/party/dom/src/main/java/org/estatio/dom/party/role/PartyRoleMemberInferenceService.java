package org.estatio.dom.party.role;

import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.party.Person;

public interface PartyRoleMemberInferenceService {

    /**
     * Determines which {@link Person}(s) are members of the provided {@link PartyRoleType role}, optionally using the
     * provided domain object as context.
     *
     * @param partyRoleType - the role, eg MAILROOM
     * @param domainObjectAsContext - typically the payload of a task, eg a Document from which the appropriate mailroom personnel can be inferred
     */
    @Programmatic
    List<Person> inferMembersOf(IPartyRoleType partyRoleType, Object domainObjectAsContext);

}

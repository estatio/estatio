package org.estatio.dom.party.role;

import java.util.Arrays;
import java.util.List;

import org.estatio.dom.party.Person;

public abstract class PartyRoleMemberInferenceServiceAbstract<E extends Enum<E> & IPartyRoleType, T>
        implements PartyRoleMemberInferenceService {

    private final Class<T> domainClass;
    private final List<E> roleTypes;

    protected PartyRoleMemberInferenceServiceAbstract(
            final Class<T> domainClass, final E... roleTypes) {
        this.domainClass = domainClass;
        this.roleTypes = Arrays.asList(roleTypes);
    }

    @Override
    public List<Person> inferMembersOf(
            final IPartyRoleType partyRoleType,
            final Object domainObject) {

        if(!roleTypes.contains(partyRoleType)) {
            return null;
        }

        if(!domainClass.isAssignableFrom(domainObject.getClass())) {
            return null;
        }

        return doInfer((E) partyRoleType, domainClass.cast(domainObject));
    }

    protected abstract List<Person> doInfer(final E partyRoleType, final T domainObject);

}

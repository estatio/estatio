package org.estatio.dom.party.role;

import java.util.List;

import org.estatio.dom.party.Person;

public abstract class PartyRoleMemberInferenceServiceAbstract<E extends Enum<E> & IPartyRoleType, T>
        implements PartyRoleMemberInferenceService {

    private final Class<T> domainClass;
    private final Class<E> roleTypeEnumClass;

    protected PartyRoleMemberInferenceServiceAbstract(final Class<T> domainClass, final Class<E> roleTypeEnumClass) {
        this.domainClass = domainClass;
        this.roleTypeEnumClass = roleTypeEnumClass;
    }

    @Override
    public List<Person> inferMembersOf(
            final IPartyRoleType partyRoleType,
            final Object domainObject) {

        if(partyRoleType == null) {
            return null;
        }

        if(!roleTypeEnumClass.isAssignableFrom(partyRoleType.getClass())) {
            return null;
        }

        if(!domainClass.isAssignableFrom(domainObject.getClass())) {
            return null;
        }

        return doInferMembersOf((E) partyRoleType, domainClass.cast(domainObject));
    }

    @Override
    public List<Person> inferMembersOf(final IPartyRoleType partyRoleType) {

        if(partyRoleType == null) {
            return null;
        }

        if(!roleTypeEnumClass.isAssignableFrom(partyRoleType.getClass())) {
            return null;
        }

        return doInferMembersOf((E) partyRoleType);
    }

    protected abstract List<Person> doInferMembersOf(final E partyRoleType, final T domainObject);

    protected abstract List<Person> doInferMembersOf(final E partyRoleType);


}

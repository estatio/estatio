package org.estatio.module.party.dom.role;

import java.util.Arrays;
import java.util.List;

public abstract class PartyRoleTypeServiceSupportAbstract<E extends Enum<E> & IPartyRoleType> implements
        PartyRoleTypeServiceSupport<E> {

    private final Class<E> enumClass;

    public PartyRoleTypeServiceSupportAbstract(final Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public List<E> listAll() {
        return Arrays.asList(enumClass.getEnumConstants());
    }


}

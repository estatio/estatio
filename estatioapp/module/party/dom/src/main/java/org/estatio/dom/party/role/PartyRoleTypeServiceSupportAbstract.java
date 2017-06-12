package org.estatio.dom.party.role;

import java.util.Arrays;
import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.party.PartyRoleTypeEnum;

public class PartyRoleTypeServiceSupportAbstract<E extends Enum<E> & IPartyRoleType> implements
        PartyRoleTypeServiceSupport {

    private final Class<E> enumClass;

    public PartyRoleTypeServiceSupportAbstract(final Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public List<IPartyRoleType> listAll() {
        return Arrays.asList(enumClass.getEnumConstants());
    }


}

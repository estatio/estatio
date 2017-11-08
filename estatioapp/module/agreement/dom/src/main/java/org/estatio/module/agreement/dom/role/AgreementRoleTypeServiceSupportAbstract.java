package org.estatio.module.agreement.dom.role;

import java.util.Arrays;
import java.util.List;

public class AgreementRoleTypeServiceSupportAbstract<E extends Enum<E> & IAgreementRoleType> implements
        AgreementRoleTypeServiceSupport {

    private final Class<E> enumClass;

    public AgreementRoleTypeServiceSupportAbstract(final Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public List<IAgreementRoleType> listAll() {
        return Arrays.asList(enumClass.getEnumConstants());
    }

}

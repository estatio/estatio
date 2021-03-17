package org.estatio.module.agreement.dom.type;

import java.util.Arrays;
import java.util.List;

public abstract class AgreementTypeServiceSupportAbstract<E extends Enum<E> & IAgreementType> implements AgreementTypeServiceSupport {

    private final Class<E> enumClass;

    protected AgreementTypeServiceSupportAbstract(final Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public List<IAgreementType> listAll() {
        return Arrays.asList(enumClass.getEnumConstants());
    }
}

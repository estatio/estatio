package org.estatio.dom.agreement.type;

import java.util.Arrays;
import java.util.List;

public class AgreementTypeServiceSupportAbstract<E extends Enum<E> & IAgreementType> implements AgreementTypeServiceSupport {

    private final Class<E> enumClass;

    public AgreementTypeServiceSupportAbstract(final Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public List<IAgreementType> listAll() {
        return Arrays.asList(enumClass.getEnumConstants());
    }
}

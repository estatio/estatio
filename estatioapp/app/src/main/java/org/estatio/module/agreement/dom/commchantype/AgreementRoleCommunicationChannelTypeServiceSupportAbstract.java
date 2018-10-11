package org.estatio.module.agreement.dom.commchantype;

import java.util.Arrays;
import java.util.List;

public abstract class AgreementRoleCommunicationChannelTypeServiceSupportAbstract<E extends Enum<E> & IAgreementRoleCommunicationChannelType>
        implements AgreementRoleCommunicationChannelTypeServiceSupport {

    private final Class<E> enumClass;

    protected AgreementRoleCommunicationChannelTypeServiceSupportAbstract(final Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public List<IAgreementRoleCommunicationChannelType> listAll() {
        return Arrays.asList(enumClass.getEnumConstants());
    }

}

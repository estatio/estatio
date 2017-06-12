package org.estatio.dom.agreement.commchantype;

import java.util.Arrays;
import java.util.List;

public class AgreementRoleCommunicationChannelTypeServiceSupportAbstract<E extends Enum<E> & IAgreementRoleCommunicationChannelType>
        implements AgreementRoleCommunicationChannelTypeServiceSupport {

    private final Class<E> enumClass;

    public AgreementRoleCommunicationChannelTypeServiceSupportAbstract(final Class<E> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public List<IAgreementRoleCommunicationChannelType> listAll() {
        return Arrays.asList(enumClass.getEnumConstants());
    }

}

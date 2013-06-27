package org.estatio.dom.agreement;

import org.estatio.dom.EstatioDomainService;

import org.apache.isis.applib.annotation.Hidden;

@Hidden
public class AgreementRoleCommunicationChannels extends EstatioDomainService<AgreementRoleCommunicationChannel> {

    public AgreementRoleCommunicationChannels() {
        super(AgreementRoleCommunicationChannels.class, AgreementRoleCommunicationChannel.class);
    }


}

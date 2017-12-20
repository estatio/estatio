package org.incode.module.communications.dom.impl.commchannel;

public abstract class CommunicationChannelOwner_phoneNumberTitles extends CommunicationChannelOwner_communicationChannelTitlesAbstract {

    public CommunicationChannelOwner_phoneNumberTitles(final CommunicationChannelOwner owner, final String separator) {
        super(owner, separator, CommunicationChannelType.PHONE_NUMBER);
    }

}

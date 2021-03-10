package org.estatio.module.agreement.dom.commchantype;

import java.util.List;

/**
 * Each module that contributes a set of {@link IAgreementRoleCommunicationChannelType}s should implement this SPI service.
 */
public interface AgreementRoleCommunicationChannelTypeServiceSupport {

    List<IAgreementRoleCommunicationChannelType> listAll();
}

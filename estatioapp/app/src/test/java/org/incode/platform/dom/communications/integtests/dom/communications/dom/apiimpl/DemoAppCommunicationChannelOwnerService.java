package org.incode.platform.dom.communications.integtests.dom.communications.dom.apiimpl;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerService;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class DemoAppCommunicationChannelOwnerService extends
        CommunicationChannelOwnerService {

    public DemoAppCommunicationChannelOwnerService() {
        super(DemoAppCommunicationChannelOwnerService.class);
    }

}

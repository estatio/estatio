package org.estatio.dom.communicationchannel;

import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner;

public class CommunicationChannelOwnerForTesting implements CommunicationChannelOwner, HasAtPath {

    @Programmatic
    @Override
    public String getAtPath() {
        return null;
    }

}

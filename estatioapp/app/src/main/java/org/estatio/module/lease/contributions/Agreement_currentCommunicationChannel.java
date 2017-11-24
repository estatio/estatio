/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.module.lease.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.lease.dom.invoicing.AgreementCommunicationChannelLocator;

// TODO: there's only one subclass of this, so why not push this logic down?
public abstract class Agreement_currentCommunicationChannel {

    private final Agreement agreement;
    private final String art;
    private final String arcct;
    private final CommunicationChannelType cct;

    public Agreement_currentCommunicationChannel(
            final Agreement agreement,
            final String agreementRoleTypeTitle,
            final String agreementRoleCommunicationChannelTypeTitle) {
        this(agreement, agreementRoleTypeTitle, agreementRoleCommunicationChannelTypeTitle, null);
    }

    public Agreement_currentCommunicationChannel(
            final Agreement agreement,
            final String agreementRoleTypeTitle,
            final String agreementRoleCommunicationChannelTypeTitle,
            final CommunicationChannelType cct) {
        this.agreement = agreement;
        art = agreementRoleTypeTitle;
        arcct = agreementRoleCommunicationChannelTypeTitle;
        this.cct = cct;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public CommunicationChannel $$() {
        final List<CommunicationChannel> channels = locator.current(agreement, art, arcct, cct);
        return channels.isEmpty() ? null : channels.get(0);
    }

    @Inject
    AgreementCommunicationChannelLocator locator;

}

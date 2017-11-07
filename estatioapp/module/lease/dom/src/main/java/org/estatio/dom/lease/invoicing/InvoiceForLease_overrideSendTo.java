
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
package org.estatio.dom.lease.invoicing;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.base.dom.Dflt;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.dom.invoice.Invoice;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;

/**
 * TODO: inline this mixin
 */
@Mixin
public class InvoiceForLease_overrideSendTo {

    private final InvoiceForLease invoiceForLease;

    public InvoiceForLease_overrideSendTo(final InvoiceForLease invoiceForLease) {
        this.invoiceForLease = invoiceForLease;
    }

    @Action()
    public Invoice $$(final CommunicationChannel communicationChannel) {
        invoiceForLease.setSendTo(communicationChannel);
        return invoiceForLease;
    }

    public CommunicationChannel default0$$() {
        return Dflt.of(choices0$$());
    }
    public List<CommunicationChannel> choices0$$() {
        return locator.onFile(
                invoiceForLease.getLease(),
                LeaseAgreementRoleTypeEnum.TENANT.getTitle(),
                CommunicationChannelType.EMAIL_ADDRESS, CommunicationChannelType.POSTAL_ADDRESS);
    }

    public String disable$$() {
        return choices0$$().isEmpty() ? "No communication channels available for the tenant" : null;
    }

    @Inject
    AgreementCommunicationChannelLocator locator;



}

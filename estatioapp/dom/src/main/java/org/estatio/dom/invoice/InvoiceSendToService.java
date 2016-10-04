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
package org.estatio.dom.invoice;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communications.AgreementRoleCommunicationChannelLocator;
import org.estatio.dom.lease.LeaseConstants;

@DomainService(nature = NatureOfService.DOMAIN)
public class InvoiceSendToService {

    CommunicationChannel firstTenantInvoiceAddress(final Agreement agreement) {
        final List<CommunicationChannel> channels = tenantInvoiceAddresses(agreement);
        return channels.size() > 0 ? channels.get(0): null;
    }

    List<CommunicationChannel> tenantInvoiceAddresses(final Agreement agreement) {
        final CommunicationChannelType ccType = null;
        return locator.locate(
                        agreement, LeaseConstants.ART_TENANT, LeaseConstants.ARCCT_INVOICE_ADDRESS, ccType);

    }

    @Inject
    AgreementRoleCommunicationChannelLocator locator;

}

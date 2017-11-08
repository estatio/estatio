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
package org.estatio.module.invoice.subscriptions;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.scratchpad.Scratchpad;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.dom.UdoDomainService;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceRepository;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class CommunicationChannelSubscriptions extends UdoDomainService<CommunicationChannelSubscriptions> {

    public CommunicationChannelSubscriptions() {
        super(CommunicationChannelSubscriptions.class);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final CommunicationChannel.RemoveEvent ev) {
        CommunicationChannel sourceCommunicationChannel = ev.getSource();
        CommunicationChannel replacementCommunicationChannel = ev.getReplacement();

        List<Invoice> notYetInvoiced;
        List<Invoice> alreadyInvoiced;

        switch (ev.getEventPhase()) {
        case VALIDATE:
            List<Invoice> invoices = invoiceRepository.findBySendTo(sourceCommunicationChannel);
            notYetInvoiced = invoices.stream()
                                    .filter(invoice -> invoice.getInvoiceNumber() == null)
                                    .collect(Collectors.toList());
            alreadyInvoiced = invoices.stream()
                                      .filter(invoice -> invoice.getInvoiceNumber() != null)
                                      .collect(Collectors.toList());
            scratchpad.put("notYetInvoiced", notYetInvoiced);
            scratchpad.put("alreadyInvoiced", alreadyInvoiced);

            // we veto if no replacement was provided and there are some invoices not yet invoiced.
            // (it doesn't matter if no replacement was provided only for already-invoiced invoices)
            if(replacementCommunicationChannel == null && !notYetInvoiced.isEmpty()) {
                ev.invalidate(TranslatableString.tr(
                    "Communication channel is being used (as the 'sendTo' channel for {num} invoice(s); "
                            + "provide a replacement",
                        "num", notYetInvoiced.size()));
            }
            break;

        case EXECUTING:
            notYetInvoiced = (List<Invoice>) scratchpad.get("notYetInvoiced");
            alreadyInvoiced = (List<Invoice>) scratchpad.get("alreadyInvoiced");

            for (Invoice invoice : notYetInvoiced) {
                invoice.setSendTo(replacementCommunicationChannel);
            }
            for (Invoice invoice : alreadyInvoiced) {
                if(invoice.getInvoiceNumber() != null) {
                    // just blank out
                    invoice.setSendTo(null);
                }
            }
            break;
        default:
            break;
        }
    }

    private transient UUID onCommunicationChannelRemoveScratchpadKey;



    @Inject
    private Scratchpad scratchpad;

    @Inject
    private InvoiceRepository invoiceRepository;

}

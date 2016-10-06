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
package org.estatio.dom.invoice.viewmodel.dnc;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.communications.dom.mixins.Document_email;
import org.incode.module.communications.dom.mixins.Document_print;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.EmailAddress;
import org.estatio.dom.communicationchannel.PostalAddress;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDateStatus;

@Mixin
public class InvoiceSummaryForPropertyDueDateStatus_sendCommunications {

    private final InvoiceSummaryForPropertyDueDateStatus invoiceSummary;

    public InvoiceSummaryForPropertyDueDateStatus_sendCommunications(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        this.invoiceSummary = invoiceSummary;
    }


    public InvoiceSummaryForPropertyDueDateStatus $$(
            final DocumentTemplate documentTemplate) throws IOException {

        final List<Invoice> invoices = invoiceSummary.getInvoices();
        for (Invoice invoice : invoices) {
            Document document = invoiceDocumentTemplateService.createAndAttach(invoice, documentTemplate);

            final CommunicationChannel sendTo = invoice.getSendTo();
            if(sendTo != null) {
                final CommunicationChannelType channelType = sendTo.getType();
                switch (channelType) {

                case EMAIL_ADDRESS:
                    final Document_email emailMixin = emailMixin(document);
                    final EmailAddress emailAddress = emailMixin.default0$$();
                    if(emailAddress != null) {
                        final String cc = emailMixin.default1$$();
                        final String bcc = emailMixin.default2$$();
                        final String subject = emailMixin.default3$$();
                        final String message = emailMixin.default4$$();
                        emailMixin.$$(emailAddress, cc, bcc, subject, message);
                    }
                    break;
                case POSTAL_ADDRESS:
                    final Document_print printMixin = printMixin(document);
                    final PostalAddress postalAddress = printMixin.default0$$();
                    if(postalAddress != null) {
                        printMixin.$$(postalAddress);
                    }
                    break;
                case PHONE_NUMBER:
                case FAX_NUMBER:
                    break;
                }
            }
        }

        return this.invoiceSummary;
    }

    private Document_email emailMixin(final Document document) {
        return factoryService.mixin(Document_email.class, document);
    }

    private Document_print printMixin(final Document document) {
        return factoryService.mixin(Document_print.class, document);
    }

    public String disable$$() {
        return invoiceSummary.getInvoices().isEmpty()? "No invoices": null;
    }

    public List<DocumentTemplate> choices0$$() {
        final List<Invoice> invoices = invoiceSummary.getInvoices();
        final Invoice anyInvoice = invoices.get(0);
        return invoiceDocumentTemplateService.templatesFor(anyInvoice);
    }

    @Inject
    InvoiceDocumentTemplateService invoiceDocumentTemplateService;

    @Inject
    FactoryService factoryService;
}

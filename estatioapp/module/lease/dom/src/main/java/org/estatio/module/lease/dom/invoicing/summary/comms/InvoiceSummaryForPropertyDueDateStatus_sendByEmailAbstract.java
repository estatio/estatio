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
package org.estatio.module.lease.dom.invoicing.summary.comms;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.invoice.DocumentTypeData;
import org.estatio.dom.invoice.Invoice;
import org.estatio.module.lease.dom.invoicing.comms.InvoiceForLease_sendByEmail;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;

public abstract class InvoiceSummaryForPropertyDueDateStatus_sendByEmailAbstract extends InvoiceSummaryForPropertyDueDateStatus_sendAbstract {

    public InvoiceSummaryForPropertyDueDateStatus_sendByEmailAbstract(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final DocumentTypeData documentTypeData) {
        super(invoiceSummary, documentTypeData, CommunicationChannelType.EMAIL_ADDRESS);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public InvoiceSummaryForPropertyDueDateStatus $$() throws IOException {

        final List<InvoiceAndDocument> invoiceAndDocuments = invoiceAndDocumentsToSend();
        for (InvoiceAndDocument invoiceAndDocument : invoiceAndDocuments) {
            final Invoice invoice = invoiceAndDocument.getInvoice();
            final Document document = invoiceAndDocument.getDocument();

            final InvoiceForLease_sendByEmail invoice_sendByEmail = invoice_email(invoice);

            final EmailAddress emailAddress = invoice_sendByEmail.default1$$(document);
            final String cc = invoice_sendByEmail.default2$$(document);
            final String bcc = invoice_sendByEmail.default5$$(document);

            invoice_sendByEmail.$$(document, emailAddress, cc, null, null, bcc, null);
        }
        return this.invoiceSummary;
    }

    public String disable$$() {
        return invoiceAndDocumentsToSend().isEmpty()? "No documents available to be send by email": null;
    }

    @Override
    Predicate<InvoiceAndDocument> filter() {
        return Predicates.and(
                        invoiceAndDocument -> !exclude(invoiceAndDocument),
                        canBeSentByEmail()
                );
    }

    /**
     * Optional hook to allow subclasses to further restrict the documents that can be sent.
     */
    protected boolean exclude(final InvoiceAndDocument invoiceAndDocument) {
        return false;
    }

    private Predicate<InvoiceAndDocument> canBeSentByEmail() {
        return invoiceAndDocument -> {
            final InvoiceForLease_sendByEmail emailMixin = invoice_email(invoiceAndDocument.getInvoice());
            final EmailAddress emailAddress = emailMixin.default1$$(invoiceAndDocument.getDocument());
            return emailAddress != null;
        };
    }

    private InvoiceForLease_sendByEmail invoice_email(final Invoice invoice) {
        return factoryService.mixin(InvoiceForLease_sendByEmail.class, invoice);
    }

    @Inject
    FactoryService factoryService;
}

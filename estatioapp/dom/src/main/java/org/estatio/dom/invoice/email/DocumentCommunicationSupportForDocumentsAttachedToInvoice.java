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
package org.estatio.dom.invoice.email;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.communications.dom.spi.CommHeaderAbstract;
import org.incode.module.communications.dom.spi.CommHeaderForEmail;
import org.incode.module.communications.dom.spi.CommHeaderForPrint;
import org.incode.module.communications.dom.spi.DocumentCommunicationSupport;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.paperclips.Paperclip;
import org.incode.module.documents.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.dom.impl.types.DocumentTypeRepository;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communications.AgreementCommunicationChannelLocator;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentCommunicationSupportForDocumentsAttachedToInvoice implements DocumentCommunicationSupport {

    @Override
    public DocumentType emailCoverNoteDocumentTypeFor(final Document document) {

        final Invoice invoice = paperclipRepository.paperclipAttaches(document, Invoice.class);
        if (invoice == null) {
            return null;
        }

        return documentTypeRepository.findByReference(Constants.EMAIL_COVER_NOTE_DOCUMENT_TYPE);
    }

    @Override
    public void inferEmailHeaderFor(
            final Document document,
            final CommHeaderForEmail header) {

        header.setSubject(document.getName());

        inferToHeader(document, header, CommunicationChannelType.EMAIL_ADDRESS);
    }

    @Override
    public void inferPrintHeaderFor(
            final Document document, final CommHeaderForPrint header) {

        inferToHeader(document, header, CommunicationChannelType.POSTAL_ADDRESS);
    }

    private <T extends CommunicationChannel> void inferToHeader(
            final Document document,
            final CommHeaderAbstract<T> header,
            final CommunicationChannelType channelType) {
        final List<Paperclip> paperclips = paperclipRepository.findByDocument(document);
        for (final Paperclip paperclip : paperclips) {
            final Object attachedTo = paperclip.getAttachedTo();
            if(attachedTo instanceof Invoice) {
                final Invoice invoice = (Invoice) attachedTo;
                addTo(invoice, header, channelType);
            }
        }
        if(header.getToChoices().isEmpty()) {
            header.setDisabledReason("Could not find any email address for tenant");
        }
    }

    private <T extends CommunicationChannel> void addTo(
            final Invoice invoice,
            final CommHeaderAbstract<T> header,
            final CommunicationChannelType channelType) {

        final Lease lease = invoice.getLease();

        // current choice(s) and default
        final List current = locator.current(lease,
                LeaseConstants.ART_TENANT, LeaseConstants.ARCCT_INVOICE_ADDRESS, channelType);
        header.getToChoices().addAll(current);
        final CommunicationChannel sendTo = invoice.getSendTo();

        if(sendTo != null && sendTo.getType() == channelType) {
            header.setToDefault((T) sendTo);
        } else {
            header.setToDefault((T) firstIfAny(current));
        }

        // additional choices (those on file)
        final List onFile = locator.onFile(lease, LeaseConstants.ART_TENANT, channelType);
        header.getToChoices().addAll(onFile);
    }

    private static <T> T firstIfAny(final List<T> list) {
        return list.isEmpty() ? null : list.iterator().next();
    }


    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    AgreementCommunicationChannelLocator locator;

}

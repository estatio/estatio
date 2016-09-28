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

import org.incode.module.communications.dom.spi.DocumentEmailSupport;
import org.incode.module.communications.dom.spi.EmailHeader;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.paperclips.Paperclip;
import org.incode.module.documents.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.dom.impl.types.DocumentTypeRepository;

import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communications.AgreementRoleCommunicationChannelLocator;
import org.estatio.dom.invoice.Constants;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentEmailSupportForDocumentsAttachedToInvoice implements DocumentEmailSupport {

    @Override
    public DocumentType emailCoverNoteDocumentTypeFor(final Document document) {

        final Invoice invoice = paperclipRepository.paperclipAttaches(document, Invoice.class);
        if (invoice == null) {
            return null;
        }

        return documentTypeRepository.findByReference(Constants.EMAIL_COVER_NOTE_DOCUMENT_TYPE);
    }

    @Override
    public void inferHeaderFor(
            final Document document,
            final EmailHeader header) {

        header.setSubject(document.getName());

        final List<Paperclip> paperclips = paperclipRepository.findByDocument(document);
        for (final Paperclip paperclip : paperclips) {
            final Object attachedTo = paperclip.getAttachedTo();
            if(attachedTo instanceof Invoice) {
                final Invoice invoice = (Invoice) attachedTo;
                final Lease lease = invoice.getLease();
                appendEmailAddressesFor(lease, header);
            }
        }
    }

    private void appendEmailAddressesFor(final Lease lease, final EmailHeader header) {
        final List emailAddresses =
                locator.locate(
                        lease, LeaseConstants.ART_TENANT, LeaseConstants.ARCCT_INVOICE_ADDRESS,
                        CommunicationChannelType.EMAIL_ADDRESS);
        header.getToSet().addAll(emailAddresses);

        if(header.getToSet().isEmpty()) {
            header.setDisabledReason("Could not find any email invoice address for tenant");
        }
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    AgreementRoleCommunicationChannelLocator locator;

}

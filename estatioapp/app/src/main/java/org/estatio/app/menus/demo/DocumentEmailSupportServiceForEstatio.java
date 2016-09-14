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
package org.estatio.app.menus.demo;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.communications.dom.spi.DocumentEmailSupportService;
import org.incode.module.communications.dom.spi.EmailHeader;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.links.Paperclip;
import org.incode.module.documents.dom.impl.links.PaperclipRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.dom.impl.types.DocumentTypeRepository;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.communicationchannel.EmailAddress;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.party.Party;
import org.estatio.fixture.documents.DocumentTypeAndTemplateFSForBlank;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentEmailSupportServiceForEstatio implements DocumentEmailSupportService {

    @Override
    public DocumentType blankDocumentType() {

        final DocumentType blankDocType =
                documentTypeRepository.findByReference(DocumentTypeAndTemplateFSForBlank.TYPE_REF);

        return blankDocType;
    }

    @Override
    public void inferHeaderFor(
            final Document document,
            final EmailHeader header) {

        header.setSubject(document.getName());

        final Set<EmailAddress> toSet = header.getToSet();
        final Set<EmailAddress> ccSet = header.getCcSet();
        final Set<EmailAddress> bccSet = header.getBccSet();

        final List<Paperclip> paperclips = paperclipRepository.findByDocument(document);
        for (final Paperclip paperclip : paperclips) {
            final Object attachedTo = paperclip.getAttachedTo();
            if(attachedTo instanceof Party) {
                final Party party = (Party) attachedTo;
                appendEmailAddressesFor(party, toSet, ccSet, bccSet);
            } else if(attachedTo instanceof Invoice) {
                final Invoice invoice = (Invoice) attachedTo;
                final Party buyer = invoice.getBuyer();
                appendEmailAddressesFor(buyer, toSet, ccSet, toSet);
            } else if(attachedTo instanceof Lease) {
                final Lease lease = (Lease) attachedTo;
                final Party party = lease.getSecondaryParty();
                appendEmailAddressesFor(party, toSet, ccSet, toSet);
            }
        }
    }

    private void appendEmailAddressesFor(
            final Party party,
            final Set<EmailAddress> toSet, final Set<EmailAddress> ccSet, final Set<EmailAddress> bccSet) {
        appendEmailAddressesFor(party, toSet);
        appendEmailAddressesFor(party, ccSet);
        appendEmailAddressesFor(party, bccSet);
    }

    private void appendEmailAddressesFor(
            final Party party, final Set<EmailAddress> emailAddresses) {
        final SortedSet<CommunicationChannel> channels = communicationChannelRepository.findByOwner(party);
        for (CommunicationChannel channel : channels) {
            if(channel instanceof EmailAddress) {
                emailAddresses.add((EmailAddress) channel);
            }
        }
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    CommunicationChannelRepository communicationChannelRepository;

    @Inject
    PaperclipRepository paperclipRepository;

}

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

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannel;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelType;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypeRepository;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypeRepository;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.EmailAddress;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.fixture.documents.DocumentTypeAndTemplateFSForBlank;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentEmailSupportServiceForDocumentsAttachedToInvoice implements DocumentEmailSupportService {

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

        // ref data lookup
        final AgreementRoleType inRoleOfTenant =
                agreementRoleTypeRepository.findByTitle(LeaseConstants.ART_TENANT);
        final AgreementRoleCommunicationChannelType inRoleOfinvoiceAddress =
                agreementRoleCommunicationChannelTypeRepository.findByTitle(LeaseConstants.ARCCT_INVOICE_ADDRESS);

        final SortedSet<AgreementRole> leaseRoles = lease.getRoles();
        for (final AgreementRole role : leaseRoles) {
            if(role.getType() == inRoleOfTenant) {
                final SortedSet<AgreementRoleCommunicationChannel> rolesOfChannels = role.getCommunicationChannels();
                for (AgreementRoleCommunicationChannel roleOfChannel : rolesOfChannels) {
                    if(roleOfChannel.getType() == inRoleOfinvoiceAddress) {
                        final CommunicationChannel communicationChannel = roleOfChannel.getCommunicationChannel();
                        if(roleOfChannel.isCurrent() &&
                           communicationChannel.getType() == CommunicationChannelType.EMAIL_ADDRESS) {
                            header.getToSet().add((EmailAddress) communicationChannel);
                        }
                    }
                }
            }
        }
        if(header.getToSet().isEmpty()) {
            header.setDisabledReason("Could not locate any email addresses for buyer of lease that are marked as the current invoice address.");
        }
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    CommunicationChannelRepository communicationChannelRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

    @Inject
    AgreementRoleCommunicationChannelTypeRepository agreementRoleCommunicationChannelTypeRepository;

}

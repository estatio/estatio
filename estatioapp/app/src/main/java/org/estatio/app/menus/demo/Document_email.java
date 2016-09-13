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

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.joda.time.DateTime;

import org.apache.isis.applib.NonRecoverableException;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.communications.dom.CommunicationsModule;
import org.incode.module.communications.dom.impl.comms.CommChannelRole;
import org.incode.module.communications.dom.impl.comms.CommChannelRoleType;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.DocumentAbstract;
import org.incode.module.documents.dom.impl.docs.DocumentRepository;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.impl.links.Paperclip;
import org.incode.module.documents.dom.impl.links.PaperclipRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;
import org.incode.module.documents.dom.impl.types.DocumentTypeRepository;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.EmailAddress;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.party.Party;
import org.estatio.fixture.documents.DocumentTypeForBlank;

@Mixin
public class Document_email  {

    private final Document document;

    public Document_email(final Document document) {
        this.document = document;
    }

    @Action()
    public Communication $$(
            @ParameterLayout(named = "To")
            final EmailAddress to,
            @Parameter(maxLength = CommunicationsModule.JdoColumnLength.SUBJECT)
            @ParameterLayout(named = "Subject")
            final String subject,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Covering note", multiLine = 14)
            final String coveringNote) throws IOException {


        // create comm and correspondent.
        final DateTime commSent = clockService.nowAsDateTime();

        final Communication communication = new Communication(
                CommunicationChannelType.EMAIL_ADDRESS, document.getAtPath(), subject, commSent);

        final CommChannelRole toRole = new CommChannelRole(CommChannelRoleType.TO, communication, to);

        communication.getCorrespondents().add(toRole);
        repositoryService.persistAndFlush(communication);


        // attach this doc to email ...
        paperclipRepository.attach(document, "attachment", communication);

        // ... and create and attach cover note
        if(coveringNote != null) {
            final DocumentType blankDocType = documentTypeRepository.findByReference(DocumentTypeForBlank.REF);

            final List<DocumentTemplate> docTemplates = documentTemplateRepository
                    .findByTypeAndApplicableToAtPath(blankDocType, document.getAtPath());
            final DocumentTemplate template = docTemplates.get(0);

            final DocumentAbstract coverNote = template.render(coveringNote, "Email cover for: " + document.getName());
            paperclipRepository.attach(coverNote, "cover", communication);
        }

        // send the email
        List<String> toList = Lists.newArrayList(to.getEmailAddress());
        final List<String> ccList = Lists.newArrayList();
        final List<String> bccList = Lists.newArrayList();
        final boolean send = emailService.send(toList, ccList, bccList, subject, coveringNote, document.asDataSource());
        if(!send) {
            throw new NonRecoverableException("Failed to send email");
        }

        return communication;
    }

    public String disable$$() {
        return emailService == null || !emailService.isConfigured() ? "Email service not configured": null;
    }

    public Set<EmailAddress> choices0$$() {
        final Set<EmailAddress> emailAddresses = Sets.newTreeSet();
        final List<Paperclip> paperclips = paperclipRepository.findByDocument(document);
        for (Paperclip paperclip : paperclips) {
            final Object attachedTo = paperclip.getAttachedTo();
            if(attachedTo instanceof Party) {
                appendEmailAddressesFor((Party) attachedTo, emailAddresses);
            } else if(attachedTo instanceof Invoice) {
                final Invoice invoice = (Invoice) attachedTo;
                final Party buyer = invoice.getBuyer();
                appendEmailAddressesFor(buyer, emailAddresses);
            } else if(attachedTo instanceof Lease) {
                final Lease lease = (Lease) attachedTo;
                appendEmailAddressesFor(lease.getSecondaryParty(), emailAddresses);
            }
        }
        return emailAddresses;
    }

    private void appendEmailAddressesFor(final Party party, final Set<EmailAddress> emailAddresses) {
        final SortedSet<CommunicationChannel> channels = communicationChannelRepository.findByOwner(party);
        for (CommunicationChannel channel : channels) {
            if(channel instanceof EmailAddress) {
                emailAddresses.add((EmailAddress) channel);
            }
        }
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    DocumentTemplateRepository documentTemplateRepository;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    PaperclipRepository paperclipRepository;


    @Inject
    CommunicationChannelRepository communicationChannelRepository;

    @Inject
    ClockService clockService;

    @Inject
    EmailService emailService;

    @Inject
    MessageService messageService;

}

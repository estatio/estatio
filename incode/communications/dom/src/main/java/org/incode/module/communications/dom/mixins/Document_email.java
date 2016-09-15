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
package org.incode.module.communications.dom.mixins;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;

import org.apache.isis.applib.NonRecoverableException;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.communications.dom.CommunicationsModule;
import org.incode.module.communications.dom.impl.comms.CommChannelRole;
import org.incode.module.communications.dom.impl.comms.CommChannelRoleType;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.spi.DocumentEmailSupportService;
import org.incode.module.communications.dom.spi.EmailHeader;
import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.DocumentAbstract;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.impl.links.PaperclipRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;

import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.EmailAddress;

/**
 * Provides the ability to send an email.
 */
@Mixin
public class Document_email  {

    public static final int EMAIL_COVERING_NOTE_MULTILINE = 14;

    public static final String PAPERCLIP_ROLE_ATTACHMENT = "attachment";
    public static final String PAPERCLIP_ROLE_COVER = "cover";

    private final Document document;

    public Document_email(final Document document) {
        this.document = document;
    }

    public static class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<Document_email> { }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public Communication $$(
            @ParameterLayout(named = "to:")
            final EmailAddress to,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "cc:")
            final EmailAddress cc,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "bcc:")
            final EmailAddress bcc,
            @Parameter(maxLength = CommunicationsModule.JdoColumnLength.SUBJECT)
            @ParameterLayout(named = "Subject")
            final String subject,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Covering note", multiLine = EMAIL_COVERING_NOTE_MULTILINE)
            final String coveringNote) throws IOException {


        // create comm and correspondent.
        final DateTime commSent = clockService.nowAsDateTime();

        final Communication communication = new Communication(
                CommunicationChannelType.EMAIL_ADDRESS, document.getAtPath(), subject, commSent);

        final CommChannelRole toRole = new CommChannelRole(CommChannelRoleType.TO, communication, to);

        communication.getCorrespondents().add(toRole);
        repositoryService.persistAndFlush(communication);


        // attach this doc to email ...
        paperclipRepository.attach(document, PAPERCLIP_ROLE_ATTACHMENT, communication);

        // ... and create and attach cover note
        if(coveringNote != null) {

            final DocumentTemplate template = determineBlankDocumentTemplate();

            final DocumentAbstract coverNote = template.render(coveringNote, null);
            paperclipRepository.attach(coverNote, PAPERCLIP_ROLE_COVER, communication);
        }

        // send the email
        final boolean send = emailService.send(
                                    asList(to), asList(cc), asList(bcc),
                                    subject, coveringNote,
                                    document.asDataSource());

        // fail-fast if there was a problem (don't persist anything).
        if(!send) {
            throw new NonRecoverableException("Failed to send email");
        }

        return communication;
    }

    public String disable$$() {
        if (emailService == null || !emailService.isConfigured()) {
            return "Email service not configured";
        }
        if(determineBlankDocumentTemplate() == null) {
            return "Blank document type/template not provided";
        }
        return null;
    }

    public EmailAddress default0$$() {
        return ifOnlyOne(choices0$$());
    }

    public Set<EmailAddress> choices0$$() {
        return determineEmailHeader().getToSet();
    }

    public Set<EmailAddress> choices1$$() {
        return determineEmailHeader().getCcSet();
    }

    public Set<EmailAddress> choices2$$() {
        return determineEmailHeader().getBccSet();
    }

    public String default3$$() {
        return determineEmailHeader().getSubject();
    }

    public String default4$$() {
        return "Please find attached";
    }

    private DocumentTemplate determineBlankDocumentTemplate() {
        final DocumentType blankDocType = determineBlankDocumentType();
        if(blankDocType == null) {
            return null;
        }
        final List<DocumentTemplate> docTemplates = documentTemplateRepository
                .findByTypeAndApplicableToAtPath(blankDocType, document.getAtPath());
        return docTemplates.isEmpty() ? null : docTemplates.get(0);
    }

    private DocumentType determineBlankDocumentType() {
        return queryResultsCache.execute(() -> {
            if(documentDocumentEmailSupportServices != null) {
                for (DocumentEmailSupportService supportService : documentDocumentEmailSupportServices) {
                    return supportService.blankDocumentType();
                }
            }
            return null;
        }, Document_email.class, "determineBlankDocumentType", document);
    }

    private EmailHeader determineEmailHeader() {
        return queryResultsCache.execute(() -> {
            final EmailHeader emailHeader = new EmailHeader();
            if(documentDocumentEmailSupportServices != null) {
                for (DocumentEmailSupportService supportService : documentDocumentEmailSupportServices) {
                    supportService.inferHeaderFor(document, emailHeader);;
                }
            }
            return emailHeader;
        }, Document_email.class, "determineEmailHeader", document);
    }

    private static List<String> asList(EmailAddress emailAddress) {
        return emailAddress != null
                ? Collections.singletonList(emailAddress.getEmailAddress())
                : Collections.emptyList();
    }

    private static <T> T ifOnlyOne(final Set<T> set) {
        return set.size() == 1? set.iterator().next(): null;
    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    RepositoryService repositoryService;

    @Inject
    List<DocumentEmailSupportService> documentDocumentEmailSupportServices;

    @Inject
    DocumentTemplateRepository documentTemplateRepository;

    @Inject
    ClockService clockService;

    @Inject
    EmailService emailService;

    @Inject
    PaperclipRepository paperclipRepository;

}

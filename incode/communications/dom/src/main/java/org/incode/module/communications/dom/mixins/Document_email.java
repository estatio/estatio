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
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.incode.module.communications.dom.CommunicationsModule;
import org.incode.module.communications.dom.impl.comms.CommChannelRoleType;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.spi.CommHeaderForEmail;
import org.incode.module.communications.dom.spi.DocumentCommunicationSupport;
import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.DocumentState;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.documents.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.documents.dom.impl.types.DocumentType;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.communicationchannel.EmailAddress;

/**
 * Provides the ability to send an email.
 */
@Mixin
public class Document_email  {

    public static final int EMAIL_COVERING_NOTE_MULTILINE = 14;

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
            final EmailAddress toChannel,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = JdoColumnLength.EMAIL_ADDRESS,
                    regexPattern = RegexValidation.CommunicationChannel.EMAIL,
                    regexPatternReplacement = RegexValidation.CommunicationChannel.EMAIL_DESCRIPTION)
            @ParameterLayout(named = "cc:")
            final String cc,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = JdoColumnLength.EMAIL_ADDRESS,
                    regexPattern = RegexValidation.CommunicationChannel.EMAIL,
                    regexPatternReplacement = RegexValidation.CommunicationChannel.EMAIL_DESCRIPTION)
            @ParameterLayout(named = "bcc:")
            final String bcc,
            @Parameter(maxLength = CommunicationsModule.JdoColumnLength.SUBJECT)
            @ParameterLayout(named = "Subject")
            final String subject,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Covering note message", multiLine = EMAIL_COVERING_NOTE_MULTILINE)
            final String message) throws IOException {

        // create comm and correspondents
        final DateTime commSent = clockService.nowAsDateTime();

        final Communication communication = Communication.newEmail(document.getAtPath(), subject, commSent);
        serviceRegistry2.injectServicesInto(communication);

        communication.addCorrespondent(CommChannelRoleType.TO, toChannel);
        communication.addCorrespondentIfAny(CommChannelRoleType.CC, cc);
        communication.addCorrespondentIfAny(CommChannelRoleType.BCC, bcc);

        final ApplicationUser currentUser = meService.me();
        final String currentUserEmailAddress = currentUser.getEmailAddress();
        communication.addCorrespondentIfAny(CommChannelRoleType.PREPARED_BY, currentUserEmailAddress);

        repositoryService.persistAndFlush(communication);

        // attach this doc to communication
        paperclipRepository.attach(document, DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT, communication);

        // create and attach cover note
        final DocumentTemplate coverNoteTemplate = determineEmailCoverNoteTemplate();
        final Document coverNoteDoc = coverNoteTemplate.createDocumentUsingBinding(this.document, message);

        coverNoteDoc.render(coverNoteTemplate, this.document, message);

        paperclipRepository.attach(coverNoteDoc, DocumentConstants.PAPERCLIP_ROLE_COVER, communication);

        // schedule the email to be sent
        communication.scheduleSend(subject);

        return communication;
    }

    public String disable$$() {
        if (emailService == null || !emailService.isConfigured()) {
            return "Email service not configured";
        }
        if (document.getState() != DocumentState.RENDERED) {
            return "Document not yet rendered";
        }
        if(determineEmailCoverNoteTemplate() == null) {
            return "Email cover note type/template not provided";
        }
        if(determineEmailHeader().getDisabledReason() != null) {
            return determineEmailHeader().getDisabledReason();
        }
        if(choices0$$().isEmpty()) {
            return "Could not locate any email address(es) to sent to";
        }
        return null;
    }

    public EmailAddress default0$$() {
        return determineEmailHeader().getToDefault();
    }

    public Set<EmailAddress> choices0$$() {
        return determineEmailHeader().getToChoices();
    }

    public String default1$$() {
        return determineEmailHeader().getCc();
    }

    public String default2$$() {
        return determineEmailHeader().getBcc();
    }

    public String default3$$() {
        return determineEmailHeader().getSubject();
    }

    public String default4$$() {
        return "";
    }

    private DocumentTemplate determineEmailCoverNoteTemplate() {
        final DocumentType blankDocType = determineEmailCoverNoteDocumentType();
        if(blankDocType == null) {
            return null;
        }
        final List<DocumentTemplate> docTemplates = documentTemplateRepository
                .findByTypeAndApplicableToAtPath(blankDocType, this.document.getAtPath());
        return docTemplates.isEmpty() ? null : docTemplates.get(0);
    }

    private DocumentType determineEmailCoverNoteDocumentType() {
        return queryResultsCache.execute(() -> {
            if(documentCommunicationSupports != null) {
                for (DocumentCommunicationSupport supportService : documentCommunicationSupports) {
                    final DocumentType documentType = supportService.emailCoverNoteDocumentTypeFor(document);
                    if(documentType != null) {
                        return documentType;
                    }
                }
            }
            return null;
        }, Document_email.class, "determineEmailCoverNoteDocumentType", document);
    }

    private CommHeaderForEmail determineEmailHeader() {
        return queryResultsCache.execute(() -> {
            final CommHeaderForEmail commHeaderForEmail = new CommHeaderForEmail();
            if(documentCommunicationSupports != null) {
                for (DocumentCommunicationSupport emailSupport : documentCommunicationSupports) {
                    emailSupport.inferEmailHeaderFor(document, commHeaderForEmail);;
                }
            }
            return commHeaderForEmail;
        }, Document_email.class, "determineEmailHeader", document);
    }


    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    RepositoryService repositoryService;

    @Inject
    List<DocumentCommunicationSupport> documentCommunicationSupports;

    @Inject
    DocumentTemplateRepository documentTemplateRepository;

    @Inject
    ClockService clockService;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    MeService meService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    EmailService emailService;

}

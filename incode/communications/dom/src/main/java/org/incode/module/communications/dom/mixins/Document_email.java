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
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.isisaddons.module.security.app.user.MeService;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.comms.CommChannelRoleType;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.spi.CommHeaderForEmail;
import org.incode.module.communications.dom.spi.DocumentCommunicationSupport;
import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;

import org.incode.module.communications.dom.impl.commchannel.EmailAddress;

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

    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_email> { }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public Communication $$(
            @ParameterLayout(named = "to:")
            final EmailAddress toChannel,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.REGEX_DESC)
            @ParameterLayout(named = "cc:")
            final String cc,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.REGEX_DESC)
            @ParameterLayout(named = "bcc:")
            final String bcc,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Covering note message", multiLine = EMAIL_COVERING_NOTE_MULTILINE)
            final String message) throws IOException {

        if(this.document.getState() == DocumentState.NOT_RENDERED) {
            // can't send the email yet, so schedule to try again in shortly.
            backgroundService.executeMixin(Document_email.class, document).$$(toChannel, cc, bcc, message);
            return null;
        }

        // create and attach cover note
        final DocumentTemplate coverNoteTemplate = determineEmailCoverNoteTemplate();
        final Document coverNoteDoc = coverNoteTemplate.createDocumentUsingBinding(this.document, message);

        coverNoteDoc.render(coverNoteTemplate, this.document, message);

        // create comm and correspondents
        final DateTime queuedAt = clockService.nowAsDateTime();

        final Communication communication = Communication.newEmail(document.getAtPath(), coverNoteDoc.getName(), queuedAt);
        serviceRegistry2.injectServicesInto(communication);

        communication.addCorrespondent(CommChannelRoleType.TO, toChannel);
        communication.addCorrespondentIfAny(CommChannelRoleType.CC, cc);
        communication.addCorrespondentIfAny(CommChannelRoleType.BCC, bcc);

        final ApplicationUser currentUser = meService.me();
        final String currentUserEmailAddress = currentUser.getEmailAddress();
        communication.addCorrespondentIfAny(CommChannelRoleType.PREPARED_BY, currentUserEmailAddress);

        repositoryService.persistAndFlush(communication);

        // attach the doc and the cover note to communication
        paperclipRepository.attach(document, DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT, communication);
        paperclipRepository.attach(coverNoteDoc, DocumentConstants.PAPERCLIP_ROLE_COVER, communication);

        // finally, schedule the email to be sent
        communication.scheduleSend();

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
        return "";
    }

    private DocumentTemplate determineEmailCoverNoteTemplate() {
        final DocumentType blankDocType = determineEmailCoverNoteDocumentType();
        if(blankDocType == null) {
            return null;
        }
        return documentTemplateRepository.findFirstByTypeAndApplicableToAtPath(blankDocType, document.getAtPath());
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

    @Inject
    BackgroundService2 backgroundService;

}

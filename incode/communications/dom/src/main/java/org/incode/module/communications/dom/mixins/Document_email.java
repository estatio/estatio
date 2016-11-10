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

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.comms.CommunicationRepository;
import org.incode.module.communications.dom.spi.CommHeaderForEmail;
import org.incode.module.communications.dom.spi.DocumentCommunicationSupport;
import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.services.DocumentCreatorService;

/**
 * Provides the ability to send an email.
 */
@Mixin
public class Document_email  {

    private final Document document;

    public Document_email(final Document document) {
        this.document = document;
    }

    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_email> { }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(
            cssClassFa = "at",
            contributed = Contributed.AS_ACTION
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
            final String bcc) throws IOException {

        if(this.document.getState() == DocumentState.NOT_RENDERED) {
            // this shouldn't happen, but want to fail-fast in case a future programmer calls this directly
            throw new IllegalArgumentException("Document is not yet rendered");
        }

        // create and attach cover note
        // nb: this functionality is basically the same as T_createAndAttachDocumentAbstract#$$
        final DocumentTemplate coverNoteTemplate = determineEmailCoverNoteTemplate();

        final Document coverNoteDoc =
                documentCreatorService.createDocumentAndAttachPaperclips(this.document, coverNoteTemplate);

        coverNoteDoc.render(coverNoteTemplate, this.document);

        // create comm and correspondents
        final String atPath = document.getAtPath();
        final String subject = coverNoteDoc.getName();
        final Communication communication =  communicationRepository.createEmail(subject, atPath, toChannel, cc, bcc);

        transactionService.flushTransaction();

        // attach the cover note to the communication
        paperclipRepository.attach(coverNoteDoc, DocumentConstants.PAPERCLIP_ROLE_COVER, communication);
        paperclipRepository.attach(document, DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT, communication);

        // also copy over as attachments to the comm anything else also attached to original document
        final List<Paperclip> documentPaperclips = paperclipRepository.findByDocument(this.document);
        for (Paperclip documentPaperclip : documentPaperclips) {
            final Object objAttachedToDocument = documentPaperclip.getAttachedTo();
            if (!(objAttachedToDocument instanceof Document)) {
                continue;
            }
            final Document docAttachedToDocument = (Document) objAttachedToDocument;
            if (docAttachedToDocument == document || docAttachedToDocument == coverNoteDoc) {
                continue;
            }
            paperclipRepository.attach(docAttachedToDocument, DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT, communication);
        }
        transactionService.flushTransaction();

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
        final EmailAddress toDefault = determineEmailHeader().getToDefault();
        if (toDefault != null) {
            return toDefault;
        }
        final Set<EmailAddress> choices = choices0$$();
        return choices.isEmpty() ? null : choices.iterator().next();
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
    TransactionService transactionService;

    @Inject
    List<DocumentCommunicationSupport> documentCommunicationSupports;

    @Inject
    DocumentTemplateRepository documentTemplateRepository;

    @Inject
    CommunicationRepository communicationRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    EmailService emailService;

    @Inject
    BackgroundService2 backgroundService;

    @Inject
    DocumentCreatorService documentCreatorService;

    @Inject
    FactoryService factoryService;


}

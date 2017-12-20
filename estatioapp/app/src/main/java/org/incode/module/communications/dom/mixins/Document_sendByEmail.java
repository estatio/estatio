package org.incode.module.communications.dom.mixins;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.isis.applib.ApplicationException;
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
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.services.DocumentCreatorService;

/**
 * Provides the ability to send an email.
 */
@Mixin(method = "act")
public class Document_sendByEmail {

    private final Document document;

    public Document_sendByEmail(final Document document) {
        this.document = document;
    }

    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_sendByEmail> { }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(
            cssClassFa = "at",
            contributed = Contributed.AS_ACTION
    )
    public Communication act(
            @ParameterLayout(named = "to:")
            final EmailAddress toChannel,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC)
            @ParameterLayout(named = "cc:")
            final String cc,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC)
            @ParameterLayout(named = "cc (2):")
            final String cc2,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC)
            @ParameterLayout(named = "cc (3):")
            final String cc3,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC)
            @ParameterLayout(named = "bcc:")
            final String bcc,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC)
            @ParameterLayout(named = "bcc (2):")
            final String bcc2
            ) throws IOException {



        if(this.document.getState() == DocumentState.NOT_RENDERED) {
            // this shouldn't happen, but want to fail-fast in case a future programmer calls this directly
            throw new IllegalArgumentException("Document is not yet rendered");
        }

        // create cover note
        //
        // nb: there is a presumption is that the cover note will not be automatically attached to any other objects,
        // ie its AttachmentAdvisor should be AttachToNone.
        final DocumentTemplate coverNoteTemplate = determineEmailCoverNoteTemplate();
        final Document coverNoteDoc =
                documentCreatorService.createDocumentAndAttachPaperclips(this.document, coverNoteTemplate);
        coverNoteDoc.render(coverNoteTemplate, this.document);

        // create comm and correspondents
        final String atPath = document.getAtPath();
        final String subject = stripFileExtensionIfAny(coverNoteDoc.getName());
        final Communication communication =  communicationRepository.createEmail(subject, atPath, toChannel, cc, cc2, cc3, bcc, bcc2);

        transactionService.flushTransaction();

        // manually attach the cover note to the comm
        paperclipRepository.attach(coverNoteDoc, DocumentConstants.PAPERCLIP_ROLE_COVER, communication);

        // also attach this "primary" document to the comm
        paperclipRepository.attach(this.document, DocumentConstants.PAPERCLIP_ROLE_PRIMARY, communication);

        // also copy over as attachments to the comm anything else also attached to primary document
        final List<Document> communicationAttachments = attachmentProvider.attachmentsFor(document);
        for (Document communicationAttachment : communicationAttachments) {
            paperclipRepository.attach(communicationAttachment, DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT, communication);

        }
        transactionService.flushTransaction();

        // finally, schedule the email to be sent
        communication.scheduleSend();

        return communication;
    }

    public String disableAct() {
        if (emailService == null || !emailService.isConfigured()) {
            return "Email service not configured";
        }
        if (document.getState() != DocumentState.RENDERED) {
            return "Document not yet rendered";
        }
        if(determineEmailCoverNoteTemplateElseNull() == null) {
            return "Email cover note type/template not provided";
        }
        if(determineEmailHeader().getDisabledReason() != null) {
            return determineEmailHeader().getDisabledReason();
        }
        if(choices0Act().isEmpty()) {
            return "Could not locate any email address(es) to sent to";
        }
        return null;
    }

    public EmailAddress default0Act() {
        final EmailAddress toDefault = determineEmailHeader().getToDefault();
        if (toDefault != null) {
            return toDefault;
        }
        final Set<EmailAddress> choices = choices0Act();
        return choices.isEmpty() ? null : choices.iterator().next();
    }

    public Set<EmailAddress> choices0Act() {
        return determineEmailHeader().getToChoices();
    }

    public String default1Act() {
        return determineEmailHeader().getCc();
    }

    public String default4Act() {
        return determineEmailHeader().getBcc();
    }


    private DocumentTemplate determineEmailCoverNoteTemplate() {
        DocumentTemplate template = determineEmailCoverNoteTemplateElseNull();
        if(template == null) {
            throw new ApplicationException("Could not locate an email cover note template.");
        }
        return template;
    }

    private DocumentTemplate determineEmailCoverNoteTemplateElseNull() {
        final DocumentType coverNoteDocumentType = determineEmailCoverNoteDocumentType();
        if(coverNoteDocumentType == null) {
            return null;
        }
        return documentTemplateRepository.findFirstByTypeAndApplicableToAtPath(coverNoteDocumentType, document.getAtPath());
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
        }, Document_sendByEmail.class, "determineEmailCoverNoteDocumentType", document);
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
        }, Document_sendByEmail.class, "determineEmailHeader", document);
    }

    // bit of a hack...
    private static String stripFileExtensionIfAny(final String name) {
        final int suffix = name.lastIndexOf(".html");
        return suffix == -1 ? name : name.substring(0, suffix);
    }


    @Inject
    Document_communicationAttachments.Provider attachmentProvider;

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

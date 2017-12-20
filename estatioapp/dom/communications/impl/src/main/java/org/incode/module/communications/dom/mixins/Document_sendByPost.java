package org.incode.module.communications.dom.mixins;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.background.BackgroundService2;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.incode.module.communications.dom.impl.commchannel.PostalAddress;
import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.comms.CommunicationRepository;
import org.incode.module.communications.dom.spi.CommHeaderForPost;
import org.incode.module.communications.dom.spi.DocumentCommunicationSupport;
import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentState;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

/**
 * Provides the ability to send as a postal communication.
 */
@Mixin(method = "act")
public class Document_sendByPost {

    private final Document document;

    public Document_sendByPost(final Document document) {
        this.document = document;
    }

    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_sendByPost> { }

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(
            cssClassFa = "envelope-o",
            contributed = Contributed.AS_ACTION
    )
    public Communication act(
            @ParameterLayout(named = "to:")
            final PostalAddress toChannel) throws IOException {

        if(this.document.getState() == DocumentState.NOT_RENDERED) {
            // this shouldn't happen, but want to fail-fast in case a future programmer calls this directly
            throw new IllegalArgumentException("Document is not yet rendered");
        }

        // create comm and correspondents
        final String atPath = document.getAtPath();
        final String subject = document.getName();
        final Communication communication = communicationRepository.createPostal(subject, atPath, toChannel);

        transactionService.flushTransaction();

        // attach this "primary" document to the comm
        paperclipRepository.attach(this.document, DocumentConstants.PAPERCLIP_ROLE_PRIMARY, communication);

        // also copy over as attachments to the comm anything else also attached to original document
        final List<Document> communicationAttachments = attachmentProvider.attachmentsFor(document);
        for (Document communicationAttachment : communicationAttachments) {
            paperclipRepository.attach(communicationAttachment, DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT, communication);
        }
        transactionService.flushTransaction();

        return communication;
    }

    public String disableAct() {
        if (document.getState() != DocumentState.RENDERED) {
            return "Document not yet rendered";
        }
        if(choices0Act().isEmpty()) {
            return "Could not locate any postal address to sent to";
        }
        return null;
    }

    public PostalAddress default0Act() {
        return determinePostHeader().getToDefault();
    }

    public Set<PostalAddress> choices0Act() {
        return determinePostHeader().getToChoices();
    }

    private CommHeaderForPost determinePostHeader() {
        return queryResultsCache.execute(() -> {
            final CommHeaderForPost commHeaderForPost = new CommHeaderForPost();
            if(documentCommunicationSupports != null) {
                for (DocumentCommunicationSupport commSupport : documentCommunicationSupports) {
                    commSupport.inferPrintHeaderFor(document, commHeaderForPost);;
                }
            }
            return commHeaderForPost;
        }, Document_sendByPost.class, "determinePrintHeader", document);
    }



    @Inject
    Document_communicationAttachments.Provider attachmentProvider;

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    List<DocumentCommunicationSupport> documentCommunicationSupports;

    @Inject
    TransactionService transactionService;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    CommunicationRepository communicationRepository;

    @Inject
    BackgroundService2 backgroundService;

}

package org.incode.module.communications.dom.impl.covernotes;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.FluentIterable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

@Mixin(method="prop")
public class Document_coverNoteFor {

    private final Document coverNoteCandidate;
    public Document_coverNoteFor(final Document coverNoteCandidate) {
        this.coverNoteCandidate = coverNoteCandidate;
    }

    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document> { }

    @Action(semantics = SemanticsOf.SAFE, domainEvent = ActionDomainEvent.class)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    public Communication prop() {
        return evaluator.coverNoteFor(coverNoteCandidate);
    }

    // hide this property if this document is not in fact a cover note for any communication
    public boolean hideProp() {
        return prop() == null;
    }


    @Inject
    Evaluator evaluator;

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Evaluator {

        /**
         * @return the communication, if any, that this document is a cover note for, else null
         */
        @Programmatic
        public Communication coverNoteFor(Document coverNoteCandidate) {
            final List<Paperclip> paperclips = paperclipRepository.findByDocument(coverNoteCandidate);
            if(paperclips.size() != 1) {
                return null;
            }
            final List<Communication> supportedDocumentsIfAny =
                    FluentIterable.from(paperclips)
                            .transform(x -> x.getAttachedTo())
                            .filter(Communication.class::isInstance)
                            .transform(Communication.class::cast)
                            .toList();
            return supportedDocumentsIfAny.size() == 1
                    ? supportedDocumentsIfAny.get(0)
                    : null;
        }

        @Inject
        PaperclipRepository paperclipRepository;
    }

}

package org.incode.module.communications.dom.mixins;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.spi.SupportingDocumentsEvaluator;

@Mixin(method = "coll")
public class Document_communicationAttachments {

    private final Document document;

    public Document_communicationAttachments(final Document document) {
        this.document = document;
    }

    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_communicationAttachments> { }

    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    public List<Document> coll() {
        return provider.attachmentsFor(document);
    }

    public boolean hideColl() {
        for (SupportingDocumentsEvaluator supportingDocumentsEvaluator : supportingDocumentsEvaluators) {
            final SupportingDocumentsEvaluator.Evaluation evaluation =
                    supportingDocumentsEvaluator.evaluate(document);
            if(evaluation == SupportingDocumentsEvaluator.Evaluation.SUPPORTING) {
                return true;
            }
        }
        return false;
    }

    @Inject
    List<SupportingDocumentsEvaluator> supportingDocumentsEvaluators;

    @Inject
    Provider provider;

    /**
     * Factored out so can be injected elsewhere also.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class Provider {

        @Programmatic
        public List<Document> attachmentsFor(final Document document) {
            final List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(document);

            return Lists.newArrayList(
                    FluentIterable.from(paperclips)
                        .transform(Paperclip::getDocument)
                        .filter(Document.class::isInstance)
                        .transform(Document.class::cast)
                        .toList());
        }

        @Inject
        PaperclipRepository paperclipRepository;

    }


}

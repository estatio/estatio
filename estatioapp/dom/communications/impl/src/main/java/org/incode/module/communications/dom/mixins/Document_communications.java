package org.incode.module.communications.dom.mixins;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;

import org.incode.module.communications.dom.impl.comms.Communication;
import org.incode.module.communications.dom.impl.covernotes.Document_coverNoteFor;
import org.incode.module.document.dom.DocumentModule;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.spi.SupportingDocumentsEvaluator;

@Mixin(method = "coll")
public class Document_communications {

    private final Document document;

    public Document_communications(final Document document) {
        this.document = document;
    }

    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Document_communications> { }

    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    public List<Communication> coll() {
        final List<Communication> communications = Lists.newArrayList(
                paperclipRepository.findByDocument(document).stream()
                                    .map(paperclip -> paperclip.getAttachedTo())
                                    .filter(attachedTo -> attachedTo instanceof Communication)
                                    .map(Communication.class::cast)
                                    .collect(Collectors.toList()));
        Collections.reverse(communications);
        return communications;
    }

    public boolean hideColl() {
        // hide for supporting documents
        for (SupportingDocumentsEvaluator supportingDocumentsEvaluator : supportingDocumentsEvaluators) {
            final SupportingDocumentsEvaluator.Evaluation evaluation =
                    supportingDocumentsEvaluator.evaluate(document);
            if(evaluation == SupportingDocumentsEvaluator.Evaluation.SUPPORTING) {
                return true;
            }
        }


        return false;
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TableColumnOrderServiceForDocumentCommunications implements TableColumnOrderService {

        @Override
        public List<String> orderParented(
                final Object parent,
                final String collectionId,
                final Class<?> collectionType,
                final List<String> propertyIds) {

            if(parent instanceof Document && Objects.equals(collectionId, "communications")) {
                propertyIds.remove("primaryDocument");
                return propertyIds;
            }
            return null;
        }

        @Override public List<String> orderStandalone(final Class<?> aClass, final List<String> list) {
            return null;
        }
    }

    @DomainService(
            nature = NatureOfService.DOMAIN,
            menuOrder = "110"
    )
    public static class SupportingDocumentsEvaluatorForCoverNotes implements SupportingDocumentsEvaluator {

        @Override
        public List<Document> supportedBy(final Document candidateSupportingDocument) {
            return null;
        }

        @Override
        public Evaluation evaluate(final Document candidateSupportingDocument) {
            final Communication communication = coverNoteEvaluator.coverNoteFor(candidateSupportingDocument);
            return communication != null ? Evaluation.SUPPORTING : Evaluation.UNKNOWN;
        }


        @Inject
        Document_coverNoteFor.Evaluator coverNoteEvaluator;
    }


    @Inject
    List<SupportingDocumentsEvaluator> supportingDocumentsEvaluators;


    @Inject
    PaperclipRepository paperclipRepository;

}

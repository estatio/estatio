package org.estatio.capex.dom.documents.categorisation.document;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.triggers.DomainObject_triggerBaseAbstract;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.invoice.DocumentTypeData;

public abstract class DocOrIncomingDoc_resetAbstract extends DomainObject_triggerBaseAbstract<
        Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState
        > {

    @Programmatic
    public abstract Document getDomainObject();

    public DocOrIncomingDoc_resetAbstract() {
        super(IncomingDocumentCategorisationStateTransitionType.RESET);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(cssClassFa = "folder-open-o")
    public Document act(
            @Nullable final String comment) {

        Document document = getDomainObject();
        document.setType(DocumentTypeData.INCOMING.findUsing(documentTypeRepository));

        // delete paperclip
        List<Paperclip> paperclipsToDelete = paperclipRepository.findByDocument(document);
        for (Paperclip paperclip : paperclipsToDelete){
            paperclipRepository.delete(paperclip);
        }

        trigger(comment);

        return document;
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        Document document = getDomainObject();
        final List<Paperclip> paperclips = paperclipRepository.findByDocument(document);
        for (Paperclip paperclip : paperclips){
            if (!FixedAsset.class.isAssignableFrom(paperclip.getAttachedTo().getClass())){
                return "Document has been already been categorized";
            }
        }
        return null;
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

}

package org.estatio.capex.dom.documents;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;

import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

public abstract class HasDocument_categoriseAbstract extends DocumentOrHasDocument_categoriseAsAbstract {

    protected final HasDocument hasDocument;

    public HasDocument_categoriseAbstract(
            final HasDocument hasDocument,
            final DocumentTypeData documentTypeData) {
        super(documentTypeData);
        this.hasDocument = hasDocument;
    }

    @Override
    public Document getDomainObject() {
        return hasDocument.getDocument();
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "folder-open-o")
    public HasDocumentAbstract act(
            @Nullable final Property property,
            @Nullable final String comment,
            final boolean goToNext) {
        final HasDocumentAbstract viewModel = categoriseAndAttachPaperclip(property);

        // to triggerStateTransition state transition
        triggerStateTransition(comment);

        if (goToNext){
            final Document nextDocument = nextDocument();
            if (nextDocument != null) {
                return this.viewModelFactory.createFor(nextDocument);
            }
            // fall through to returning the view model for this document
            messageService.informUser("No more documents to categorise");
        }

        return viewModel;
    }

    @Override
    public Property default0Act() {
        return super.default0Act();
    }

    public boolean default2Act(){
        return true;
    }

    @Override
    public boolean hideAct() {
        return super.hideAct();
    }

    private Document nextDocument() {
        List<Document> incomingDocuments = documentRepository.findWithNoPaperclips();
        return incomingDocuments.size() > 0 ? incomingDocuments.get(0) : null;
    }

    @Inject
    MessageService messageService;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    HasDocumentAbstract.Factory viewModelFactory;

}

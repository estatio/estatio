package org.estatio.capex.dom.documents;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.documents.incoming.IncomingOrderAndInvoiceViewModel;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

public abstract class HasDocumentAbstract_categoriseAbstract {

    protected final HasDocument hasDocument;
    private final DocumentTypeData documentTypeData;
    private final Class<? extends HasDocumentAbstract> viewModelClass;

    public HasDocumentAbstract_categoriseAbstract(
            final HasDocument hasDocument,
            final DocumentTypeData documentTypeData,
            final Class<? extends HasDocumentAbstract> viewModelClass) {
        this.hasDocument = hasDocument;
        this.documentTypeData = documentTypeData;
        this.viewModelClass = viewModelClass;
    }

    public static class DomainEvent
            extends EstatioCapexDomModule.ActionDomainEvent<HasDocumentAbstract_categoriseAbstract> {
        @Override
        public void setMixedIn(final Object mixedIn) {
            super.setMixedIn(mixedIn);
        }
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = DomainEvent.class
    )
    @ActionLayout(cssClassFa = "folder-open-o")
    public HasDocument act(
            final Property property,
            final boolean goToNext) {
        Document document = hasDocument.getDocument();
        if(documentTypeData != null) {
            document.setType(documentTypeData.findUsing(documentTypeRepository));
        }

        HasDocument hasDocument = doCreate(document, property);
        if (goToNext){
            return nextDocument()!=null ? factory.map(nextDocument()) : null;
        }

        return serviceRegistry2.injectServicesInto(hasDocument);
    }

    public boolean default1Act(){
        return true;
    }

    protected HasDocument doCreate(final Document document, final Property property) {
        HasDocumentAbstract viewModel = factoryService.instantiate(viewModelClass);
        viewModel.setDocument(document);
        try {
            ((IncomingOrderAndInvoiceViewModel) viewModel).setFixedAsset(property);
            paperclipRepository.attach(document,null, property);
        } catch (Exception e){
            // do nothing
        }
        return viewModel;
    }

    private Document nextDocument() {
        List<Document> incomingDocuments = documentRepository.findWithNoPaperclips();
        return incomingDocuments.size() > 0 ? incomingDocuments.get(0) : null;
    }

    public boolean hideAct() {
        return viewModelClass.isAssignableFrom(hasDocument.getClass()) || disableAct() != null;
    }

    // TODO: this action probably can be removed after user feedback (and hideAct() adapted)
    public String disableAct() {
        final Document document = hasDocument.getDocument();
        final List<Paperclip> paperclips = paperclipRepository.findByDocument(document);
        return paperclips.isEmpty() ? null : "Document has been already been categorized";
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    FactoryService factoryService;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    HasDocumentAbstract.Factory factory;

}

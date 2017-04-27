package org.estatio.capex.dom.documents;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.incoming.IncomingOrderAndInvoiceViewModel;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

public abstract class HasDocumentAbstract_categorizeAbstract {

    protected final HasDocument hasDocument;
    private final DocumentTypeData documentTypeData;
    private final Class<? extends HasDocumentAbstract> viewModelClass;

    public HasDocumentAbstract_categorizeAbstract(final HasDocument hasDocument, final DocumentTypeData documentTypeData, final Class<? extends HasDocumentAbstract> viewModelClass) {
        this.hasDocument = hasDocument;
        this.documentTypeData = documentTypeData;
        this.viewModelClass = viewModelClass;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "folder-open-o")
    public HasDocument act(final Property property) {
        Document document = hasDocument.getDocument();
        if(documentTypeData != null) {
            document.setType(documentTypeData.findUsing(documentTypeRepository));
        }

        HasDocument orderViewModel = doCreate(document, property);

        return serviceRegistry2.injectServicesInto(orderViewModel);
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

    public boolean hideAct() {
        return viewModelClass.isAssignableFrom(hasDocument.getClass());
    }

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

}

package org.estatio.capex.dom.documents;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.incoming.IncomingDocumentViewModel;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class HasDocumentAbstract_resetCategorization {

    protected final HasDocument hasDocument;

    public HasDocumentAbstract_resetCategorization(final HasDocument hasDocument) {
        this.hasDocument = hasDocument;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "folder-open-o")
    public HasDocument act() {
        Document document = hasDocument.getDocument();
        document.setType(DocumentTypeData.INCOMING.findUsing(documentTypeRepository));
        HasDocument orderViewModel = doCreate(document);

        return serviceRegistry2.injectServicesInto(orderViewModel);
    }

    protected HasDocument doCreate(final Document document) {
        HasDocumentAbstract viewModel = factoryService.instantiate(IncomingDocumentViewModel.class);
        viewModel.setDocument(document);
        return viewModel;
    }

    public boolean hideAct() {
        return IncomingDocumentViewModel.class.isAssignableFrom(hasDocument.getClass());
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

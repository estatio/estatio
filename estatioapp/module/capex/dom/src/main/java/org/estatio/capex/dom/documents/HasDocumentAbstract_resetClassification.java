package org.estatio.capex.dom.documents;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.capex.dom.documents.incoming.IncomingDocumentViewModel;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class HasDocumentAbstract_resetClassification {


    protected final HasDocument hasDocument;

    public HasDocumentAbstract_resetClassification(final HasDocument hasDocument) {
        this.hasDocument = hasDocument;
    }


    public static class DomainEvent extends ActionDomainEvent<HasDocumentAbstract_resetClassification> {}

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = DomainEvent.class
    )
    @ActionLayout(cssClassFa = "folder-open-o")
    public HasDocument act() {
        Document document = hasDocument.getDocument();
        document.setType(DocumentTypeData.INCOMING.findUsing(documentTypeRepository));

        // delete paperclip
        List<Paperclip> paperclipsToDelete = paperclipRepository.findByDocument(document);
        for (Paperclip paperclip : paperclipsToDelete){
            paperclipRepository.delete(paperclip);
        }

        HasDocument incomingDocumentViewModel = doCreate(document);

        return serviceRegistry2.injectServicesInto(incomingDocumentViewModel);
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
        for (Paperclip paperclip : paperclips){
            if (!FixedAsset.class.isAssignableFrom(paperclip.getAttachedTo().getClass())){
                return "Document has been already been categorized";
            }
        }
        return null;
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

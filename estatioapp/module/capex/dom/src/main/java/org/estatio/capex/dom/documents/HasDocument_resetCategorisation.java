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

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.documents.incoming.IncomingDocumentViewModel;
import org.estatio.capex.dom.triggers.DomainObject_triggerBaseAbstract;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class HasDocument_resetCategorisation extends DomainObject_triggerBaseAbstract<
        Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState
        > {

    protected final HasDocument hasDocument;

    public HasDocument_resetCategorisation(final HasDocument hasDocument) {
        super(IncomingDocumentCategorisationStateTransitionType.RESET);
        this.hasDocument = hasDocument;
    }

    @Override
    public Document getDomainObject() {
        return hasDocument.getDocument();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(cssClassFa = "folder-open-o")
    public HasDocument act(final String comment) {
        super.act(comment);

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
        if(super.hideAct()) {
            return true;
        }
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

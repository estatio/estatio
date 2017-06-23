package org.estatio.capex.dom.documents.categorisation.invoice;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class IncomingInvoice_switchView {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_switchView(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(
            contributed = Contributed.AS_ACTION,
            cssClassFa = "fa-exchange" // not sure why this isn't being picked up from isis-non-changing.properties
    )
    @MemberOrder(sequence = "1")
    public IncomingDocAsInvoiceViewModel act() {
        Document document = findIncomingInvoiceDocumentAttachedTo(incomingInvoice);
        final IncomingDocAsInvoiceViewModel viewModel = new IncomingDocAsInvoiceViewModel(incomingInvoice, document);
        serviceRegistry2.injectServicesInto(viewModel);
        viewModel.init();
        return viewModel;
    }

    private Document findIncomingInvoiceDocumentAttachedTo(final IncomingInvoice incomingInvoice) {
        return queryResultsCache.execute(
                () -> doFindIncomingInvoiceDocumentAttachedTo(incomingInvoice),
                IncomingInvoice_switchView.class,
                "findIncomingInvoiceDocumentAttachedTo", incomingInvoice);
    }

    private Document doFindIncomingInvoiceDocumentAttachedTo(final IncomingInvoice incomingInvoice) {
        List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(incomingInvoice);
        for (Paperclip paperclip : paperclips) {
            DocumentAbstract documentAbstract = paperclip.getDocument();
            if(documentAbstract instanceof Document) {
                final Document document = (Document) documentAbstract;
                if(DocumentTypeData.docTypeDataFor(document) == DocumentTypeData.INCOMING_INVOICE) {
                    return document;
                }
            }
        }
        return null;
    }

    @Inject
    QueryResultsCache queryResultsCache;
    @Inject
    PaperclipRepository paperclipRepository;
    @Inject
    ServiceRegistry2 serviceRegistry2;
}

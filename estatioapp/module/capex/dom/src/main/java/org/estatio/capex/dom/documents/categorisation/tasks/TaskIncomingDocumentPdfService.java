package org.estatio.capex.dom.documents.categorisation.tasks;

import java.util.Objects;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.value.Blob;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoice_pdf;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.task.Task;

@DomainService(nature = NatureOfService.DOMAIN)
public class TaskIncomingDocumentPdfService {

    @Programmatic
    public Blob lookupPdfFor(final Task task) {
        return queryResultsCache.execute(() -> doProp(task), TaskIncomingDocumentPdfService.class, "lookupPdfFor", task);
    }

    private Blob doProp(final Task task) {
        StateTransition<?,?,?,?> stateTransition = stateTransitionService.findFor(task);
        if(stateTransition == null) {
            return null;
        }

        if(stateTransition instanceof IncomingDocumentCategorisationStateTransition) {
            IncomingDocumentCategorisationStateTransition idcst = (IncomingDocumentCategorisationStateTransition) stateTransition;
            Document document = idcst.getDocument();

            if(document == null) {
                return null;
            }
            if (!Objects.equals(document.getMimeType(), "application/pdf")) {
                return null;
            }
            return document.getBlob();
        }

        if(stateTransition instanceof IncomingInvoiceApprovalStateTransition) {
            IncomingInvoiceApprovalStateTransition iiast = (IncomingInvoiceApprovalStateTransition) stateTransition;
            IncomingInvoice invoice = iiast.getInvoice();
            IncomingInvoice_pdf mixin = factoryService.mixin(IncomingInvoice_pdf.class, invoice);
            return mixin.hideProp() ? null : mixin.prop();
        }

        return null;
    }

    @Inject FactoryService factoryService;

    @Inject StateTransitionService stateTransitionService;

    @Inject
    QueryResultsCache queryResultsCache;

}

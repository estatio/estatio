package org.estatio.module.capex.dom.task.pdf;

import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.value.Blob;

import org.incode.module.base.dom.MimeTypes;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.approval.OrderApprovalStateTransition;
import org.estatio.module.capex.dom.state.StateTransition;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.financial.dom.BankAccount;

@DomainService(nature = NatureOfService.DOMAIN)
public class TaskIncomingDocumentPdfService {

    @Programmatic
    public Blob lookupPdfFor(final Task task) {
        return queryResultsCache.execute(
                () -> doLookupPdfFor(task),
                TaskIncomingDocumentPdfService.class,
                "lookupPdfFor", task);
    }

    private Blob doLookupPdfFor(final Task task) {
        StateTransition<?,?,?,?> stateTransition = stateTransitionService.findFor(task);
        if(stateTransition == null) {
            return null;
        }

        if(stateTransition instanceof IncomingDocumentCategorisationStateTransition) {
            IncomingDocumentCategorisationStateTransition idcst =
                    (IncomingDocumentCategorisationStateTransition) stateTransition;
            final Document document = idcst.getDocument();
            if(document == null) {
                return null;
            }
            if (!Objects.equals(document.getMimeType(), MimeTypes.APPLICATION_PDF.asStr())) {
                return null;
            }
            return document.getBlob();
        }

        if(stateTransition instanceof OrderApprovalStateTransition) {
            final OrderApprovalStateTransition oast =
                    (OrderApprovalStateTransition) stateTransition;
            final Order order = oast.getOrdr();
            final Optional<Document> documentIfAny = lookupAttachedPdfService.lookupOrderPdfFrom(order);
            return documentIfAny.map(DocumentAbstract::getBlob).orElse(null);
        }

        if(stateTransition instanceof IncomingInvoiceApprovalStateTransition) {
            final IncomingInvoiceApprovalStateTransition iiast =
                    (IncomingInvoiceApprovalStateTransition) stateTransition;
            final IncomingInvoice invoice = iiast.getInvoice();
            final Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(invoice);
            return documentIfAny.map(DocumentAbstract::getBlob).orElse(null);
        }

        if(stateTransition instanceof BankAccountVerificationStateTransition) {
            final BankAccountVerificationStateTransition bavst =
                    (BankAccountVerificationStateTransition) stateTransition;
            final BankAccount bankAccount = bavst.getBankAccount();
            final Optional<Document> documentIfAny = lookupAttachedPdfService.lookupIbanProofPdfFrom(bankAccount);
            return documentIfAny.map(DocumentAbstract::getBlob).orElse(null);
        }

        return null;
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject
    QueryResultsCache queryResultsCache;

}

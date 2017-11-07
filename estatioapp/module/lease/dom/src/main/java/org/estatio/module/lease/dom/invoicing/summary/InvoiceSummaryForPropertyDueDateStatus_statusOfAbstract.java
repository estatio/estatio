package org.estatio.module.lease.dom.invoicing.summary;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;

import org.estatio.dom.invoice.InvoiceStatus;

public abstract class InvoiceSummaryForPropertyDueDateStatus_statusOfAbstract extends InvoiceSummaryForPropertyDueDateStatus_filterAbstract {

    private final InvoiceStatus invoiceStatus;

    public InvoiceSummaryForPropertyDueDateStatus_statusOfAbstract(
            final InvoiceSummaryForPropertyDueDateStatus invoiceSummary,
            final InvoiceStatus invoiceStatus) {
        super(invoiceSummary);
        this.invoiceStatus = invoiceStatus;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act() {
        final List<InvoiceSummaryForPropertyDueDateStatus> summary = findSummaries();
        return summaryFrom(summary);
    }

    protected List<InvoiceSummaryForPropertyDueDateStatus> findSummaries() {
        return findSummariesFor(invoiceStatus, invoiceSummary.getDueDate());
    }

    public String disableAct() {
        return findSummaries().isEmpty() ? "No " + invoiceStatus + " invoices": null;
    }

    @Inject
    MessageService messageService;
}

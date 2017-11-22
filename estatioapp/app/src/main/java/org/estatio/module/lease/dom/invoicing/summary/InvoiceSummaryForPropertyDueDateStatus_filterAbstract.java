package org.estatio.module.lease.dom.invoicing.summary;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.services.message.MessageService;

import org.estatio.module.invoice.dom.InvoiceStatus;

public abstract class InvoiceSummaryForPropertyDueDateStatus_filterAbstract {
    final InvoiceSummaryForPropertyDueDateStatus invoiceSummary;

    public InvoiceSummaryForPropertyDueDateStatus_filterAbstract(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        this.invoiceSummary = invoiceSummary;
    }

    protected List<InvoiceSummaryForPropertyDueDateStatus> findSummariesFor(
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate) {
        return repository.findByAtPathAndSellerReferenceAndStatusAndDueDate(
                invoiceSummary.getAtPath(),
                invoiceSummary.getSeller().getReference(),
                invoiceStatus,
                dueDate);
    }


    protected List<InvoiceSummaryForPropertyDueDateStatus> findSummariesFor(final InvoiceStatus invoiceStatus) {
        return repository.findByAtPathAndSellerReferenceAndStatus(
                invoiceSummary.getAtPath(),
                invoiceSummary.getSeller().getReference(),
                invoiceStatus);
    }

    protected Object summaryFrom(final List<InvoiceSummaryForPropertyDueDateStatus> summariesForDate) {
        // the following is defensive coding... in fact there should always be a maximum of
        // just one invoice for atPath, partyRef, invoiceStatus and dueDate
        final int size = summariesForDate.size();
        switch (size) {
        case 0:
            messageService.warnUser("No invoice summaries found for status/dueDate");
            return invoiceSummary;
        case 1:
            // expected
            return summariesForDate.get(0);
        default:
            messageService.warnUser("More than one invoice summary found for status/dueDate...");
            return Lists.newArrayList(summariesForDate);
        }
    }


    @Inject
    MessageService messageService;
    @Inject
    InvoiceSummaryForPropertyDueDateStatusRepository repository;
}

package org.estatio.module.lease.dom.invoicing.summary;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;

import org.estatio.module.invoice.dom.InvoiceStatus;

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public class InvoiceSummaryForPropertyDueDateStatus_filter extends InvoiceSummaryForPropertyDueDateStatus_filterAbstract {

    public InvoiceSummaryForPropertyDueDateStatus_filter(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        super(invoiceSummary);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act(
            final InvoiceStatus invoiceStatus,
            final LocalDate dueDate
    ) {
        final List<InvoiceSummaryForPropertyDueDateStatus> summariesForDate = findSummariesFor(invoiceStatus, dueDate);
        return summaryFrom(summariesForDate);
    }

    public InvoiceStatus default0Act() {
        return invoiceSummary.getStatus();
    }

    public List<LocalDate> choices1Act(final InvoiceStatus invoiceStatus) {
        final List<InvoiceSummaryForPropertyDueDateStatus> summaries = findSummariesFor(invoiceStatus);
        return Lists.newArrayList(
                FluentIterable.from(summaries)
                              .transform(x -> x.getDueDate())
                              .toList()
                              .reverse()
        );
    }

    @Inject
    MessageService messageService;
}

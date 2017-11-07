package org.estatio.dom.lease.invoicing.viewmodel;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.base.FragmentRenderService;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceAttributeName;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.dom.lease.invoicing.ssrs.InvoiceAttributesVM;

/**
 * TODO: inline this mixin
 */
@Mixin(method = "act")
public class InvoiceSummaryForPropertyDueDateStatus_resetDescriptions {

    private final InvoiceSummaryForPropertyDueDateStatus summary;

    public InvoiceSummaryForPropertyDueDateStatus_resetDescriptions(final InvoiceSummaryForPropertyDueDateStatus summary) {
        this.summary = summary;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public InvoiceSummaryForPropertyDueDateStatus act() {

        final List<InvoiceForLease> invoices = summary.getInvoices();
        for (final InvoiceForLease ninvoice : invoices) {
            updateAttribute(ninvoice, InvoiceAttributeName.INVOICE_DESCRIPTION);
            updateAttribute(ninvoice, InvoiceAttributeName.PRELIMINARY_LETTER_DESCRIPTION);
        }
        return summary;
    }

    private void updateAttribute(final InvoiceForLease ninvoice, final InvoiceAttributeName invoiceAttributeName) {
        ninvoice.updateAttribute(
                invoiceAttributeName,
                fragmentRenderService.render(new InvoiceAttributesVM(ninvoice), invoiceAttributeName.getFragmentName()),
                Invoice.InvoiceAttributeAction.RESET);
    }

    @Inject FragmentRenderService fragmentRenderService;

}

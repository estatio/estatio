package org.estatio.module.lease.dom.invoicing.summary;

import javax.inject.Inject;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;

import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.lease.dom.LeaseTermForTax;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;

@Mixin()
public class InvoiceSummaryForPropertyDueDateStatus_updateTaxTerms  {

    private InvoiceSummaryForPropertyDueDateStatus invoiceSummary;

    public InvoiceSummaryForPropertyDueDateStatus_updateTaxTerms(final InvoiceSummaryForPropertyDueDateStatus invoiceSummary) {
        this.invoiceSummary = invoiceSummary;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE, associateWith = "invoices", associateWithSequence = "1")
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public InvoiceSummaryForPropertyDueDateStatus updateTaxTerms(
            final LocalDate paymentDate
    ) {
        invoiceSummary.getInvoices().forEach(i->{
            Lists.newArrayList(i.getItems()).forEach(ii->{
                InvoiceItemForLease item = (InvoiceItemForLease) ii;
                if (item.getSource()!=null && item.getSource().getClass().isAssignableFrom(LeaseTermForTax.class)){
                    LeaseTermForTax taxTermSource = (LeaseTermForTax) item.getSource();
                    if (taxTermSource.getPaymentDate()==null){
                        taxTermSource.setPaymentDate(paymentDate);
                    } else {
                        String warning = String.format("Payment date was already set on %s for tax on lease %s", taxTermSource.getPaymentDate().toString("dd-MM-yyyy"), taxTermSource.getLeaseItem().getLease().getReference());
                        messageService.warnUser(warning);
                    }
                }
            });
        });
        return invoiceSummary;
    }

    public String disableUpdateTaxTerms(){
        for (InvoiceForLease invoice : invoiceSummary.getInvoices()){
            for (InvoiceItem item : invoice.getItems()){
                InvoiceItemForLease castedItem = (InvoiceItemForLease) item;
                if (castedItem.getSource()!=null && castedItem.getSource().getClass().isAssignableFrom(LeaseTermForTax.class)){
                    return null;
                }
            }
        }
        return "No tax is being invoiced";
    }

    @Inject
    MessageService messageService;
}

package org.estatio.module.lease.dom.invoicing.attr;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeName;
import org.estatio.module.invoice.dom.attr.Invoice_resetAttributeAbstract;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.ssrs.InvoiceAttributesVM;

@Mixin(method = "act")
public class InvoiceForLease_resetInvoiceDescription
                extends Invoice_resetAttributeAbstract {

    public InvoiceForLease_resetInvoiceDescription(final InvoiceForLease invoice) {
        super(invoice, InvoiceAttributeName.INVOICE_DESCRIPTION);
    }

    @Override
    protected Object viewModelFor(final Invoice invoice) {
        return new InvoiceAttributesVM((InvoiceForLease) invoice);
    }
}

package org.estatio.module.lease.dom.invoicing.attr;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.invoice.dom.attr.InvoiceAttributeName;
import org.estatio.module.invoice.dom.attr.Invoice_overrideAttributeAbstract;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin(method = "act")
public class InvoiceForLease_overrideInvoiceDescription
                extends Invoice_overrideAttributeAbstract {

    public InvoiceForLease_overrideInvoiceDescription(final InvoiceForLease invoice) {
        super(invoice, InvoiceAttributeName.INVOICE_DESCRIPTION);
    }
}

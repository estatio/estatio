package org.estatio.module.lease.dom.invoicing;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.invoice.dom.DocumentTypeData;
import org.estatio.module.lease.dom.invoicing.summary.comms.DocAndCommForInvoiceDoc;

@Mixin(method= "act")
public class InvoiceForLease_downloadInvoiceDocs extends InvoiceForLease_downloadAbstract {

    public InvoiceForLease_downloadInvoiceDocs(final InvoiceForLease invoice){
        super(invoice, i->new DocAndCommForInvoiceDoc(i), DocumentTypeData.INVOICE);
    }

}

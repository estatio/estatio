package org.estatio.module.lease.dom.invoicing.attr;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.invoice.dom.attr.InvoiceAttributeName;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin(method="prop")
public class InvoiceForLease_preliminaryLetterComment
        extends InvoiceForLease_attributeValueAbstract {

    public InvoiceForLease_preliminaryLetterComment(final InvoiceForLease invoiceForLease) {
        super(invoiceForLease, InvoiceAttributeName.PRELIMINARY_LETTER_COMMENT);
    }
}

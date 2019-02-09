package org.estatio.module.lease.dom.invoicing.attr.prop;

import org.apache.isis.applib.annotation.Mixin;

import org.estatio.module.invoice.dom.attr.InvoiceAttributeName;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

//region > _preliminaryLetterDescription (derived property)
@Mixin(method="prop")
public class InvoiceForLease_preliminaryLetterDescription
        extends InvoiceForLease_attributeValueAbstract {

    public InvoiceForLease_preliminaryLetterDescription(final InvoiceForLease invoiceForLease) {
        super(invoiceForLease, InvoiceAttributeName.PRELIMINARY_LETTER_DESCRIPTION);
    }
}

package org.estatio.module.lease.dom.invoicing.attr;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeName;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin(method="prop")
public class InvoiceForLease_comment {
    private final InvoiceForLease invoiceForLease;
    public InvoiceForLease_comment(final InvoiceForLease invoiceForLease) {
        this.invoiceForLease = invoiceForLease;
    }
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @PropertyLayout(multiLine = Invoice.DescriptionType.Meta.MULTI_LINE)
    public String prop() {
        return invoiceForLease.attributeValueFor(InvoiceAttributeName.INVOICE_COMMENT);
    }
    public boolean hideProp() {
        return false;
    }
}

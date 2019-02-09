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

//region > _preliminaryLetterDescription (derived property)
@Mixin(method="prop")
public class InvoiceForLease_preliminaryLetterDescription {
    private final InvoiceForLease invoiceForLease;
    public InvoiceForLease_preliminaryLetterDescription(final InvoiceForLease invoiceForLease) {
        this.invoiceForLease = invoiceForLease;
    }
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @PropertyLayout(multiLine = Invoice.DescriptionType.Meta.MULTI_LINE)
    public String prop() {
        invoiceForLease.attributeValueFor(InvoiceAttributeName.PRELIMINARY_LETTER_DESCRIPTION);
        return null;
    }
    public boolean hideProp() {
        return false;
    }
}

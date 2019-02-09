package org.estatio.module.lease.dom.invoicing.attr.prop;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeName;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin(method="prop")
public class InvoiceForLease_attributeValueAbstract {
    private final InvoiceForLease invoiceForLease;
    private final InvoiceAttributeName attributeName;

    public InvoiceForLease_attributeValueAbstract(final InvoiceForLease invoiceForLease, final InvoiceAttributeName attributeName) {
        this.invoiceForLease = invoiceForLease;
        this.attributeName = attributeName;
    }
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @PropertyLayout(multiLine = Invoice.DescriptionType.Meta.MULTI_LINE)
    public String prop() {
        return invoiceAttributeRepository.findValueByInvoiceAndName(attributeName, invoiceForLease);
    }
    public boolean hideProp() {
        return false;
    }

    @Inject protected
    InvoiceAttributeRepository invoiceAttributeRepository;

}

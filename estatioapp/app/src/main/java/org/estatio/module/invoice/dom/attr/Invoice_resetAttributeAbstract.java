package org.estatio.module.invoice.dom.attr;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.base.platform.docfragment.FragmentRenderService;
import org.estatio.module.invoice.dom.Invoice;

public abstract class Invoice_resetAttributeAbstract<T extends Invoice<?>> {
    private final T invoice;
    private final InvoiceAttributeName invoiceAttributeName;

    public Invoice_resetAttributeAbstract(final T invoice, final InvoiceAttributeName invoiceAttributeName) {
        this.invoice = invoice;
        this.invoiceAttributeName = invoiceAttributeName;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Invoice act() {
        final Object domainObject = viewModelFor(invoice);
        invoice.updateAttribute(
                invoiceAttributeName,
                fragmentRenderService.render(domainObject, invoiceAttributeName.getFragmentName()),
                Invoice.InvoiceAttributeAction.RESET);
        return invoice;
    }

    protected abstract Object viewModelFor(T invoice);

    public boolean hideAct() {
        return !invoice.attributeOverriddenFor(invoiceAttributeName);
    }

    public String disableAct() {
        if (invoice.isImmutableDueToState()) {
            return "Invoice can't be changed";
        }
        return null;
    }

    @Inject protected
    FragmentRenderService fragmentRenderService;

}

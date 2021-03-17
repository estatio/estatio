package org.estatio.module.invoice.dom.attr;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.types.NotesType;

import org.estatio.module.invoice.dom.Invoice;

/**
 * Subclasses should be annotated with <code>@Mixin(method="act")</code>
 */
public abstract class Invoice_overrideAttributeAbstract {
    private final Invoice invoice;
    private final InvoiceAttributeName invoiceAttributeName;

    public Invoice_overrideAttributeAbstract(final Invoice invoice, final InvoiceAttributeName invoiceAttributeName) {
        this.invoice = invoice;
        this.invoiceAttributeName = invoiceAttributeName;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Invoice act(
            @Parameter(maxLength = NotesType.Meta.MAX_LEN, optionality = Optionality.OPTIONAL)
            @ParameterLayout(multiLine = Invoice.DescriptionType.Meta.MULTI_LINE) final String overrideWith) {
        invoice.updateAttribute(this.invoiceAttributeName, overrideWith, Invoice.InvoiceAttributeAction.OVERRIDE);
        return invoice;
    }

    public String disableAct() {
        if (invoice.isImmutableDueToState()) {
            return "Invoice can't be changed";
        }
        return null;
    }

    public String default0Act() {
        return invoiceAttributeRepository.findValueByInvoiceAndName(invoiceAttributeName, invoice);
    }

    @Inject protected
    InvoiceAttributeRepository invoiceAttributeRepository;

}

package org.estatio.module.coda.dom.elements.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.coda.dom.elements.CodaElement;
import org.estatio.module.coda.dom.elements.InvoiceItemCodaElementsLink;
import org.estatio.module.coda.dom.elements.InvoiceItemCodaElementsLinkRepository;

@Mixin
public class IncomingInvoiceItem_codaElement5 {

    private final IncomingInvoiceItem item;

    public IncomingInvoiceItem_codaElement5(IncomingInvoiceItem item) {
        this.item = item;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public CodaElement $$() {
        final InvoiceItemCodaElementsLink link = repository.findUnique(item);
        return link!=null ? link.getCodaElement5() : null;
    }

    @Inject InvoiceItemCodaElementsLinkRepository repository;

}

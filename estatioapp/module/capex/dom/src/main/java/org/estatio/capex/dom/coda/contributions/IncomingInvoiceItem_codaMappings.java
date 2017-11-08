package org.estatio.capex.dom.coda.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.coda.CodaMapping;
import org.estatio.capex.dom.coda.CodaMappingRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;

/**
 * This cannot be inlined (needs to be a mixin) because IncomingInvoiceItem does not know about Coda.
 */
@Mixin
public class IncomingInvoiceItem_codaMappings {

    private final IncomingInvoiceItem item;

    public IncomingInvoiceItem_codaMappings(IncomingInvoiceItem item) {
        this.item = item;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<CodaMapping> $$() {

        return repository.findMatching(item.getIncomingInvoiceType(), item.getCharge());
    }

    @Inject CodaMappingRepository repository;

}

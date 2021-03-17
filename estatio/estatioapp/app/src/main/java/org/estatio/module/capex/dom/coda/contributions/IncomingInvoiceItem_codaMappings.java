package org.estatio.module.capex.dom.coda.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.coda.CodaMapping;
import org.estatio.module.capex.dom.coda.CodaMappingRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;

/**
 * TODO: this could be inlined, however we'll probably split out coda as a separate module, in which will be a regular contribution again
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

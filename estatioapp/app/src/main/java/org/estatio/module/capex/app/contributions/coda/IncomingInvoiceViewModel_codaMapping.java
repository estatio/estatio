package org.estatio.module.capex.app.contributions.coda;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.coda.CodaMappingRepository;
import org.estatio.module.capex.app.invoice.IncomingDocAsInvoiceViewModel;

/**
 * TODO: this could be inlined, however we'll probably split out coda as a separate module, in which will be a regular contribution again
 */
@Mixin
public class IncomingInvoiceViewModel_codaMapping {

    private final IncomingDocAsInvoiceViewModel item;

    public IncomingInvoiceViewModel_codaMapping(IncomingDocAsInvoiceViewModel item) {
        this.item = item;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public String $$() {

        return repository.findMatching(item.getIncomingInvoiceType(), item.getCharge()).stream()
                .map(x -> x.getCodaElement().title())
                .collect(Collectors.joining(","));
    }

    @Inject
    CodaMappingRepository repository;

}

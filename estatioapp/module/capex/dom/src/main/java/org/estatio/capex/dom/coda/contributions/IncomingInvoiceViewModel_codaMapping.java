package org.estatio.capex.dom.coda.contributions;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.coda.CodaMappingRepository;
import org.estatio.capex.dom.invoice.viewmodel.IncomingDocAsInvoiceViewModel;

/**
 * This cannot be inlined (needs to be a mixin) because IncomingInvoiceItem does not know about Coda.
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

    @Inject CodaMappingRepository repository;

}

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
import org.estatio.capex.dom.invoice.viewmodel.IncomingDocAsInvoiceViewModel;

@Mixin
public class IncomingInvoiceViewModel_codaMappings {

    private final IncomingDocAsInvoiceViewModel item;

    public IncomingInvoiceViewModel_codaMappings(IncomingDocAsInvoiceViewModel item) {
        this.item = item;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<CodaMapping> $$() {
        return repository.findMatching(item.getIncomingInvoiceType(), item.getCharge());
    }

    @Inject CodaMappingRepository repository;

}

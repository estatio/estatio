package org.estatio.capex.dom.coda.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.coda.CodaMapping;
import org.estatio.capex.dom.coda.CodaMappingFilter;
import org.estatio.capex.dom.coda.CodaMappingRepository;
import org.estatio.capex.dom.documents.categorisation.invoice.IncomingDocAsInvoiceViewModel;
import org.estatio.dom.charge.Charge;

@Mixin
public class IncomingInvoiceViewModel_codaMappings {

    private final IncomingDocAsInvoiceViewModel item;

    public IncomingInvoiceViewModel_codaMappings(IncomingDocAsInvoiceViewModel item) {
        this.item = item;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<CodaMapping> $$() {

        final Charge charge = item.getCharge();
        CodaMappingFilter budgetFilter = item.getBudgetItem() != null ? CodaMappingFilter.YES : CodaMappingFilter.NO;
        CodaMappingFilter projectFilter = item.getProject() != null ? CodaMappingFilter.YES : CodaMappingFilter.NO;
        CodaMappingFilter repositoryFilter = item.getProject() != null ? CodaMappingFilter.YES : CodaMappingFilter.NO;

        return repository.findMatching(charge, budgetFilter, projectFilter, repositoryFilter);
    }

    @Inject CodaMappingRepository repository;

}

package org.estatio.module.capex.dom.invoice.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.project.ProjectItemTerm;

/**
 * TODO: although this could currently be inlined, we expect to factor out project from incoming invoice, in which case this will be a typical contribution across modules.
 */
@Mixin
public class ProjectItemTerm_Invoices {

    private final ProjectItemTerm projectItemTerm;
    public ProjectItemTerm_Invoices(ProjectItemTerm projectItemTerm){
        this.projectItemTerm = projectItemTerm;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<IncomingInvoice> invoices() {
        return incomingInvoiceItemRepository.findByProjectItem(projectItemTerm.getProjectItem()).stream()
                .filter(ii->ii.getClass().isAssignableFrom(IncomingInvoiceItem.class))
                .map(ii->(IncomingInvoice) ii.getInvoice())
                .filter(i->i.getPaidDate()!=null)
                .filter(i-> projectItemTerm.getInterval().contains(i.getPaidDate()))
                .collect(Collectors.toList());
    }

    @Inject
    private IncomingInvoiceItemRepository incomingInvoiceItemRepository;
}

package org.estatio.module.capex.dom.invoice.contributions;

import java.math.BigDecimal;
import java.util.function.Function;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

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
public class ProjectItemTerm_PaidAmount {

    private final ProjectItemTerm projectItemTerm;
    public ProjectItemTerm_PaidAmount(ProjectItemTerm projectItemTerm){
        this.projectItemTerm = projectItemTerm;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Column(scale = 2)
    public BigDecimal paidAmount(){
        return sum(IncomingInvoice::getNetAmount);
    }

    private BigDecimal sum(final Function<IncomingInvoice, BigDecimal> x) {
        return incomingInvoiceItemRepository.findByProjectItem(projectItemTerm.getProjectItem()).stream()
                .filter(ii->ii.getClass().isAssignableFrom(IncomingInvoiceItem.class))
                .map(ii->(IncomingInvoice) ii.getInvoice())
                .filter(i->i.getPaidDate()!=null)
                .filter(i-> projectItemTerm.getInterval().contains(i.getPaidDate()))
                .map(x)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Inject
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;
}

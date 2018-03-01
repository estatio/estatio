package org.estatio.module.capex.dom.invoice.contributions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.invoice.dom.InvoiceItem;

/**
 * TODO: although this could currently be inlined, we expect to factor out project from incoming invoice, in which case this will be a typical contribution across modules.
 */
@Mixin
public class Project_InvoicedAmountNotOnProjectItems {

    private final Project project;
    public Project_InvoicedAmountNotOnProjectItems(Project project){
        this.project = project;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Column(scale = 2)
    public BigDecimal invoicedAmountNotOnProjectItems(){
        return sum(InvoiceItem::getNetAmount);
    }

    private BigDecimal sum(final Function<InvoiceItem, BigDecimal> x) {
        return incomingInvoiceItemRepository.invoiceItemsNotOnProjectItem(project).stream()
                .filter(i->!i.isDiscarded())
                .map(x)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean hideInvoicedAmountNotOnProjectItems(){
        return project.isParentProject();
    }

    @Inject
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;
}

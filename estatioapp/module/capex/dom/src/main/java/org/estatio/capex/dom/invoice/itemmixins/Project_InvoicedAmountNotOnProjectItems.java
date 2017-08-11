package org.estatio.capex.dom.invoice.itemmixins;

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

import org.estatio.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.invoice.InvoiceItem;

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

    @Inject
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;
}

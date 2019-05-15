package org.estatio.module.capex.dom.invoice.contributions;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.invoice.dom.InvoiceItem;

/**
 * TODO: although this could currently be inlined, we expect to factor out project from incoming invoice, in which case this will be a typical contribution across modules.
 */
@Mixin
public class Project_PaidAmount {

    private final Project project;
    public Project_PaidAmount(Project project){
        this.project = project;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @Column(scale = 2)
    public BigDecimal paidAmount(){
        return project.isParentProject() ? amountWhenParentProject() : sum(InvoiceItem::getNetAmount);
    }

    private BigDecimal sum(final Function<InvoiceItem, BigDecimal> x) {
        return incomingInvoiceItemRepository.findByProject(project).stream()
                .filter(ii->paidDate(ii)!=null)
                .map(x)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LocalDate paidDate(final InvoiceItem invoiceItem){
        IncomingInvoice invoice = (IncomingInvoice) invoiceItem.getInvoice();
        return invoice.getPaidDate();
    }


    private BigDecimal amountWhenParentProject(){
        BigDecimal result = BigDecimal.ZERO;
        for (Project child : project.getChildren()){
            result = result.add(wrapperFactory.wrap(new Project_PaidAmount(child)).paidAmount());
        }
        return result;
    }

    @Inject
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;

    @Inject WrapperFactory wrapperFactory;
}

package org.estatio.module.capex.dom.invoice.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.module.capex.dom.project.Project;

/**
 * TODO: although this could currently be inlined, we expect to factor out project from incoming invoice, in which case this will be a typical contribution across modules.
 */
@Mixin
public class Project_InvoiceItemsNotOnProjectItem {

    private final Project project;
    public Project_InvoiceItemsNotOnProjectItem(Project project){
        this.project = project;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<IncomingInvoiceItem> invoiceItemsNotOnProjectItem() {
        return incomingInvoiceItemRepository.invoiceItemsNotOnProjectItem(project).stream()
                .filter(i->!i.isDiscarded())
                .collect(Collectors.toList());
    }

    public boolean hideInvoiceItemsNotOnProjectItem(){
        return project.isParentProject();
    }

    @Inject
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;
}

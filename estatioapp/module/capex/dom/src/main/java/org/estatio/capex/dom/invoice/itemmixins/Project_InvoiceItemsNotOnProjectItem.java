package org.estatio.capex.dom.invoice.itemmixins;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.invoice.IncomingInvoiceItemRepository;
import org.estatio.capex.dom.project.Project;

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

    @Inject
    IncomingInvoiceItemRepository incomingInvoiceItemRepository;
}

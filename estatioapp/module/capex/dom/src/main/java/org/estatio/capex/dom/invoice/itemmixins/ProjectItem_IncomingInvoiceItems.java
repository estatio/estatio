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
import org.estatio.module.capex.dom.project.ProjectItem;

/**
 * This cannot be inlined (must be a mixin) because Project does not know about incoming invoices.
 */
@Mixin
public class ProjectItem_IncomingInvoiceItems {

    private final ProjectItem projectItem;
    public ProjectItem_IncomingInvoiceItems(ProjectItem projectItem){
        this.projectItem = projectItem;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<IncomingInvoiceItem> invoiceItems() {
        return incomingInvoiceItemRepository.findByProjectAndCharge(projectItem.getProject(), projectItem.getCharge()).stream()
                .filter(x->!x.isDiscarded())
                .collect(Collectors.toList());
    }

    @Inject
    private IncomingInvoiceItemRepository incomingInvoiceItemRepository;
}

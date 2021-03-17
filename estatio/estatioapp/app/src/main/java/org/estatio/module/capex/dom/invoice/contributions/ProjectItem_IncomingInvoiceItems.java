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
import org.estatio.module.capex.dom.project.ProjectItem;

/**
 * TODO: although this could currently be inlined, we expect to factor out project from incoming invoice, in which case this will be a typical contribution across modules.
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

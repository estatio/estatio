package org.estatio.capex.dom.invoice.approval.tasks;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransition;
import org.estatio.capex.dom.task.Task;

@Mixin
public class IncomingInvoice_tasks {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_tasks(final IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Task> tasks() {
        final List<IncomingInvoiceApprovalStateTransition> transitions = repository.findByDomainObject(incomingInvoice);
        return Task.from(transitions);
    }

    @Inject
    IncomingInvoiceApprovalStateTransition.Repository repository;

    @Inject
    BookmarkService bookmarkService;


}

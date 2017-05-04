package org.estatio.capex.dom.invoice.task;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.TaskRepository;

@Mixin
public class IncomingInvoice_tasks {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_tasks(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<Task> tasks() {
        final List<IncomingInvoiceStateTransition> transitions = repository.findByInvoice(incomingInvoice);
        return Task.from(transitions);
    }

    @Inject
    IncomingInvoiceStateTransitionRepository repository;

    @Inject
    BookmarkService bookmarkService;


}

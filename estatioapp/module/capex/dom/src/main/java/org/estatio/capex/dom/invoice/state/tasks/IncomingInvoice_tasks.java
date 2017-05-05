package org.estatio.capex.dom.invoice.state.tasks;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.bookmark.BookmarkService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransition;
import org.estatio.capex.dom.invoice.state.IncomingInvoiceStateTransitionRepository;
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
        final List<IncomingInvoiceStateTransition> transitions = repository.findByDomainObject(incomingInvoice);
        return Task.from(transitions);
    }

    @Inject
    IncomingInvoiceStateTransitionRepository repository;

    @Inject
    BookmarkService bookmarkService;


}

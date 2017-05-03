package org.estatio.capex.dom.invoice.task;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.IncomingInvoice;

@Mixin
public class IncomingInvoice_tasks {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_tasks(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<StateTransitionForIncomingInvoice> tasks() {
        return repository.findByInvoice(incomingInvoice);
    }

    @Inject
    TaskForIncomingInvoiceRepository repository;


}

package org.estatio.capex.dom.invoice.task;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.dom.roles.EstatioRole;

@Mixin
public class IncomingInvoice_newTask {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_newTask(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action()
    @MemberOrder(name = "tasks", sequence = "1")
    public IncomingInvoice newTask(
            EstatioRole assignTo,
            @Parameter(optionality = Optionality.OPTIONAL)
            String description
    ) {
        repository.create(incomingInvoice, assignTo, description);
        return incomingInvoice;
    }

    @Inject
    TaskForIncomingInvoiceRepository repository;

}

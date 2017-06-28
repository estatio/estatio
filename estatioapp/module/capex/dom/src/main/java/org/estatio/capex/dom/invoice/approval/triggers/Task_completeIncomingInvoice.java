package org.estatio.capex.dom.invoice.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.party.Person;

@Mixin(method = "act")
public class Task_completeIncomingInvoice
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_complete> {

    protected final Task task;

    public Task_completeIncomingInvoice(final Task task) {
        super(task, IncomingInvoice_complete.class);
        this.task = task;
    }

    @Action()
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act(
            Person personToAssignTo,
            @Nullable final String comment,
            final boolean goToNext) {
        Object mixinResult = mixin().act(personToAssignTo, comment);
        return toReturnElse(goToNext, mixinResult);
    }

    public boolean hideAct() {
        return super.hideAct() || mixin().hideAct();
    }


}

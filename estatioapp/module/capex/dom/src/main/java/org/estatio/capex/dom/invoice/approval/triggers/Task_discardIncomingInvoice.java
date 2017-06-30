package org.estatio.capex.dom.invoice.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_discardIncomingInvoice
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_discard> {

    protected final Task task;

    public Task_discardIncomingInvoice(final Task task) {
        super(task, IncomingInvoice_discard.class);
        this.task = task;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(cssClassFa = "trash-o")
    public Object act(
            @Nullable final String comment,
            final boolean goToNext) {
        Object mixinResult = mixin().act(comment);
        return toReturnElse(goToNext, mixinResult);
    }

    public boolean hideAct() {
        return super.hideAct() || mixin().hideAct();
    }

    public String disableAct() {
        if(doGetDomainObjectIfAny() == null) {
            return null;
        }
        return mixin().disableAct();
    }

    public boolean default1Act() {
        return true;
    }

}

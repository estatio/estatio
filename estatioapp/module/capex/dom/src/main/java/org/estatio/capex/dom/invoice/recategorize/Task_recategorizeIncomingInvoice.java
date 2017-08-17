package org.estatio.capex.dom.invoice.recategorize;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.invoice.approval.triggers.Task_mixinIncomingInvoiceAbstract;
import org.estatio.capex.dom.task.Task;

/**
 * This mixin cannot be inlined because Task does not know about its target domain object.
 */
@Mixin(method = "act")
public class Task_recategorizeIncomingInvoice
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_recategorize> {

    protected final Task task;

    public Task_recategorizeIncomingInvoice(final Task task) {
        super(task, IncomingInvoice_recategorize.class);
        this.task = task;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(cssClassFa = "mail-reply", cssClass = "btn-danger")
    public Object act(
            @Nullable final String comment,
            final boolean goToNext) {
        final Object nextTaskIfAny = nextTaskOrWarnIfRequired(goToNext);
        Object mixinResult = mixin().act(comment);
        return coalesce(nextTaskIfAny, mixinResult);
    }

    public boolean hideAct() {
        return super.hideAct();
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

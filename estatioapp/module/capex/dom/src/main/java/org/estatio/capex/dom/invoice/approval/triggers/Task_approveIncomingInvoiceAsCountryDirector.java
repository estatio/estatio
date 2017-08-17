package org.estatio.capex.dom.invoice.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

/**
 * This mixin cannot be inlined because Task does not know about its target domain object.
 */
@Mixin(method = "act")
public class Task_approveIncomingInvoiceAsCountryDirector
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_approveAsCountryDirector> {

    protected final Task task;

    public Task_approveIncomingInvoiceAsCountryDirector(final Task task) {
        super(task, IncomingInvoice_approveAsCountryDirector.class);
        this.task = task;
    }

    @Action()
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "fa-thumbs-up")
    public  Object act(
            @Nullable final String comment,
            final boolean goToNext) {
        final Object nextTaskIfAny = nextTaskOrWarnIfRequired(goToNext);
        Object mixinResult = mixin().act(comment, goToNext);
        return coalesce(nextTaskIfAny, mixinResult);
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

    public String validate0Act(String comment) {
        return validateCommentIfByProxy(comment);
    }

    public boolean default1Act() {
        return true;
    }

}

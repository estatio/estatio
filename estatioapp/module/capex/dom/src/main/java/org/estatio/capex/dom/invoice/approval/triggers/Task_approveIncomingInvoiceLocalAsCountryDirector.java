package org.estatio.capex.dom.invoice.approval.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;

@Mixin(method = "act")
public class Task_approveIncomingInvoiceLocalAsCountryDirector
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_approveLocalAsCountryDirector> {

    protected final Task task;

    public Task_approveIncomingInvoiceLocalAsCountryDirector(final Task task) {
        super(task, IncomingInvoice_approveLocalAsCountryDirector.class);
        this.task = task;
    }

    @Action()
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "fa-thumbs-up")
    public  Object act(
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


    public String validate0Act(String comment) {
        return validateCommentIfByProxy(comment);
    }

    public boolean default1Act() {
        return true;
    }

}

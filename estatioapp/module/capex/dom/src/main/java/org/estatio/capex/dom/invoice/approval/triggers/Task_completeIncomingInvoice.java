package org.estatio.capex.dom.invoice.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
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
    @ActionLayout(cssClassFa = "fa-flag-checkered")
    public Object act(
            final String role,
            @Nullable final Person personToAssignNextTo,
            @Nullable final String comment,
            final boolean goToNext) {
        Object mixinResult = mixin().act(role, personToAssignNextTo, comment);
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

    public String default0Act() {
        return mixin().default0Act();
    }

    public Person default1Act() {
        return mixin().default1Act();
    }

    public List<Person> choices1Act() {
        return mixin().choices1Act();
    }

    public boolean default3Act() {
        return true;
    }

}

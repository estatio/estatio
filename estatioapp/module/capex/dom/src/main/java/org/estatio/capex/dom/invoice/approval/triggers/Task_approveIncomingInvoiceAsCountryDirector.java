package org.estatio.capex.dom.invoice.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.party.Person;

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
            final String role,
            final Person personToAssignNextTo,
            @Nullable final String comment,
            final boolean goToNext) {
        Object mixinResult = mixin().act(role, personToAssignNextTo, comment);
        return toReturnElse(goToNext, mixinResult);
    }

    public boolean hideAct() {
        return super.hideAct() || mixin().hideAct();
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

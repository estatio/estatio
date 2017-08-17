package org.estatio.capex.dom.order.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MinLength;
import org.apache.isis.applib.annotation.Mixin;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.party.Person;

/**
 * This mixin cannot be inlined because Task does not know about its target domain object.
 */
@Mixin(method = "act")
public class Task_completeOrderWithApproval
        extends Task_mixinOrderAbstract<Order_completeWithApproval> {

    private final Task task;

    public Task_completeOrderWithApproval(final Task task) {
        super(task, Order_completeWithApproval.class);
        this.task = task;
    }

    @Action()
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act(
            Person approvedBy,
            LocalDate approvedOn,
            @Nullable final String comment,
            final boolean goToNext) {
        final Object nextTaskIfAny = nextTaskOrWarnIfRequired(goToNext);
        Object mixinResult = mixin().act(approvedBy, approvedOn, comment);
        return coalesce(nextTaskIfAny, mixinResult);
    }

    public List<Person> autoComplete0Act(@MinLength(3) final String searchPhrase) {
        return mixin().autoComplete0Act(searchPhrase);
    }

    public String validate1Act(LocalDate approvedOn) {
        return mixin().validate1Act(approvedOn);
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

}

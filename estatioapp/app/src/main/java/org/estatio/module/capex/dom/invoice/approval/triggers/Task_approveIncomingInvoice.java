package org.estatio.module.capex.dom.invoice.approval.triggers;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;

/**
 * This mixin cannot be inlined because Task does not know about its target domain object.
 */
@Mixin(method = "act")
public class Task_approveIncomingInvoice
        extends Task_mixinIncomingInvoiceAbstract<IncomingInvoice_approve> {

    protected final Task task;

    public Task_approveIncomingInvoice(final Task task) {
        super(task, IncomingInvoice_approve.class);
        this.task = task;
    }

    public static class ActionDomainEvent
            extends Task_mixinIncomingInvoiceAbstract.ActionDomainEvent<Task_approveIncomingInvoice> { }

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "fa-thumbs-o-up")
    public Object act(
            @Nullable final IPartyRoleType roleToAssignNextTo, // ECP-855: this field serves as a hint to the user to which role the next task will be assigned
            @Nullable final Person personToAssignNextTo,
            @Nullable final String comment,
            final boolean goToNext) {
        final Object nextTaskIfAny = nextTaskOrWarnIfRequired(goToNext);
        Object mixinResult = mixin().act(roleToAssignNextTo, personToAssignNextTo, comment, goToNext);
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

    public String validate2Act(String comment) {
        return validateCommentIfByProxy(comment);
    }

    public IPartyRoleType default0Act() {
        return mixin().default0Act();
    }

    public Person default1Act(final IPartyRoleType roleType) {
        return mixin().default1Act(roleType);
    }

    public List<Person> choices1Act(final IPartyRoleType roleType) {
        return mixin().choices1Act(roleType);
    }

    public boolean default3Act() {
        return true;
    }


}

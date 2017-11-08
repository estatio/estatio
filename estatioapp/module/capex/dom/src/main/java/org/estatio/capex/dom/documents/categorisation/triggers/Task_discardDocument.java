package org.estatio.capex.dom.documents.categorisation.triggers;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.dom.task.Task;

/**
 * This cannot be inlined (needs to be a mixin) because Task does not know about the domain object it refers to.
 */
@Mixin(method = "act")
public class Task_discardDocument
        extends Task_mixinDocumentAbstract<Document_discard> {

    protected final Task task;

    public Task_discardDocument(final Task task) {
        super(task, Document_discard.class);
        this.task = task;
    }

    public static class ActionDomainEvent
            extends Task_mixinDocumentAbstract.ActionDomainEvent<Task_discardDocument> { }

    @Action(
            domainEvent = Task_categoriseDocumentAsOrder.ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "trash-o")
    public Object act(
            @Nullable final String comment,
            final boolean goToNext) {
        final Object nextTaskIfAny = nextTaskOrWarnIfRequired(goToNext);
        Document mixinResult = mixin().act(comment);
        return coalesce(nextTaskIfAny, mixinResult);
    }

    public boolean default1Act() {
        return true;
    }

    public boolean hideAct() {
        return super.hideAct() || mixin().hideAct();
    }

    public String disableAct() {
        if(doGetDomainObjectIfAny() == null) {
            return null;
        }
        return  mixin().disableAct();
    }

}

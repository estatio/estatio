package org.estatio.module.capex.app.contributions.categorise;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.app.document.IncomingDocViewModel;
import org.estatio.module.capex.dom.documents.categorisation.triggers.Document_categoriseAsPropertyInvoice;
import org.estatio.module.capex.dom.documents.categorisation.triggers.Task_mixinDocumentAbstract;
import org.estatio.module.capex.dom.task.Task;

/**
 * This cannot be inlined (needs to be a mixin) because Task does not know about the domain object it refers to.
 */
@Mixin(method = "act")
public class Task_categoriseDocumentAsPropertyInvoice
        extends Task_mixinDocumentAbstract<Document_categoriseAsPropertyInvoice> {

    protected final Task task;

    public Task_categoriseDocumentAsPropertyInvoice(final Task task) {
        super(task, Document_categoriseAsPropertyInvoice.class);
        this.task = task;
    }

    public static class ActionDomainEvent
            extends Task_mixinDocumentAbstract.ActionDomainEvent<Task_categoriseDocumentAsPropertyInvoice> { }

    @Action(
            domainEvent = Task_categoriseDocumentAsOrder.ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "folder-open-o")
    public Object act(
            final Property property,
            @Nullable final String comment,
            final boolean goToNext) {
        final Object nextTaskIfAny = nextTaskOrWarnIfRequired(goToNext);
        Object mixinResult = mixin().act(property, comment);
        if(mixinResult instanceof IncomingDocViewModel) {
            IncomingDocViewModel viewModel = (IncomingDocViewModel) mixinResult;
            // to support 'goToNext' when finished with the view model
            viewModel.setOriginatingTask(task);
        }
        return coalesce(nextTaskIfAny, mixinResult);
    }

    public boolean default2Act() {
        return true;
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

    public List<Property> choices0Act() {
        return mixin().choices0Act();
    }

}

package org.estatio.module.capex.app.contributions.categorise;

import javax.annotation.Nullable;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.app.document.IncomingDocViewModel;
import org.estatio.module.capex.dom.documents.categorisation.triggers.Document_categoriseAsOrder;
import org.estatio.module.capex.dom.documents.categorisation.triggers.Task_mixinDocumentAbstract;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.task.Task;
import org.estatio.module.asset.dom.Property;

/**
 * This cannot be inlined (needs to be a mixin) because Task does not know about the domain object it refers to.
 */
@Mixin(method = "act")
public class Task_categoriseDocumentAsOrder
        extends Task_mixinDocumentAbstract<Document_categoriseAsOrder> {

    protected final Task task;

    public Task_categoriseDocumentAsOrder(final Task task) {
        super(task, Document_categoriseAsOrder.class);
        this.task = task;
    }

    public static class ActionDomainEvent
            extends Task_mixinDocumentAbstract.ActionDomainEvent<Task_categoriseDocumentAsOrder> { }

    @Action(
            domainEvent = ActionDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "folder-open-o")
    public Object act(
            @Nullable final Property property,
            final IncomingInvoiceType orderType,
            @Nullable final String comment,
            final boolean goToNext) {
        final Object nextTaskIfAny = nextTaskOrWarnIfRequired(goToNext);
        Object mixinResult = mixin().act(property, orderType, comment);
        if(mixinResult instanceof IncomingDocViewModel) {
            IncomingDocViewModel viewModel = (IncomingDocViewModel) mixinResult;
            // to support 'goToNext' when finished with the view model
            viewModel.setOriginatingTask(task);
        }
        return coalesce(nextTaskIfAny, mixinResult);
    }

    public String validateAct(
            final Property property,
            final IncomingInvoiceType orderType,
            final String comment,
            final boolean goToNext) {
        return mixin().validateAct(property, orderType, comment);
    }

    public IncomingInvoiceType default1Act() {
        return IncomingInvoiceType.CAPEX;
    }

    public boolean default3Act() {
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


}

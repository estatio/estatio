package org.estatio.capex.dom.documents.categorisation.triggers;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.document.IncomingDocViewModel;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_mixinActAbstract;
import org.estatio.dom.asset.Property;

@Mixin(method = "act")
public class Task_categoriseDocumentAsOrder
        extends Task_mixinActAbstract<Document_categoriseAsOrder, Document> {

    protected final Task task;

    public Task_categoriseDocumentAsOrder(final Task task) {
        super(task, Document_categoriseAsOrder.class);
        this.task = task;
    }

    @Action()
    @ActionLayout(contributed = Contributed.AS_ACTION, cssClassFa = "folder-open-o")
    public Object act(
            @Nullable final Property property,
            @Nullable final String comment,
            final boolean goToNext) {
        Object mixinResult = mixin().act(property, comment);
        if(mixinResult instanceof IncomingDocViewModel) {
            IncomingDocViewModel viewModel = (IncomingDocViewModel) mixinResult;
            // to support 'goToNext' when finished with the view model
            viewModel.setOriginatingTask(task);
        }
        return toReturnElse(goToNext, mixinResult);
    }

    public String validateAct(
            final Property property,
            final String comment,
            final boolean goToNext) {
        return mixin().validateAct(property, comment);
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

    @Override
    protected Document doGetDomainObjectIfAny() {
        final IncomingDocumentCategorisationStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getDocument() : null;
    }

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository repository;

}

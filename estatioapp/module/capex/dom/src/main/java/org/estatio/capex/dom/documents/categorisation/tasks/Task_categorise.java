package org.estatio.capex.dom.documents.categorisation.tasks;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.document.Document_categorise;
import org.estatio.capex.dom.documents.categorisation.document.IncomingDocViewModel;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.Task_mixinActAbstract;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

@Mixin(method = "act")
public class Task_categorise
        extends
        Task_mixinActAbstract<Document_categorise, Document> {

    protected final Task task;

    public Task_categorise(final Task task) {
        super(task, Document_categorise.class);
        this.task = task;
    }

    @Action()
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Object act(
            final DocumentTypeData documentTypeData,
            @Nullable final IncomingInvoice.Type incomingInvoiceType,
            @Nullable final Property property,
            @Nullable final String comment,
            final boolean goToNext) {
        Object mixinResult = mixin().act(documentTypeData, incomingInvoiceType, property, comment);
        if(mixinResult instanceof IncomingDocViewModel) {
            IncomingDocViewModel viewModel = (IncomingDocViewModel) mixinResult;
            // to support 'goToNext' when finished with the view model
            viewModel.setOriginatingTask(task);
        }
        return toReturnElse(goToNext, mixinResult);
    }

    public List<DocumentTypeData> choices0Act() {
        return mixin().choices0Act();
    }

    public String validateAct(
            final DocumentTypeData documentTypeData,
            final IncomingInvoice.Type incomingInvoiceType,
            final Property property,
            final String comment) {
        return mixin().validateAct(documentTypeData, incomingInvoiceType, property, comment);
    }

    public boolean default4Act() {
        return true;
    }

    public boolean hideAct() {
        return super.hideAct() || mixin().hideAct();
    }

    @Override
    protected Document doGetDomainObjectIfAny() {
        final IncomingDocumentCategorisationStateTransition transition = repository.findByTask(this.task);
        return transition != null ? transition.getDocument() : null;
    }

    @Inject
    IncomingDocumentCategorisationStateTransition.Repository repository;

}

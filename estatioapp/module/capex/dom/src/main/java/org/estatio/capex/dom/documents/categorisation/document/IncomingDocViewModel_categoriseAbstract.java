package org.estatio.capex.dom.documents.categorisation.document;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentRepository;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.TaskRepository;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.DocumentTypeData;

public abstract class IncomingDocViewModel_categoriseAbstract extends
        DocOrIncomingDocViewModel_categoriseAsAbstract {

    protected final IncomingDocViewModel viewModel;

    public IncomingDocViewModel_categoriseAbstract(
            final IncomingDocViewModel viewModel,
            final DocumentTypeData documentTypeData) {
        super(documentTypeData);
        this.viewModel = viewModel;
    }

    @Override
    public Document getDomainObject() {
        return viewModel.getDocument();
    }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    @ActionLayout(cssClassFa = "folder-open-o")
    public Object act(
            @Nullable final Property property,
            @Nullable final String comment,
            final boolean goToNext) {
        final Document document = categoriseAndAttachPaperclip(property);

        IncomingDocumentCategorisationStateTransition transition =
                trigger(comment);

        if (goToNext){
            Task nextTask = nextTaskForMeElseFrom(transition);
            if(nextTask != null) {
                return nextTask;
            }
            // else fall through to returning the view model for this document
            messageService.informUser("No more tasks");
        }

        return this.viewModelFactory.createFor(document);
    }

    @Override
    public Property default0Act() {
        return super.default0Act();
    }

    public boolean default2Act(){
        return true;
    }

    @Override
    public boolean hideAct() {
        return super.hideAct();
    }

    private Task nextTaskForMeElseFrom(final IncomingDocumentCategorisationStateTransition transition) {
        Task taskJustCompleted = viewModel.getOriginatingTask();
        if(taskJustCompleted == null) {
            taskJustCompleted = transition.getTask();
        }
        List<Task> remainingTasks =
                taskRepository.findTasksIncompleteForMeCreatedOnAfter(taskJustCompleted.getCreatedOn());
        return !remainingTasks.isEmpty() ? remainingTasks.get(0) : null;
    }

    @Inject
    MessageService messageService;

    @Inject
    DocumentRepository documentRepository;

    @Inject
    TaskRepository taskRepository;

    @Inject
    IncomingDocViewModel.Factory viewModelFactory;

}

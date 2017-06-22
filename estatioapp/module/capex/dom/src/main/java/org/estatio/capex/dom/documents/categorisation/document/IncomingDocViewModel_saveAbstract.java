package org.estatio.capex.dom.documents.categorisation.document;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.message.MessageService;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationState;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransition;
import org.estatio.capex.dom.documents.categorisation.IncomingDocumentCategorisationStateTransitionType;
import org.estatio.capex.dom.order.OrderRepository;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.TaskRepository;
import org.estatio.capex.dom.triggers.DomainObject_triggerAbstract;

import lombok.Getter;

public abstract class IncomingDocViewModel_saveAbstract<
        T,
        VM extends IncomingDocViewModel<T>
        > extends DomainObject_triggerAbstract<
        Document,
        IncomingDocumentCategorisationStateTransition,
        IncomingDocumentCategorisationStateTransitionType,
        IncomingDocumentCategorisationState
        > {

    @Getter
    protected final VM viewModel;

    public IncomingDocViewModel_saveAbstract(final VM viewModel, final IncomingDocumentCategorisationStateTransitionType transitionType) {
        super(viewModel.getDocument(), transitionType);
        this.viewModel = viewModel;
    }


    @Action(
            semantics = SemanticsOf.IDEMPOTENT
    )
    public Object act(
            @Nullable final String comment,
            final boolean goToNext){
        final T domainObject = doCreate();

        IncomingDocumentCategorisationStateTransition transition =
                trigger(comment);

        this.viewModel.setDomainObject(domainObject);

        if (goToNext){
            Task nextTask = nextTaskForMeElseFrom(transition);
            if(nextTask != null) {
                return nextTask;
            }
            // else fall through to returning the view model for this document
            messageService.informUser("No more tasks");
        }

        return domainObject;
    }


    public boolean default1Act(){
        return true;
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct(){
        return viewModel.minimalRequiredDataToComplete();
    }


    protected abstract T doCreate();

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
    TaskRepository taskRepository;

    @Inject
    protected IncomingDocViewModel.Factory factory;

    @Inject
    protected PaperclipRepository paperclipRepository;

    @Inject
    protected OrderRepository orderRepository;

    @Inject
    protected ClockService clockService;

}

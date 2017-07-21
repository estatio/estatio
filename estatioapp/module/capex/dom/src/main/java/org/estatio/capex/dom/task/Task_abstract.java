package org.estatio.capex.dom.task;

import javax.inject.Inject;

import org.apache.isis.applib.services.message.MessageService;

public abstract class Task_abstract {

    protected final Task task;

    public Task_abstract(final Task task) {
        this.task = task;
    }

    protected Object nextTaskOrWarnIfRequired(final boolean goToNext) {
        if (goToNext){
            final Task nextTask = nextTaskAfter(task);
            if (nextTask != null) {
                return nextTask;
            }
            // fall through to returning the view model for this document
            messageService.informUser("No more tasks");
        }

        return null;
    }

    protected static Object coalesce(final Object... candidates) {
        for (Object candidate : candidates) {
            if(candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    protected Task nextTaskBefore(final Task task) {
        return taskRepository.previousTaskBefore(task);
    }

    protected Task nextTaskAfter(final Task task) {
        return taskRepository.nextTaskAfter(task);
    }

    @Inject
    TaskRepository taskRepository;

    @Inject
    MessageService messageService;

}

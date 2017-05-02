package org.estatio.capex.dom.invoice.rule;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

public interface TaskTransition<DO extends TaskState.Owner<DO, S>, S extends TaskState<DO, S>, TT extends TaskTransition<DO, S, TT>> {

    @Programmatic
    void preApply(
            final DO domainObject,
            final TaskTransition<DO, S, TT> transition);

    @Programmatic
    void postApply(
            final DO domainObject,
            final TaskTransition<DO, S, TT> transition);

    @Programmatic
    List<S> getFromStates();

    @Programmatic
    S getToState();

    @Programmatic
    EstatioRole getTaskRoleRequiredIfAny();

    @Programmatic
    Task<?> newTaskIfApplicable(
            DO domainObject,
            final WrapperFactory wrapperFactory,
            final FactoryService factoryService);

    List<TT> allValues();

    class Util {

        private Util(){}

        public static
        <TT extends TaskTransition<DO, S, TT>,
                S extends TaskState<DO, S>,
                DO extends TaskState.Owner<DO, S>>
        boolean isFromState(
                final TT taskTransition,
                final S fromState) {
            final List<S> fromStates = taskTransition.getFromStates();
            return fromStates == null || fromStates.contains(fromState);
        }

        public static
            <TT extends TaskTransition<DO, S, TT>,
             S extends TaskState<DO, S>,
             DO extends TaskState.Owner<DO, S>>
        List<TT> transitionsFrom(
                final TT prototype,
                final S fromState) {
            final List<TT> transitions = Lists.newArrayList();
            final List<TT> allTransitions = prototype.allValues();
            for (TT transition : allTransitions) {
                if (isFromState(prototype, fromState)) {
                    continue;
                }
                transitions.add(transition);
            }
            return transitions;
        }

        public static <TT extends TaskTransition<DO, S, TT>,
                S extends TaskState<DO, S>,
                DO extends TaskState.Owner<DO, S>>
        Task<?> apply(
                final TT taskTransition,
                final DO domainObject,
                final WrapperFactory wrapperFactory,
                final FactoryService factoryService) {

            taskTransition.preApply(domainObject, taskTransition);

            // transition the domain object to its next state
            final S nextState = taskTransition.getToState();
            domainObject.setTaskState(nextState);

            // for wherever we might go next, we spin throug all possible transitions,
            // and create a task for the first one that applies to this particular domain object.
            final List<TT> candidateTransitions = transitionsFrom(taskTransition, nextState);
            Task<?> task = null;
            for (TT candidateTransition : candidateTransitions) {
                final EstatioRole taskRole = candidateTransition.getTaskRoleRequiredIfAny();
                if(taskRole == null) {
                    continue;
                }
                task = candidateTransition.newTaskIfApplicable(domainObject, wrapperFactory, factoryService);
                if(task != null) {
                    break;
                }
            }

            taskTransition.postApply(domainObject, taskTransition);
            return task;
        }

        public static <TT extends TaskTransition<DO, S, TT>,
                S extends TaskState<DO, S>,
                DO extends TaskState.Owner<DO, S>>
        boolean canApply(
                final TT transition,
                final DO domainObject) {
            // TODO: need to beef this up, to also take into account the state of the domain object
            return TaskTransition.Util.isFromState(transition, domainObject.getTaskState());
        }
    }


}

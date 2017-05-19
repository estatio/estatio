package org.estatio.capex.dom.state;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

public abstract class StateTransitionRepositoryAbstract<
        DO,
        ST extends StateTransitionAbstract<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > implements StateTransitionRepository<DO,ST,STT,S> {

    private final Class<ST> stateTransitionClass;

    public StateTransitionRepositoryAbstract(final Class<ST> stateTransitionClass) {
        this.stateTransitionClass = stateTransitionClass;
    }

    @Override
    @Programmatic
    public List<ST> listAll() {
        return repositoryService.allInstances(stateTransitionClass);
    }

    @Override
    @Programmatic
    public List<ST> findByDomainObject(final DO domainObject) {
        return repositoryService.allMatches(
                new org.apache.isis.applib.query.QueryDefault<>(
                        stateTransitionClass,
                        "findByDomainObject",
                        "domainObject", domainObject));
    }

    @Override
    @Programmatic
    public ST findByDomainObjectAndCompleted(final DO domainObject, final boolean completed) {
        // can't be uniqueMatch, because for the very first call to StateTransitionService#apply, called on an
        // INSTANTIATED event, there won't yet be any StateTransitions for this invoice
        return repositoryService.firstMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        stateTransitionClass,
                        "findByDomainObjectAndCompleted",
                        "domainObject", domainObject,
                        "completed", completed ));
    }


    @Programmatic
    public ST findByTask(final Task task) {
        return repositoryService.firstMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        stateTransitionClass,
                        "findByTask",
                        "task", task));
    }

    @Override
    public ST create(
            final DO domainObject,
            final STT transitionType,
            final S fromState,
            final EstatioRole taskAssignToIfAny,
            final String taskDescription) {

        final Task taskIfAny = createTaskIfRequired(taskAssignToIfAny, taskDescription);

        final ST stateTransition = createTransition(domainObject, transitionType, fromState, taskIfAny);

        return stateTransition;
    }

    protected Task createTaskIfRequired(
            final EstatioRole taskAssignToIfAny,
            final String taskDescription) {
        if (taskAssignToIfAny == null) {
            return null;
        }
        final String transitionObjectType = metaModelService3.toObjectType(stateTransitionClass);
        final Task task = new Task(taskAssignToIfAny, taskDescription, transitionObjectType);
        repositoryService.persist(task);
        return task;
    }


    protected ST createTransition(
            final DO domainObject,
            final STT transitionType,
            final S fromState,
            final Task taskIfAny) {

        final ST stateTransition = repositoryService.instantiate(stateTransitionClass);
        stateTransition.setDomainObject(domainObject);
        stateTransition.setTransitionType(transitionType);
        stateTransition.setFromState(fromState);
        stateTransition.setTask(taskIfAny);

        final LocalDateTime createdOn = clockService.nowAsLocalDateTime();
        stateTransition.setCreatedOn(createdOn);

        repositoryService.persistAndFlush(stateTransition);

        return stateTransition;
    }

    @Inject
    protected MetaModelService3 metaModelService3;

    @Inject
    protected RepositoryService repositoryService;

    @Inject
    protected ClockService clockService;

}

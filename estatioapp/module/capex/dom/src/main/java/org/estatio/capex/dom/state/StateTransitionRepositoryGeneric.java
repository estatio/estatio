package org.estatio.capex.dom.state;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.xactn.TransactionService;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

@DomainService(nature = NatureOfService.DOMAIN)
public class StateTransitionRepositoryGeneric {

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    List<ST> listAll(final Class<ST> stateTransitionClass) {
        return repositoryService.allInstances(stateTransitionClass);
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    List<ST> findByDomainObject(final DO domainObject, final Class<ST> stateTransitionClass) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        stateTransitionClass,
                        "findByDomainObject",
                        "domainObject", domainObject));
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST findFirstByDomainObject(final DO domainObject, final Class<ST> stateTransitionClass) {
        return repositoryService.firstMatch(
                new QueryDefault<>(
                        stateTransitionClass,
                        "findByDomainObject",
                        "domainObject", domainObject));
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST findByDomainObjectAndCompleted(final DO domainObject, final boolean completed, final Class<ST> stateTransitionClass) {
        // can't be uniqueMatch, because for the very first call to StateTransitionService#trigger, called on an
        // INSTANTIATED event, there won't yet be any StateTransitions for this invoice
        return repositoryService.firstMatch(
                new QueryDefault<>(
                        stateTransitionClass,
                        "findByDomainObjectAndCompleted",
                        "domainObject", domainObject,
                        "completed", completed ));
    }


    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST findByTask(final Task task, final Class<ST> stateTransitionClass) {
        return repositoryService.firstMatch(
                new QueryDefault<>(
                        stateTransitionClass,
                        "findByTask",
                        "task", task));
    }

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST create(
            final DO domainObject,
            final STT transitionType,
            final S fromState,
            final EstatioRole taskAssignToIfAny,
            final String taskDescription,
            final Class<ST> stateTransitionClass) {

        final Task taskIfAny = createTaskIfRequired(taskAssignToIfAny, taskDescription, stateTransitionClass);

        final ST stateTransition = createTransition(domainObject, transitionType, fromState, taskIfAny, stateTransitionClass);

        return stateTransition;
    }

    protected <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    Task createTaskIfRequired(
            final EstatioRole taskAssignToIfAny,
            final String taskDescription,
            final Class<ST> stateTransitionClass) {
        if (taskAssignToIfAny == null) {
            return null;
        }
        final String transitionObjectType = metaModelService3.toObjectType(stateTransitionClass);
        final Task task = new Task(taskAssignToIfAny, taskDescription, transitionObjectType);
        repositoryService.persist(task);
        return task;
    }


    protected <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    ST createTransition(
            final DO domainObject,
            final STT transitionType,
            final S fromState,
            final Task taskIfAny,
            final Class<ST> stateTransitionClass) {

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

    @Programmatic
    public <
            DO,
            ST extends StateTransitionAbstract<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            >
    void deleteFor(final DO domainObject, final Class<ST> stateTransitionClass) {
        final List<ST> stateTransitions = findByDomainObject(domainObject, stateTransitionClass);
        for (ST transition : stateTransitions) {
            repositoryService.removeAndFlush(transition);
        }
        transactionService.flushTransaction();
    }

    @Inject
    protected MetaModelService3 metaModelService3;

    @Inject
    protected RepositoryService repositoryService;

    @Inject
    protected TransactionService transactionService;

    @Inject
    protected ClockService clockService;

}

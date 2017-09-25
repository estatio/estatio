package org.estatio.capex.dom.state;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.capex.dom.task.Task;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.IPartyRoleType;

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
        return stateTransitionRepositoryGeneric.listAll(stateTransitionClass);
    }

    @Override
    @Programmatic
    public List<ST> findByDomainObject(final DO domainObject) {
        return stateTransitionRepositoryGeneric.findByDomainObject(domainObject, stateTransitionClass);
    }

    @Override
    @Programmatic
    public ST findByDomainObjectAndCompleted(final DO domainObject, final boolean completed) {
        return stateTransitionRepositoryGeneric.findByDomainObjectAndCompleted(domainObject, completed, stateTransitionClass);
    }


    @Override
    @Programmatic
    public ST findByTask(final Task task) {
        return stateTransitionRepositoryGeneric.findByTask(task, stateTransitionClass);
    }

    @Override
    @Programmatic
    public ST create(
            final DO domainObject,
            final STT transitionType,
            final S fromState,
            final IPartyRoleType taskAssignToIfAny,
            final Person personToAssignToIfAny,
            final String taskDescription) {

        return stateTransitionRepositoryGeneric.create(
                domainObject, transitionType, fromState,
                taskAssignToIfAny, personToAssignToIfAny, taskDescription,
                stateTransitionClass);
    }

    @Override
    @Programmatic
    public void deleteFor(final DO domainObject) {
        stateTransitionRepositoryGeneric.deleteFor(domainObject, stateTransitionClass);
    }

    @Programmatic
    public ST findByDomainObjectAndToState(
            final DO domainObject,
            final S toState,
            final NatureOfTransition natureOfTransition) {

        final List<ST> transitions = stateTransitionRepositoryGeneric.findByDomainObject(domainObject, stateTransitionClass);
        final List<ST> completedTransitions =
                transitions.stream()
                        .filter(x -> x.getToState() == toState)

                        // if nature of transition is required to be explicit, then this in turn implies that
                        // completedBy will have been populated.
                        .filter(x-> natureOfTransition != NatureOfTransition.EXPLICIT ||
                                    x.getCompletedBy() != null)

                        .sorted(completedOn().reversed())
                        .collect(Collectors.toList());

        return completedTransitions.isEmpty() ? null : completedTransitions.get(0);
    }

    private Comparator<ST> completedOn() {
        return Comparator.comparing(StateTransitionAbstract::getCompletedOn);
    }

    @Inject
    StateTransitionRepositoryGeneric stateTransitionRepositoryGeneric;

}

package org.estatio.module.task.dom.triggers;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.task.dom.state.State;
import org.estatio.module.task.dom.state.StateTransition;
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.task.dom.state.StateTransitionType;
import org.estatio.module.task.dom.task.Task;
import org.estatio.module.task.dom.task.TaskRepository;
import org.estatio.module.task.dom.policy.EnforceTaskAssignmentPolicySubscriber;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeService;

/**
 * Subclasses should be annotated using: @Mixin(method = "act")
 */
public abstract class DomainObject_triggerAbstract<
        DO,
        ST extends StateTransition<DO, ST, STT, S>,
        STT extends StateTransitionType<DO, ST, STT, S>,
        S extends State<S>
        > {

    protected final DO domainObject;
    protected final Class<ST> stateTransitionClass;
    protected final List<S> fromStates;
    protected final STT requiredTransitionType;

    public static abstract class ActionDomainEvent<MIXIN>
            extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<MIXIN>
            implements EnforceTaskAssignmentPolicySubscriber.WithStateTransitionClass {

    }

    protected DomainObject_triggerAbstract(
            final DO domainObject,
            final Class<ST> stateTransitionClass,
            final List<S> fromStates,
            final STT requiredTransitionType) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
        this.fromStates = fromStates;
        this.requiredTransitionType = requiredTransitionType;
    }

    protected DomainObject_triggerAbstract(
            final DO domainObject,
            final Class<ST> stateTransitionClass,
            final STT requiredTransitionType) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
        this.fromStates = requiredTransitionType.getFromStates();
        this.requiredTransitionType = requiredTransitionType;
    }


    public DO getDomainObject() {
        return domainObject;
    }

    protected DO nextAfterPendingIfRequested(final boolean goToNext) {
        final DO nextObj = goToNext ? nextAfterPending() : null;
        return coalesce(nextObj, getDomainObject());
    }

    protected DO nextAfterPending() {
        return queryResultsCache.execute(
                this::doNextAfterPending, getClass(), "nextAfterPending", getDomainObject());
    }

    private DO doNextAfterPending() {
        final ST pendingTransition = stateTransitionService
                .pendingTransitionOf(getDomainObject(), stateTransitionClass);
        final DO nextInvoice = nextViaTask(pendingTransition);
        return coalesce(nextInvoice, getDomainObject());
    }

    private DO nextViaTask(final ST transition) {
        if(transition == null) {
            return null;
        }
        final Task task = transition.getTask();
        if (task == null) {
            return null;
        }

        final Task nextTask = taskRepository.nextTaskAfter(task);
        if(nextTask == null) {
            return null;
        }
        final ST nextTransition = findByTask(nextTask);
        if(nextTransition == null) {
            return null;
        }
        return nextTransition.getDomainObject();
    }


    protected DO previousBeforePending() {
        return queryResultsCache.execute(
                this::doPreviousBeforePending, getClass(), "previousBeforePending", getDomainObject());
    }

    private DO doPreviousBeforePending() {
        final ST pendingTransition = stateTransitionService
                .pendingTransitionOf(getDomainObject(), stateTransitionClass);
        final DO previousInvoice = previousViaTask(pendingTransition);
        return coalesce(previousInvoice, getDomainObject());
    }

    private DO previousViaTask(final ST transition) {
        if(transition == null) {
            return null;
        }
        final Task task = transition.getTask();
        if (task == null) {
            return null;
        }

        final Task previousTask = taskRepository.previousTaskBefore(task);
        if(previousTask == null) {
            return null;
        }
        final ST previousTransition = findByTask(previousTask);
        if(previousTransition == null) {
            return null;
        }
        return previousTransition.getDomainObject();
    }


    protected static <T> T coalesce(final T... candidates) {
        for (T candidate : candidates) {
            if(candidate != null) {
                return candidate;
            }
        }
        return null;
    }

    protected ST findByTask(final Task previousTask) {
        return null;
    }


    /**
     * Subclasses should call, to request that the state transition occur (or at least, be attempted).
     *
     * <p>
     *     It's possible that the transition may not occur if there
     *     is {@link StateTransitionType#isGuardSatisfied(Object, ServiceRegistry2) guard} that is not yet satisfied
     *     for the particular domain object.
     * </p>
     *
     * @return - the {@link StateTransition} most recently completed for the domain object.
     */
    protected final ST trigger(
            final String currentTaskCommentIfAny,
            final String nextTaskDescriptionIfAny) {
        return stateTransitionService.trigger(getDomainObject(), stateTransitionClass, requiredTransitionType,
                currentTaskCommentIfAny, nextTaskDescriptionIfAny);
    }

    protected final ST trigger(
            final IPartyRoleType role,
            final Person personToAssignNextTo,
            final String currentTaskCommentIfAny,
            final String nextTaskDescriptionIfAny) {
        return stateTransitionService.trigger(getDomainObject(), stateTransitionClass, requiredTransitionType, role, personToAssignNextTo,
                currentTaskCommentIfAny, nextTaskDescriptionIfAny);
    }

    protected final ST getPendingTransition() {
        return stateTransitionService.pendingTransitionOf(getDomainObject(), stateTransitionClass);
    }

    protected Person defaultPersonToAssignNextTo(final IPartyRoleType roleType) {
        if(requiredTransitionType == null) {
            return null;
        }
        return partyRoleTypeService.onlyMemberOfElseNone(roleType, domainObject);
    }

    protected List<Person> choicesPersonToAssignNextTo(final IPartyRoleType roleType) {
        if(requiredTransitionType == null) {
            return Collections.emptyList();
        }
        return partyRoleTypeService.membersOf(roleType);
    }

    private <T extends Enum<T> & IPartyRoleType> List<T> peekPartyRoleType() {
        if(requiredTransitionType == null) {
            return null;
        }
        List<IPartyRoleType> iPartyRoleTypes = stateTransitionService
                .peekTaskRoleAssignToAfter(domainObject, requiredTransitionType);
        final List collect =
                iPartyRoleTypes.stream()
                        .filter(x -> Enum.class.isAssignableFrom(x.getClass()))
                        .collect(Collectors.toList());
        return collect;
    }

    protected <T extends Enum<T> & IPartyRoleType> List<T> enumPartyRoleType() {
        return peekPartyRoleType();
    }

    protected IPartyRoleType firstPartyRoleType() {
        return enumPartyRoleType().stream().findFirst().orElse(null);
    }

    /**
     * Subclasses must call, typically in their <tt>hideAct()</tt> guargs, in order to check whether {@link #trigger(String, String)}.
     */
    protected final boolean cannotTransition() {
        return !canTransition();
    }

    private boolean canTransition() {
        final S currentState = stateTransitionService.currentStateOf(getDomainObject(), stateTransitionClass);
        if(requiredTransitionType != null) {
            return requiredTransitionType.canTransitionFromStateAndIsMatch(
                                                        domainObject, currentState, serviceRegistry2);
        } else {
            return fromStates.contains(currentState);
        }
    }

    protected String reasonGuardNotSatisified() {
        if(requiredTransitionType != null) {
            return requiredTransitionType.reasonGuardNotSatisified(domainObject, serviceRegistry2);
        } else {
            ST st = stateTransitionService.pendingTransitionOf(domainObject, stateTransitionClass);
            if (st == null) {
                return null;
            }
            return st.getTransitionType().reasonGuardNotSatisified(domainObject, serviceRegistry2);
        }
    }

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    TaskRepository taskRepository;

    @Inject
    protected StateTransitionService stateTransitionService;

    @Inject
    protected ServiceRegistry2 serviceRegistry2;

    @Inject
    protected PartyRoleTypeService partyRoleTypeService;

}

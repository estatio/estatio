package org.estatio.capex.dom.triggers;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.IPartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeService;

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
    protected final STT requiredTransitionTypeIfAny;

    protected DomainObject_triggerAbstract(
            final DO domainObject,
            final Class<ST> stateTransitionClass,
            final List<S> fromStates,
            final STT requiredTransitionTypeIfAny) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
        this.fromStates = fromStates;
        this.requiredTransitionTypeIfAny = requiredTransitionTypeIfAny;
    }

    protected DomainObject_triggerAbstract(
            final DO domainObject,
            final Class<ST> stateTransitionClass,
            final STT requiredTransitionType) {
        this.domainObject = domainObject;
        this.stateTransitionClass = stateTransitionClass;
        this.fromStates = requiredTransitionType.getFromStates();
        this.requiredTransitionTypeIfAny = requiredTransitionType;
    }


    public DO getDomainObject() {
        return domainObject;
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
        return stateTransitionService.trigger(getDomainObject(), stateTransitionClass, requiredTransitionTypeIfAny,
                currentTaskCommentIfAny, nextTaskDescriptionIfAny);
    }

    protected final ST trigger(
            final Person personToAssignNextTo,
            final String currentTaskCommentIfAny,
            final String nextTaskDescriptionIfAny) {
        return stateTransitionService.trigger(getDomainObject(), stateTransitionClass, requiredTransitionTypeIfAny, personToAssignNextTo, currentTaskCommentIfAny,
                nextTaskDescriptionIfAny);
    }

    protected Person defaultPersonToAssignNextTo() {
        if(requiredTransitionTypeIfAny == null) {
            return null;
        }
        IPartyRoleType partyRoleType = peekPartyRoleType();
        return partyRoleTypeService.firstMemberOf(partyRoleType, domainObject);
    }

    protected List<Person> choicesPersonToAssignNextTo() {
        if(requiredTransitionTypeIfAny == null) {
            return Collections.emptyList();
        }
        IPartyRoleType partyRoleType = peekPartyRoleType();
        return partyRoleTypeService.membersOf(partyRoleType);
    }

    private <T extends Enum<T> & IPartyRoleType> T peekPartyRoleType() {
        if(requiredTransitionTypeIfAny == null) {
            return null;
        }
        IPartyRoleType iPartyRoleType = stateTransitionService
                .peekTaskRoleAssignToAfter(domainObject, requiredTransitionTypeIfAny);
        return iPartyRoleType != null && Enum.class.isAssignableFrom(iPartyRoleType.getClass())
                ? (T) iPartyRoleType
                : null;
    }

    protected <T extends Enum<T> & IPartyRoleType> T enumPartyRoleType() {
        return peekPartyRoleType();
    }

    protected String enumPartyRoleTypeName() {
        final Enum roleType = enumPartyRoleType();
        return roleType != null ? roleType.name() : "";
    }

    /**
     * Subclasses must call, typically in their <tt>hideAct()</tt> guargs, in order to check whether {@link #trigger(String, String)}.
     */
    protected final boolean cannotTransition() {
        return !canTransition();
    }

    private boolean canTransition() {
        final S currentState = stateTransitionService.currentStateOf(getDomainObject(), stateTransitionClass);
        if(requiredTransitionTypeIfAny != null) {
            return requiredTransitionTypeIfAny.canTransitionFromStateAndIsMatch(
                                                        domainObject, currentState, serviceRegistry2);
        } else {
            return fromStates.contains(currentState);
        }
    }

    protected String reasonGuardNotSatisified() {
        if(requiredTransitionTypeIfAny != null) {
            return requiredTransitionTypeIfAny.reasonGuardNotSatisified(domainObject, serviceRegistry2);
        } else {
            ST st = stateTransitionService.pendingTransitionOf(domainObject, stateTransitionClass);
            if (st == null) {
                return null;
            }
            return st.getTransitionType().reasonGuardNotSatisified(domainObject, serviceRegistry2);
        }
    }

    @Inject
    protected StateTransitionService stateTransitionService;

    @Inject
    protected ServiceRegistry2 serviceRegistry2;

    @Inject
    protected PartyRoleTypeService partyRoleTypeService;

}

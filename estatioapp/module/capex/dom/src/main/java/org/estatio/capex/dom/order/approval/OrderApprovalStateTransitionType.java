package org.estatio.capex.dom.order.approval;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.order.Order;
import org.estatio.capex.dom.state.AdvancePolicy;
import org.estatio.capex.dom.state.NextTransitionSearchStrategy;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.TaskAssignmentStrategy;
import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.IPartyRoleType;

import lombok.Getter;

@Getter
public enum OrderApprovalStateTransitionType
        implements StateTransitionType<
        Order,
        OrderApprovalStateTransition,
        OrderApprovalStateTransitionType,
        OrderApprovalState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (OrderApprovalState)null,
            OrderApprovalState.NEW,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    COMPLETE_WITH_APPROVAL(
            OrderApprovalState.NEW,
            OrderApprovalState.APPROVED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.to(FixedAssetRoleTypeEnum.PROPERTY_MANAGER),
            AdvancePolicy.MANUAL),
    DISCARD(
            OrderApprovalState.NEW,
            OrderApprovalState.DISCARDED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL);

    private final List<OrderApprovalState> fromStates;
    private final OrderApprovalState toState;
    private final NextTransitionSearchStrategy nextTransitionSearchStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;
    private final AdvancePolicy advancePolicy;

    OrderApprovalStateTransitionType(
            final List<OrderApprovalState> fromState,
            final OrderApprovalState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.nextTransitionSearchStrategy = nextTransitionSearchStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
        this.advancePolicy = advancePolicy;
    }

    OrderApprovalStateTransitionType(
            final OrderApprovalState fromState,
            final OrderApprovalState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState, nextTransitionSearchStrategy,
                taskAssignmentStrategy,
                advancePolicy);
    }

    public static class TransitionEvent
            extends StateTransitionEvent<
            Order,
            OrderApprovalStateTransition,
            OrderApprovalStateTransitionType,
            OrderApprovalState> {
        public TransitionEvent(
                final Order domainObject,
                final OrderApprovalStateTransition stateTransitionIfAny,
                final OrderApprovalStateTransitionType transitionType) {
            super(domainObject, stateTransitionIfAny, transitionType);
        }
    }

    @Override
    public NextTransitionSearchStrategy getNextTransitionSearchStrategy() {
        return nextTransitionSearchStrategy;
    }

    @Override
    public AdvancePolicy advancePolicyFor(
            final Order domainObject, final ServiceRegistry2 serviceRegistry2) {
        return advancePolicy;
    }

    @Override
    public TransitionEvent newStateTransitionEvent(
            final Order domainObject,
            final OrderApprovalStateTransition pendingTransitionIfAny) {
        return new TransitionEvent(domainObject, pendingTransitionIfAny, this);
    }


    @Override
    public OrderApprovalStateTransition createTransition(
            final Order domainObject,
            final OrderApprovalState fromState,
            final IPartyRoleType assignToIfAny,
            final Person personToAssignToIfAny,
            final ServiceRegistry2 serviceRegistry2) {

        final OrderApprovalStateTransition.Repository repository =
                serviceRegistry2.lookupService(OrderApprovalStateTransition.Repository.class);

        final String taskDescription = Enums.getFriendlyNameOf(this);
        return repository.create(domainObject, this, fromState, assignToIfAny, personToAssignToIfAny, taskDescription);
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends StateTransitionServiceSupportAbstract<
            Order,
            OrderApprovalStateTransition,
            OrderApprovalStateTransitionType,
            OrderApprovalState> {

        public SupportService() {
            super(OrderApprovalStateTransitionType.class, OrderApprovalStateTransition.class
            );
        }

        @Override
        protected StateTransitionRepository<
                Order,
                OrderApprovalStateTransition,
                OrderApprovalStateTransitionType,
                OrderApprovalState
                > getRepository() {
            return repository;
        }

        @Inject
        OrderApprovalStateTransition.Repository repository;

    }

}


package org.estatio.capex.dom.payment.approval;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.payment.Payment;
import org.estatio.capex.dom.state.AdvancePolicy;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.state.NextTransitionSearchStrategy;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.TaskAssignmentStrategy;
import org.estatio.dom.party.PartyRoleTypeEnum;
import org.estatio.dom.party.role.IPartyRoleType;

import lombok.Getter;

@Getter
public enum PaymentApprovalStateTransitionType
        implements StateTransitionType<
                Payment,
                PaymentApprovalStateTransition,
                PaymentApprovalStateTransitionType,
                PaymentApprovalState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (PaymentApprovalState)null,
            PaymentApprovalState.NEW,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    APPROVE_AS_FINANCIAL_DIRECTOR(
            PaymentApprovalState.NEW,
            PaymentApprovalState.APPROVED_BY_FINANCIAL_DIRECTOR,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.FINANCIAL_DIRECTOR),
            AdvancePolicy.MANUAL);

    private final List<PaymentApprovalState> fromStates;
    private final PaymentApprovalState toState;
    private final NextTransitionSearchStrategy nextTransitionSearchStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;
    private final AdvancePolicy advancePolicy;

    PaymentApprovalStateTransitionType(
            final List<PaymentApprovalState> fromState,
            final PaymentApprovalState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.nextTransitionSearchStrategy = nextTransitionSearchStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
        this.advancePolicy = advancePolicy;
    }

    PaymentApprovalStateTransitionType(
            final PaymentApprovalState fromState,
            final PaymentApprovalState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState, nextTransitionSearchStrategy,
                taskAssignmentStrategy,
                advancePolicy);
    }

    public static class TransitionEvent
            extends StateTransitionEvent<
                        Payment,
                        PaymentApprovalStateTransition,
                        PaymentApprovalStateTransitionType,
                        PaymentApprovalState> {
        public TransitionEvent(
                final Payment domainObject,
                final PaymentApprovalStateTransition stateTransitionIfAny,
                final PaymentApprovalStateTransitionType transitionType) {
            super(domainObject, stateTransitionIfAny, transitionType);
        }
    }

    @Override
    public NextTransitionSearchStrategy getNextTransitionSearchStrategy() {
        return nextTransitionSearchStrategy;
    }

    @Override
    public AdvancePolicy advancePolicyFor(
            final Payment domainObject, final ServiceRegistry2 serviceRegistry2) {
        return advancePolicy;
    }

    @Override
    public TransitionEvent newStateTransitionEvent(
            final Payment domainObject,
            final PaymentApprovalStateTransition pendingTransitionIfAny) {
        return new TransitionEvent(domainObject, pendingTransitionIfAny, this);
    }


    @Override
    public PaymentApprovalStateTransition createTransition(
            final Payment domainObject,
            final PaymentApprovalState fromState,
            final IPartyRoleType assignToIfAny,
            final ServiceRegistry2 serviceRegistry2) {

        final PaymentApprovalStateTransition.Repository repository =
                serviceRegistry2.lookupService(PaymentApprovalStateTransition.Repository.class);

        final String taskDescription = Enums.getFriendlyNameOf(this);
        return repository.create(domainObject, this, fromState, assignToIfAny, taskDescription);
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends StateTransitionServiceSupportAbstract<
            Payment, PaymentApprovalStateTransition, PaymentApprovalStateTransitionType, PaymentApprovalState> {

        public SupportService() {
            super(PaymentApprovalStateTransitionType.class, PaymentApprovalStateTransition.class
            );
        }

        @Override
        protected StateTransitionRepository<
                Payment,
                PaymentApprovalStateTransition,
                PaymentApprovalStateTransitionType,
                PaymentApprovalState
                > getRepository() {
            return repository;
        }

        @Inject
        PaymentApprovalStateTransition.Repository repository;

    }

}


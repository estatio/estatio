package org.estatio.module.capex.dom.payment.approval;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.state.AdvancePolicy;
import org.estatio.module.capex.dom.state.NextTransitionSearchStrategy;
import org.estatio.module.capex.dom.state.StateTransitionEvent;
import org.estatio.module.capex.dom.state.StateTransitionRepository;
import org.estatio.module.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.module.capex.dom.state.StateTransitionType;
import org.estatio.module.capex.dom.state.TaskAssignmentStrategy;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;

import lombok.Getter;

@Getter
public enum PaymentBatchApprovalStateTransitionType
        implements StateTransitionType<
        PaymentBatch,
        PaymentBatchApprovalStateTransition,
        PaymentBatchApprovalStateTransitionType,
        PaymentBatchApprovalState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (PaymentBatchApprovalState)null,
            PaymentBatchApprovalState.NEW,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    COMPLETE(
            PaymentBatchApprovalState.NEW,
            PaymentBatchApprovalState.COMPLETED,
            NextTransitionSearchStrategy.none(),
            null, // task assignment strategy overridden below
            AdvancePolicy.MANUAL){
            @Override
            public TaskAssignmentStrategy getTaskAssignmentStrategy() {
                return (TaskAssignmentStrategy<
                        PaymentBatch,
                        PaymentBatchApprovalStateTransition,
                        PaymentBatchApprovalStateTransitionType,
                        PaymentBatchApprovalState>) (paymentBatch, serviceRegistry2) -> {
                    if (paymentBatch.getAtPath().startsWith("/ITA")) return null;
                    return PartyRoleTypeEnum.TREASURER;
                };
            }
        },
    CONFIRM_AUTHORISATION(
            PaymentBatchApprovalState.COMPLETED,
            PaymentBatchApprovalState.PAID,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    DISCARD(
            PaymentBatchApprovalState.COMPLETED,
            PaymentBatchApprovalState.DISCARDED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL);

    private final List<PaymentBatchApprovalState> fromStates;
    private final PaymentBatchApprovalState toState;
    private final NextTransitionSearchStrategy nextTransitionSearchStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;
    private final AdvancePolicy advancePolicy;

    PaymentBatchApprovalStateTransitionType(
            final List<PaymentBatchApprovalState> fromState,
            final PaymentBatchApprovalState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.nextTransitionSearchStrategy = nextTransitionSearchStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
        this.advancePolicy = advancePolicy;
    }

    PaymentBatchApprovalStateTransitionType(
            final PaymentBatchApprovalState fromState,
            final PaymentBatchApprovalState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState, nextTransitionSearchStrategy,
                taskAssignmentStrategy,
                advancePolicy);
    }

    public static class TransitionEvent
            extends StateTransitionEvent<
            PaymentBatch,
            PaymentBatchApprovalStateTransition,
            PaymentBatchApprovalStateTransitionType,
            PaymentBatchApprovalState> {
        public TransitionEvent(
                final PaymentBatch domainObject,
                final PaymentBatchApprovalStateTransition stateTransitionIfAny,
                final PaymentBatchApprovalStateTransitionType transitionType) {
            super(domainObject, stateTransitionIfAny, transitionType);
        }
    }

    @Override
    public NextTransitionSearchStrategy getNextTransitionSearchStrategy() {
        return nextTransitionSearchStrategy;
    }

    @Override
    public AdvancePolicy advancePolicyFor(
            final PaymentBatch domainObject, final ServiceRegistry2 serviceRegistry2) {
        return advancePolicy;
    }

    @Override
    public TransitionEvent newStateTransitionEvent(
            final PaymentBatch domainObject,
            final PaymentBatchApprovalStateTransition pendingTransitionIfAny) {
        return new TransitionEvent(domainObject, pendingTransitionIfAny, this);
    }


    @Override
    public PaymentBatchApprovalStateTransition createTransition(
            final PaymentBatch domainObject,
            final PaymentBatchApprovalState fromState,
            final IPartyRoleType assignToIfAny,
            final Person personToAssignToIfAny,
            final String taskDescriptionIfAny,
            final ServiceRegistry2 serviceRegistry2) {

        final PaymentBatchApprovalStateTransition.Repository repository =
                serviceRegistry2.lookupService(PaymentBatchApprovalStateTransition.Repository.class);

        final String taskDescription = Util.taskDescriptionUsing(taskDescriptionIfAny, this);

        return repository.create(domainObject, this, fromState, assignToIfAny, personToAssignToIfAny, taskDescription);
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends StateTransitionServiceSupportAbstract<
            PaymentBatch, PaymentBatchApprovalStateTransition, PaymentBatchApprovalStateTransitionType, PaymentBatchApprovalState> {

        public SupportService() {
            super(PaymentBatchApprovalStateTransitionType.class, PaymentBatchApprovalStateTransition.class
            );
        }

        @Override
        protected StateTransitionRepository<
                PaymentBatch,
                PaymentBatchApprovalStateTransition,
                PaymentBatchApprovalStateTransitionType,
                PaymentBatchApprovalState
                > getRepository() {
            return repository;
        }

        @Inject
        PaymentBatchApprovalStateTransition.Repository repository;

    }

}


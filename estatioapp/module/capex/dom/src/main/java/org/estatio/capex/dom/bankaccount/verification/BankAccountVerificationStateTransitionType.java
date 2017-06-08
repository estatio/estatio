package org.estatio.capex.dom.bankaccount.verification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.state.StateTransitionStrategy;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.TaskAssignmentStrategy;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum BankAccountVerificationStateTransitionType
        implements StateTransitionType<
            BankAccount,
            BankAccountVerificationStateTransition,
            BankAccountVerificationStateTransitionType,
            BankAccountVerificationState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (BankAccountVerificationState)null,
            BankAccountVerificationState.NOT_VERIFIED,
            StateTransitionStrategy.Util.none(), TaskAssignmentStrategy.Util.none()
            // don't automatically create pending transition to next state; this will be done only on request (by StateTransitionService#createPendingTransition)
    ),
    VERIFY_BANK_ACCOUNT(
            BankAccountVerificationState.NOT_VERIFIED,
            BankAccountVerificationState.VERIFIED,
            StateTransitionStrategy.Util.none(), TaskAssignmentStrategy.Util.to(EstatioRole.TREASURER)
    ),
    CANCEL(
            BankAccountVerificationState.NOT_VERIFIED,
            BankAccountVerificationState.CANCELLED,
            StateTransitionStrategy.Util.none(), TaskAssignmentStrategy.Util.none()
    ),
    RESET(
            Arrays.asList(
                    BankAccountVerificationState.NOT_VERIFIED,
                    BankAccountVerificationState.VERIFIED,
                    BankAccountVerificationState.CANCELLED
            ),
            BankAccountVerificationState.NOT_VERIFIED,
            StateTransitionStrategy.Util.next(), TaskAssignmentStrategy.Util.none()
    );

    private final List<BankAccountVerificationState> fromStates;
    private final BankAccountVerificationState toState;
    private final StateTransitionStrategy stateTransitionStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;

    BankAccountVerificationStateTransitionType(
            final List<BankAccountVerificationState> fromState,
            final BankAccountVerificationState toState,
            final StateTransitionStrategy stateTransitionStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.stateTransitionStrategy = stateTransitionStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
    }

    BankAccountVerificationStateTransitionType(
            final BankAccountVerificationState fromState,
            final BankAccountVerificationState toState,
            final StateTransitionStrategy stateTransitionStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState, stateTransitionStrategy,
                taskAssignmentStrategy
        );
    }

    public static class TransitionEvent
            extends StateTransitionEvent<
                        BankAccount,
                        BankAccountVerificationStateTransition,
                        BankAccountVerificationStateTransitionType,
                        BankAccountVerificationState> {
        public TransitionEvent(
                final BankAccount domainObject,
                final BankAccountVerificationStateTransition stateTransitionIfAny,
                final BankAccountVerificationStateTransitionType transitionType) {
            super(domainObject, stateTransitionIfAny, transitionType);
        }
    }

    @Override
    public StateTransitionStrategy getTransitionStrategy() {
        return stateTransitionStrategy;
    }

    @Override
    public TransitionEvent newStateTransitionEvent(
            final BankAccount domainObject,
            final BankAccountVerificationStateTransition pendingTransitionIfAny) {
        return new TransitionEvent(domainObject, pendingTransitionIfAny, this);
    }



    @Override
    public BankAccountVerificationStateTransition createTransition(
            final BankAccount domainObject,
            final BankAccountVerificationState fromState,
            final EstatioRole assignToIfAny,
            final ServiceRegistry2 serviceRegistry2) {

        final BankAccountVerificationStateTransition.Repository repository =
                serviceRegistry2.lookupService(BankAccountVerificationStateTransition.Repository.class);

        final String taskDescription = Enums.getFriendlyNameOf(this);
        return repository.create(domainObject, this, fromState, assignToIfAny, taskDescription);
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService
            extends StateTransitionServiceSupportAbstract<
                        BankAccount,
                        BankAccountVerificationStateTransition,
                        BankAccountVerificationStateTransitionType,
                        BankAccountVerificationState> {

        public SupportService() {
            super(BankAccountVerificationStateTransitionType.class, BankAccountVerificationStateTransition.class
            );
        }

        @Override
        protected StateTransitionRepository<
                BankAccount,
                BankAccountVerificationStateTransition,
                BankAccountVerificationStateTransitionType,
                BankAccountVerificationState
                > getRepository() {
            return repository;
        }

        @Inject
        BankAccountVerificationStateTransition.Repository repository;

    }

}


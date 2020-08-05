package org.estatio.module.capex.dom.invoice.accountingaudit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeEnum;
import org.estatio.module.task.dom.state.AdvancePolicy;
import org.estatio.module.task.dom.state.NextTransitionSearchStrategy;
import org.estatio.module.task.dom.state.StateTransitionEvent;
import org.estatio.module.task.dom.state.StateTransitionRepository;
import org.estatio.module.task.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.module.task.dom.state.StateTransitionType;
import org.estatio.module.task.dom.state.TaskAssignmentStrategy;

import lombok.Getter;

@Getter
public enum IncomingInvoiceAccountingStateTransitionType
        implements StateTransitionType<
        IncomingInvoice,
        IncomingInvoiceAccountingStateTransition,
        IncomingInvoiceAccountingStateTransitionType,
        IncomingInvoiceAccountingState> {

    INSTANTIATE(
            (IncomingInvoiceAccountingState) null,
            IncomingInvoiceAccountingState.NEW,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    INVOICE_COMPLETED(
            IncomingInvoiceAccountingState.NEW,
            IncomingInvoiceAccountingState.AUDITABLE,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    AUDIT(
            Arrays.asList(IncomingInvoiceAccountingState.AUDITABLE, IncomingInvoiceAccountingState.ESCALATED),
            IncomingInvoiceAccountingState.AUDITED,
            NextTransitionSearchStrategy.firstMatching(),
            null,
            AdvancePolicy.MANUAL){
        @Override
        public TaskAssignmentStrategy getTaskAssignmentStrategy() {
            return (TaskAssignmentStrategy<
                    IncomingInvoice,
                    IncomingInvoiceAccountingStateTransition,
                    IncomingInvoiceAccountingStateTransitionType,
                    IncomingInvoiceAccountingState>) (incomingInvoice, serviceRegistry2) -> Arrays.asList(PartyRoleTypeEnum.AUDITOR_ACCOUNTANT);
        }
    },
    ESCALATE(
            IncomingInvoiceAccountingState.AUDITABLE,
            IncomingInvoiceAccountingState.ESCALATED,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    ;

    private final List<IncomingInvoiceAccountingState> fromStates;
    private final IncomingInvoiceAccountingState toState;
    private final NextTransitionSearchStrategy nextTransitionSearchStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;
    private final AdvancePolicy advancePolicy;

    IncomingInvoiceAccountingStateTransitionType(
            final List<IncomingInvoiceAccountingState> fromState,
            final IncomingInvoiceAccountingState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.nextTransitionSearchStrategy = nextTransitionSearchStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
        this.advancePolicy = advancePolicy;
    }

    IncomingInvoiceAccountingStateTransitionType(
            final IncomingInvoiceAccountingState fromState,
            final IncomingInvoiceAccountingState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this(fromState != null
                        ? Collections.singletonList(fromState)
                        : null,
                toState, nextTransitionSearchStrategy, taskAssignmentStrategy,
                advancePolicy);
    }

    public static class TransitionEvent
            extends StateTransitionEvent<
            IncomingInvoice,
            IncomingInvoiceAccountingStateTransition,
            IncomingInvoiceAccountingStateTransitionType,
            IncomingInvoiceAccountingState> {
        public TransitionEvent(
                final IncomingInvoice domainObject,
                final IncomingInvoiceAccountingStateTransition stateTransitionIfAny,
                final IncomingInvoiceAccountingStateTransitionType transitionType) {
            super(domainObject, stateTransitionIfAny, transitionType);
        }
    }

    @Override
    public NextTransitionSearchStrategy getNextTransitionSearchStrategy() {
        return nextTransitionSearchStrategy;
    }

    @Override
    public TransitionEvent newStateTransitionEvent(
            final IncomingInvoice domainObject,
            final IncomingInvoiceAccountingStateTransition transitionIfAny) {
        return new TransitionEvent(domainObject, transitionIfAny, this);
    }

    @Override
    public AdvancePolicy advancePolicyFor(
            final IncomingInvoice domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        return advancePolicy;
    }

    @Override
    public IncomingInvoiceAccountingStateTransition createTransition(
            final IncomingInvoice domainObject,
            final IncomingInvoiceAccountingState fromState,
            final IPartyRoleType assignToIfAny,
            final Person personToAssignToIfAny,
            final String taskDescriptionIfAny,
            final ServiceRegistry2 serviceRegistry2) {

        final IncomingInvoiceAccountingStateTransition.Repository repository =
                serviceRegistry2.lookupService(IncomingInvoiceAccountingStateTransition.Repository.class);

        final String taskDescription = Util.taskDescriptionUsing(taskDescriptionIfAny, this);

        return repository.create(domainObject, this, fromState, assignToIfAny, personToAssignToIfAny, taskDescription);
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends StateTransitionServiceSupportAbstract<
            IncomingInvoice, IncomingInvoiceAccountingStateTransition, IncomingInvoiceAccountingStateTransitionType, IncomingInvoiceAccountingState> {

        public SupportService() {
            super(IncomingInvoiceAccountingStateTransitionType.class, IncomingInvoiceAccountingStateTransition.class
            );
        }

        @Override
        protected StateTransitionRepository<
                IncomingInvoice,
                IncomingInvoiceAccountingStateTransition,
                IncomingInvoiceAccountingStateTransitionType,
                IncomingInvoiceAccountingState
                > getRepository() {
            return repository;
        }

        @Inject
        IncomingInvoiceAccountingStateTransition.Repository repository;

    }
}


package org.estatio.module.financial.dom.bankaccount.verification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
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
public enum BankAccountVerificationStateTransitionType
        implements StateTransitionType<
        BankAccount,
        BankAccountVerificationStateTransition,
        BankAccountVerificationStateTransitionType,
        BankAccountVerificationState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (BankAccountVerificationState) null,
            BankAccountVerificationState.NOT_VERIFIED,
            NextTransitionSearchStrategy.none(), TaskAssignmentStrategy.none(),
            // don't automatically create pending transition to next state; this will be done only on request (by StateTransitionService#createPendingTransition)
            AdvancePolicy.MANUAL),
    VERIFY_BANK_ACCOUNT(
            BankAccountVerificationState.NOT_VERIFIED,
            BankAccountVerificationState.VERIFIED,
            NextTransitionSearchStrategy.none(),
            null,  // task assignment strategy overridden below
            AdvancePolicy.MANUAL) {
        @Override
        public TaskAssignmentStrategy getTaskAssignmentStrategy() {
            return (TaskAssignmentStrategy<
                    BankAccount,
                    BankAccountVerificationStateTransition,
                    BankAccountVerificationStateTransitionType,
                    BankAccountVerificationState>) (bankAccount, serviceRegistry2) -> {
                if (bankAccount.getAtPath().startsWith("/ITA")) {
                    return null;
                }
                return Collections.singletonList(PartyRoleTypeEnum.TREASURER);
            };
        }

        @Override
        public String reasonGuardNotSatisified(final BankAccount bankAccount, final ServiceRegistry2 serviceRegistry2) {
            Party owner = bankAccount.getOwner();
            return !bankAccount.getAtPath().startsWith("/ITA") && owner instanceof Organisation && ((Organisation) owner).getChamberOfCommerceCode() == null ?
                    "Can not verify bank account because owner is missing chamber of commerce code" :
                    null;
        }
    },
    RESET(
            BankAccountVerificationState.VERIFIED,
            BankAccountVerificationState.NOT_VERIFIED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    REJECT_PROOF(
            BankAccountVerificationState.NOT_VERIFIED,
            BankAccountVerificationState.AWAITING_PROOF,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    PROOF_UPDATED(
            Arrays.asList(BankAccountVerificationState.AWAITING_PROOF, BankAccountVerificationState.DISCARDED),
            BankAccountVerificationState.NOT_VERIFIED,
            NextTransitionSearchStrategy.firstMatching(),
            null,
            AdvancePolicy.MANUAL) {
        @Override
        public TaskAssignmentStrategy getTaskAssignmentStrategy() {
            return (TaskAssignmentStrategy<
                    BankAccount,
                    BankAccountVerificationStateTransition,
                    BankAccountVerificationStateTransitionType,
                    BankAccountVerificationState>) (bankAccount, serviceRegistry2) -> {
                if (!bankAccount.getAtPath().startsWith("/ITA")) {
                    final BankAccountVerificationStateTransition.IncomingInvoiceRepository repository =
                            serviceRegistry2.lookupService(BankAccountVerificationStateTransition.IncomingInvoiceRepository.class);
                    List<IncomingInvoice> invoices = repository.findByApprovalStateAndBankAccount(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK, bankAccount);

                    if (!invoices.isEmpty()) {
                        for (IncomingInvoice invoice : invoices) {
                            if (!invoice.getType().equals(IncomingInvoiceType.CORPORATE_EXPENSES)) {
                                return Collections.singletonList(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER);
                            }
                        }
                        return Collections.singletonList(PartyRoleTypeEnum.CORPORATE_ADMINISTRATOR);
                    } else {
                        return Collections.singletonList(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER);
                    }
                }

                return Collections.singletonList(PartyRoleTypeEnum.INCOMING_INVOICE_MANAGER);
            };
        }

    },
    DISCARD(
            Arrays.asList(BankAccountVerificationState.NOT_VERIFIED, BankAccountVerificationState.AWAITING_PROOF),
            BankAccountVerificationState.DISCARDED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL);

    private final List<BankAccountVerificationState> fromStates;
    private final BankAccountVerificationState toState;
    private final NextTransitionSearchStrategy nextTransitionSearchStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;
    private final AdvancePolicy advancePolicy;

    BankAccountVerificationStateTransitionType(
            final List<BankAccountVerificationState> fromState,
            final BankAccountVerificationState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.nextTransitionSearchStrategy = nextTransitionSearchStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
        this.advancePolicy = advancePolicy;
    }

    BankAccountVerificationStateTransitionType(
            final BankAccountVerificationState fromState,
            final BankAccountVerificationState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this(fromState != null ? Collections.singletonList(fromState) : null, toState, nextTransitionSearchStrategy,
                taskAssignmentStrategy,
                advancePolicy);
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
    public NextTransitionSearchStrategy getNextTransitionSearchStrategy() {
        return nextTransitionSearchStrategy;
    }

    @Override
    public TransitionEvent newStateTransitionEvent(
            final BankAccount domainObject,
            final BankAccountVerificationStateTransition pendingTransitionIfAny) {
        return new TransitionEvent(domainObject, pendingTransitionIfAny, this);
    }

    @Override
    public AdvancePolicy advancePolicyFor(
            final BankAccount domainObject, final ServiceRegistry2 serviceRegistry2) {
        return advancePolicy;
    }

    @Override
    public BankAccountVerificationStateTransition createTransition(
            final BankAccount domainObject,
            final BankAccountVerificationState fromState,
            final IPartyRoleType assignToIfAny,
            final Person personToAssignToIfAny,
            final String taskDescriptionIfAny,
            final ServiceRegistry2 serviceRegistry2) {

        final BankAccountVerificationStateTransition.Repository repository =
                serviceRegistry2.lookupService(BankAccountVerificationStateTransition.Repository.class);

        final String taskDescription = Util.taskDescriptionUsing(taskDescriptionIfAny, this);
        return repository.create(domainObject, this, fromState, assignToIfAny, personToAssignToIfAny, taskDescription);
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


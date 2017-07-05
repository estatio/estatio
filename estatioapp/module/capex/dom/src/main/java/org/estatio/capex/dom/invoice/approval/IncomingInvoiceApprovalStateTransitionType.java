package org.estatio.capex.dom.invoice.approval;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.bankaccount.documents.BankAccount_attachVerificationProof;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.project.ProjectRoleTypeEnum;
import org.estatio.capex.dom.state.AdvancePolicy;
import org.estatio.capex.dom.state.NextTransitionSearchStrategy;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.TaskAssignmentStrategy;
import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.PartyRoleTypeEnum;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.IPartyRoleType;

import lombok.Getter;

@Getter
public enum IncomingInvoiceApprovalStateTransitionType
        implements StateTransitionType<
                                        IncomingInvoice,
                IncomingInvoiceApprovalStateTransition,
                IncomingInvoiceApprovalStateTransitionType,
                IncomingInvoiceApprovalState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (IncomingInvoiceApprovalState)null,
            IncomingInvoiceApprovalState.NEW,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    COMPLETE(
            IncomingInvoiceApprovalState.NEW,
            IncomingInvoiceApprovalState.COMPLETED,
            NextTransitionSearchStrategy.firstMatching(),
            null, // task assignment strategy overridden below
            AdvancePolicy.MANUAL) {
        @Override
        public TaskAssignmentStrategy getTaskAssignmentStrategy() {
            return (TaskAssignmentStrategy<
                        IncomingInvoice,
                        IncomingInvoiceApprovalStateTransition,
                        IncomingInvoiceApprovalStateTransitionType,
                        IncomingInvoiceApprovalState>) (incomingInvoice, serviceRegistry2) -> {

                final boolean hasProperty = incomingInvoice.getProperty() != null;
                if (hasProperty) {
                    return FixedAssetRoleTypeEnum.PROPERTY_MANAGER;
                }

                switch (incomingInvoice.getType()) {
                case CAPEX:
                case PROPERTY_EXPENSES:
                case SERVICE_CHARGES:
                    // this case should not be hit, because the upstream document categorisation process
                    // should have also set a property in this case, so the previous check would have been satisfied
                    // just adding this case in the switch stmt "for completeness"
                    return FixedAssetRoleTypeEnum.PROPERTY_MANAGER;
                case LOCAL_EXPENSES:
                    return PartyRoleTypeEnum.OFFICE_ADMINISTRATOR;
                case CORPORATE_EXPENSES:
                    return PartyRoleTypeEnum.CORPORATE_ADMINISTRATOR;
                }
                // REVIEW: for other types, we haven't yet established a business process, so no task will be created
                return null;
            };
        }
        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return getTaskAssignmentStrategy().getAssignTo(domainObject, serviceRegistry2) != null;
        }

        @Override
        public String reasonGuardNotSatisified(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return incomingInvoice.reasonIncomplete();
        }
    },
    APPROVE(
            IncomingInvoiceApprovalState.COMPLETED,
            IncomingInvoiceApprovalState.APPROVED,
            NextTransitionSearchStrategy.firstMatching(),
            null, // task assignment strategy overridden below
            AdvancePolicy.MANUAL) {
        @Override
        public TaskAssignmentStrategy getTaskAssignmentStrategy() {
            return (TaskAssignmentStrategy<
                    IncomingInvoice,
                    IncomingInvoiceApprovalStateTransition,
                    IncomingInvoiceApprovalStateTransitionType,
                    IncomingInvoiceApprovalState>) (incomingInvoice, serviceRegistry2) -> {
                switch (incomingInvoice.getType()) {
                case CAPEX:
                    return ProjectRoleTypeEnum.PROJECT_MANAGER;
                case PROPERTY_EXPENSES:
                case SERVICE_CHARGES:
                    return FixedAssetRoleTypeEnum.ASSET_MANAGER;
                }
                return null;
            };
        }
        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return getTaskAssignmentStrategy().getAssignTo(domainObject, serviceRegistry2) != null;
        }
    },
    APPROVE_LOCAL_AS_COUNTRY_DIRECTOR(
            IncomingInvoiceApprovalState.COMPLETED,
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.COUNTRY_DIRECTOR),
            AdvancePolicy.MANUAL) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return incomingInvoice.getType() == IncomingInvoiceType.LOCAL_EXPENSES;
        }
    },
    CHECK_BANK_ACCOUNT_FOR_CORPORATE(
            IncomingInvoiceApprovalState.COMPLETED,
            IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isMatch(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return incomingInvoice.getType() == IncomingInvoiceType.CORPORATE_EXPENSES;
        }

        @Override
        public void applyTo(
                final IncomingInvoice incomingInvoice,
                final Class<IncomingInvoiceApprovalStateTransition> stateTransitionClass,
                final ServiceRegistry2 serviceRegistry2) {
            CHECK_BANK_ACCOUNT.applyTo(incomingInvoice, stateTransitionClass, serviceRegistry2);
        }
    },
    APPROVE_AS_COUNTRY_DIRECTOR(
            IncomingInvoiceApprovalState.APPROVED,
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.COUNTRY_DIRECTOR),
            AdvancePolicy.MANUAL),
    CHECK_BANK_ACCOUNT(
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
            IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public void applyTo(
                final IncomingInvoice incomingInvoice,
                final Class<IncomingInvoiceApprovalStateTransition> stateTransitionClass,
                final ServiceRegistry2 serviceRegistry2) {
            super.applyTo(incomingInvoice, stateTransitionClass, serviceRegistry2);
            if(CONFIRM_BANK_ACCOUNT_VERIFIED.isGuardSatisified(incomingInvoice, serviceRegistry2)) {
                return;
            }

            final BankAccount bankAccount = triggerBankVerificationState(incomingInvoice, serviceRegistry2);
            attachDocumentAsPossibleIbanProof(incomingInvoice, bankAccount, serviceRegistry2);
        }

        private BankAccount triggerBankVerificationState(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            final StateTransitionService stateTransitionService =
                    serviceRegistry2.lookupService(StateTransitionService.class);

            final BankAccount bankAccount = incomingInvoice.getBankAccount();
            if (bankAccount != null) {
                if(stateTransitionService.currentStateOf(bankAccount, BankAccountVerificationStateTransition.class) == null) {
                    stateTransitionService
                            .trigger(bankAccount, BankAccountVerificationStateTransitionType.INSTANTIATE, null);
                }
                // re-evaluate the state machine.
                stateTransitionService
                        .trigger(bankAccount, BankAccountVerificationStateTransition.class, null, null);
            }
            return bankAccount;
        }

        private void attachDocumentAsPossibleIbanProof(
                final IncomingInvoice incomingInvoice,
                final BankAccount bankAccount, final ServiceRegistry2 serviceRegistry2) {
            final LookupAttachedPdfService lookupAttachedPdfService =
                    serviceRegistry2.lookupService(LookupAttachedPdfService.class);

            final PaperclipRepository paperclipRepository = serviceRegistry2
                    .lookupService(PaperclipRepository.class);

            final Optional<Document> documentIfAny =
                    lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(incomingInvoice);
            if (documentIfAny.isPresent()) {
                final Document document = documentIfAny.get();
                paperclipRepository.attach(document, BankAccount_attachVerificationProof.ROLE_NAME_FOR_IBAN_PROOF, bankAccount);
            }
        }

    },
    CONFIRM_BANK_ACCOUNT_VERIFIED(
            IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK,
            IncomingInvoiceApprovalState.PAYABLE,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isGuardSatisified(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            return isBankAccountVerified(incomingInvoice, serviceRegistry2) ||
                   incomingInvoice.getPaymentMethod() == PaymentMethod.DIRECT_DEBIT;
        }

        private boolean isBankAccountVerified(
                final IncomingInvoice incomingInvoice,
                final ServiceRegistry2 serviceRegistry2) {
            final StateTransitionService stateTransitionService =
                    serviceRegistry2.lookupService(StateTransitionService.class);

            final BankAccount bankAccount = incomingInvoice.getBankAccount();

            BankAccountVerificationState state = stateTransitionService
                    .currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);

            return state == BankAccountVerificationState.VERIFIED;
        }
    },
    PAY_BY_IBP(
            IncomingInvoiceApprovalState.PAYABLE,
            IncomingInvoiceApprovalState.PAID,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    PAY_BY_DD(
            IncomingInvoiceApprovalState.PAYABLE,
            IncomingInvoiceApprovalState.PAID,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    DISCARD(
            IncomingInvoiceApprovalState.NEW,
            IncomingInvoiceApprovalState.DISCARDED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    ;

    private final List<IncomingInvoiceApprovalState> fromStates;
    private final IncomingInvoiceApprovalState toState;
    private final NextTransitionSearchStrategy nextTransitionSearchStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;
    private final AdvancePolicy advancePolicy;

    IncomingInvoiceApprovalStateTransitionType(
            final List<IncomingInvoiceApprovalState> fromState,
            final IncomingInvoiceApprovalState toState,
            final NextTransitionSearchStrategy nextTransitionSearchStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy,
            final AdvancePolicy advancePolicy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.nextTransitionSearchStrategy = nextTransitionSearchStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
        this.advancePolicy = advancePolicy;
    }

    IncomingInvoiceApprovalStateTransitionType(
            final IncomingInvoiceApprovalState fromState,
            final IncomingInvoiceApprovalState toState,
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
                        IncomingInvoiceApprovalStateTransition,
                        IncomingInvoiceApprovalStateTransitionType,
                        IncomingInvoiceApprovalState> {
        public TransitionEvent(
                final IncomingInvoice domainObject,
                final IncomingInvoiceApprovalStateTransition stateTransitionIfAny,
                final IncomingInvoiceApprovalStateTransitionType transitionType) {
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
            final IncomingInvoiceApprovalStateTransition transitionIfAny) {
        return new TransitionEvent(domainObject, transitionIfAny, this);
    }

    @Override
    public AdvancePolicy advancePolicyFor(
            final IncomingInvoice domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        return advancePolicy;
    }

    @Override
    public IncomingInvoiceApprovalStateTransition createTransition(
            final IncomingInvoice domainObject,
            final IncomingInvoiceApprovalState fromState,
            final IPartyRoleType assignToIfAny,
            final Person personToAssignToIfAny,
            final ServiceRegistry2 serviceRegistry2) {

        final IncomingInvoiceApprovalStateTransition.Repository repository =
                serviceRegistry2.lookupService(IncomingInvoiceApprovalStateTransition.Repository.class);

        final String taskDescription = Enums.getFriendlyNameOf(this);
        return repository.create(domainObject, this, fromState, assignToIfAny, personToAssignToIfAny, taskDescription);
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends StateTransitionServiceSupportAbstract<
            IncomingInvoice, IncomingInvoiceApprovalStateTransition, IncomingInvoiceApprovalStateTransitionType, IncomingInvoiceApprovalState> {

        public SupportService() {
            super(IncomingInvoiceApprovalStateTransitionType.class, IncomingInvoiceApprovalStateTransition.class
            );
        }

        @Override
        protected StateTransitionRepository<
                IncomingInvoice,
                IncomingInvoiceApprovalStateTransition,
                IncomingInvoiceApprovalStateTransitionType,
                IncomingInvoiceApprovalState
                > getRepository() {
            return repository;
        }

        @Inject
        IncomingInvoiceApprovalStateTransition.Repository repository;

    }

}


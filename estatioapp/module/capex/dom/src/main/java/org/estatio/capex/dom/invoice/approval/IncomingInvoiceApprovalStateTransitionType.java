package org.estatio.capex.dom.invoice.approval;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.project.ProjectRoleTypeEnum;
import org.estatio.capex.dom.state.AdvancePolicy;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.state.NextTransitionSearchStrategy;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.TaskAssignmentStrategy;
import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.PartyRoleTypeEnum;
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
            NextTransitionSearchStrategy.firstMatching(), TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL),
    COMPLETE_CLASSIFICATION(
            IncomingInvoiceApprovalState.NEW,
            IncomingInvoiceApprovalState.CLASSIFIED,
            NextTransitionSearchStrategy.firstMatching(), TaskAssignmentStrategy.to(FixedAssetRoleTypeEnum.PROPERTY_MANAGER),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public String reasonGuardNotSatisified(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return domainObject.reasonClassificationInComplete();
        }
    },
    APPROVE_AS_PROJECT_MANAGER(
            IncomingInvoiceApprovalState.CLASSIFIED,
            IncomingInvoiceApprovalState.APPROVED_BY_PROJECT_MANAGER,
            NextTransitionSearchStrategy.firstMatching(), TaskAssignmentStrategy.to(ProjectRoleTypeEnum.PROJECT_MANAGER),
            AdvancePolicy.MANUAL) {
        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject,
                final ServiceRegistry2 serviceRegistry2) {
            return domainObject.hasProject();
        }
    },
    APPROVE_AS_ASSET_MANAGER(
            IncomingInvoiceApprovalState.CLASSIFIED,
            IncomingInvoiceApprovalState.APPROVED_BY_ASSET_MANAGER,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.to(FixedAssetRoleTypeEnum.ASSET_MANAGER),
            AdvancePolicy.MANUAL) {
        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject,
                final ServiceRegistry2 serviceRegistry2) {
            return !APPROVE_AS_PROJECT_MANAGER.isMatch(domainObject, serviceRegistry2);
            //return domainObject.hasServiceCharges();
        }
    },
    APPROVE_AS_COUNTRY_ADMINISTRATOR(
            IncomingInvoiceApprovalState.CLASSIFIED,
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_ADMINISTRATOR,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.COUNTRY_ADMINISTRATOR),
            AdvancePolicy.MANUAL) {
        @Override
        public boolean isMatch(
                final IncomingInvoice domainObject,
                final ServiceRegistry2 serviceRegistry2) {
            // ie else the above two routes
            return !APPROVE_AS_PROJECT_MANAGER.isMatch(domainObject, serviceRegistry2) && !APPROVE_AS_ASSET_MANAGER.isMatch(domainObject, serviceRegistry2);
        }
    },
    APPROVE_AS_COUNTRY_DIRECTOR(
            Arrays.asList(
                    IncomingInvoiceApprovalState.APPROVED_BY_PROJECT_MANAGER,
                    IncomingInvoiceApprovalState.APPROVED_BY_ASSET_MANAGER),
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
            NextTransitionSearchStrategy.firstMatching(),
            TaskAssignmentStrategy.to(PartyRoleTypeEnum.COUNTRY_DIRECTOR),
            AdvancePolicy.MANUAL),
    CHECK_BANK_ACCOUNT(
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
            IncomingInvoiceApprovalState.PAYABLE,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.AUTOMATIC) {
        @Override
        public boolean isGuardSatisified(
                final IncomingInvoice domainObject,
                final ServiceRegistry2 serviceRegistry2) {
            final StateTransitionService stateTransitionService =
                    serviceRegistry2.lookupService(StateTransitionService.class);

            final BankAccount bankAccount = domainObject.getBankAccount();

            BankAccountVerificationState state = stateTransitionService
                    .currentStateOf(bankAccount, BankAccountVerificationStateTransition.class);

            return state == BankAccountVerificationState.VERIFIED;
        }
    },
    CANCEL(
            Arrays.asList(
                    IncomingInvoiceApprovalState.CLASSIFIED,
                    IncomingInvoiceApprovalState.APPROVED_BY_PROJECT_MANAGER,
                    IncomingInvoiceApprovalState.APPROVED_BY_ASSET_MANAGER),
            IncomingInvoiceApprovalState.CANCELLED,
            NextTransitionSearchStrategy.none(),
            TaskAssignmentStrategy.none(),
            AdvancePolicy.MANUAL);

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
            final ServiceRegistry2 serviceRegistry2) {

        final IncomingInvoiceApprovalStateTransition.Repository repository =
                serviceRegistry2.lookupService(IncomingInvoiceApprovalStateTransition.Repository.class);

        final String taskDescription = Enums.getFriendlyNameOf(this);
        return repository.create(domainObject, this, fromState, assignToIfAny, taskDescription);
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


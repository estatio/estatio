package org.estatio.capex.dom.invoice.approval;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.project.ProjectRoleTypeEnum;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.state.StateTransitionStrategy;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.TaskAssignmentStrategy;
import org.estatio.dom.asset.role.FixedAssetRoleTypeEnum;
import org.estatio.dom.party.relationship.PartyRelationshipTypeEnum;
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
            StateTransitionStrategy.Util.next(), TaskAssignmentStrategy.Util.none()
    ),
    COMPLETE_CLASSIFICATION(
            IncomingInvoiceApprovalState.NEW,
            IncomingInvoiceApprovalState.CLASSIFIED,
            StateTransitionStrategy.Util.next(), TaskAssignmentStrategy.Util.to(FixedAssetRoleTypeEnum.PROPERTY_MANAGER)
    ) {
        @Override
        public boolean isGuardSatisified(
                final IncomingInvoice domainObject,
                final ServiceRegistry2 serviceRegistry2) {
            return domainObject.classificationComplete();
        }
    },
    APPROVE_AS_PROJECT_MANAGER(
            IncomingInvoiceApprovalState.CLASSIFIED,
            IncomingInvoiceApprovalState.APPROVED_BY_PROJECT_MANAGER,
            StateTransitionStrategy.Util.next(), TaskAssignmentStrategy.Util.to(ProjectRoleTypeEnum.PROJECT_MANAGER)
    ) {
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
            StateTransitionStrategy.Util.next(),
            TaskAssignmentStrategy.Util.to(FixedAssetRoleTypeEnum.ASSET_MANAGER)
    ) {
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
            StateTransitionStrategy.Util.next(),
            TaskAssignmentStrategy.Util.to(PartyRelationshipTypeEnum.COUNTRY_ADMINISTRATOR)
    ) {
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
            StateTransitionStrategy.Util.next(),
            TaskAssignmentStrategy.Util.to(PartyRelationshipTypeEnum.COUNTRY_DIRECTOR)
    ),
    CHECK_BANK_ACCOUNT(
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
            IncomingInvoiceApprovalState.PAYABLE,
            StateTransitionStrategy.Util.none(),
            TaskAssignmentStrategy.Util.none()
    ),
    CANCEL(
            Arrays.asList(
                    IncomingInvoiceApprovalState.CLASSIFIED,
                    IncomingInvoiceApprovalState.APPROVED_BY_PROJECT_MANAGER,
                    IncomingInvoiceApprovalState.APPROVED_BY_ASSET_MANAGER),
            IncomingInvoiceApprovalState.CANCELLED,
            StateTransitionStrategy.Util.none(),
            TaskAssignmentStrategy.Util.none()
    );

    private final List<IncomingInvoiceApprovalState> fromStates;
    private final IncomingInvoiceApprovalState toState;
    private final StateTransitionStrategy stateTransitionStrategy;
    private final TaskAssignmentStrategy taskAssignmentStrategy;

    IncomingInvoiceApprovalStateTransitionType(
            final List<IncomingInvoiceApprovalState> fromState,
            final IncomingInvoiceApprovalState toState,
            final StateTransitionStrategy stateTransitionStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy) {
        this.fromStates = fromState;
        this.toState = toState;
        this.stateTransitionStrategy = stateTransitionStrategy;
        this.taskAssignmentStrategy = taskAssignmentStrategy;
    }

    IncomingInvoiceApprovalStateTransitionType(
            final IncomingInvoiceApprovalState fromState,
            final IncomingInvoiceApprovalState toState,
            final StateTransitionStrategy stateTransitionStrategy,
            final TaskAssignmentStrategy taskAssignmentStrategy) {
        this(fromState != null
                        ? Collections.singletonList(fromState)
                        : null,
                toState, stateTransitionStrategy, taskAssignmentStrategy
        );
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
    public StateTransitionStrategy getTransitionStrategy() {
        return stateTransitionStrategy;
    }

    @Override
    public TransitionEvent newStateTransitionEvent(
            final IncomingInvoice domainObject,
            final IncomingInvoiceApprovalStateTransition transitionIfAny) {
        return new TransitionEvent(domainObject, transitionIfAny, this);
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


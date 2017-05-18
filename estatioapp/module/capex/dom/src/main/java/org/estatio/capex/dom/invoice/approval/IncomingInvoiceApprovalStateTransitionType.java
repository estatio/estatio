package org.estatio.capex.dom.invoice.approval;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

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
            IncomingInvoiceApprovalState.NEW
    ),
    APPROVE_AS_PROJECT_MANAGER(
            IncomingInvoiceApprovalState.NEW,
            IncomingInvoiceApprovalState.APPROVED_BY_PROJECT_MANAGER
    ) {

        @Override
        public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
            return EstatioRole.PROJECT_MANAGER;
        }

        @Override
        public boolean canApply(
                final IncomingInvoice domainObject,
                final ServiceRegistry2 serviceRegistry2) {
            return domainObject.hasProject();
        }

    },
    APPROVE_AS_ASSET_MANAGER(
            IncomingInvoiceApprovalState.NEW,
            IncomingInvoiceApprovalState.APPROVED_BY_ASSET_MANAGER
    ) {

        @Override
        public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
            return EstatioRole.ASSET_MANAGER;
        }

        @Override
        public boolean canApply(
                final IncomingInvoice domainObject,
                final ServiceRegistry2 serviceRegistry2) {
            return !domainObject.hasProject() && domainObject.hasFixedAsset();
        }
    },
    APPROVE_AS_COUNTRY_DIRECTOR(
            Arrays.asList(
                    IncomingInvoiceApprovalState.APPROVED_BY_PROJECT_MANAGER,
                    IncomingInvoiceApprovalState.APPROVED_BY_ASSET_MANAGER),
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR
    ) {
        @Override
        public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
            return EstatioRole.COUNTRY_DIRECTOR;
        }
    },
    APPROVE_AS_TREASURER(
            IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
            IncomingInvoiceApprovalState.APPROVED_BY_TREASURER
    ) {
        @Override
        public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
            return EstatioRole.TREASURER;
        }
    },
    PAY(
            IncomingInvoiceApprovalState.APPROVED_BY_TREASURER,
            IncomingInvoiceApprovalState.PAID
    ) {
        @Override
        public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
            return EstatioRole.TREASURER;
        }
    },
    CANCEL(
            Arrays.asList(
                    IncomingInvoiceApprovalState.NEW,
                    IncomingInvoiceApprovalState.APPROVED_BY_PROJECT_MANAGER,
                    IncomingInvoiceApprovalState.APPROVED_BY_ASSET_MANAGER,
                    IncomingInvoiceApprovalState.APPROVED_BY_COUNTRY_DIRECTOR,
                    IncomingInvoiceApprovalState.APPROVED_BY_TREASURER),
            IncomingInvoiceApprovalState.CANCELLED
    );

    private final List<IncomingInvoiceApprovalState> fromStates;
    private final IncomingInvoiceApprovalState toState;

    IncomingInvoiceApprovalStateTransitionType(
            final List<IncomingInvoiceApprovalState> fromState,
            final IncomingInvoiceApprovalState toState) {
        this.fromStates = fromState;
        this.toState = toState;
    }

    IncomingInvoiceApprovalStateTransitionType(
            final IncomingInvoiceApprovalState fromState,
            final IncomingInvoiceApprovalState toState) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState);
    }

    public static class IncomingInvoiceApprovalTransitionEvent
            extends StateTransitionEvent<
                        IncomingInvoice,
                        IncomingInvoiceApprovalStateTransition,
                        IncomingInvoiceApprovalStateTransitionType,
                        IncomingInvoiceApprovalState> {
        public IncomingInvoiceApprovalTransitionEvent(
                final IncomingInvoice domainObject,
                final IncomingInvoiceApprovalStateTransition stateTransitionIfAny,
                final IncomingInvoiceApprovalStateTransitionType transitionType) {
            super(domainObject, stateTransitionIfAny, transitionType);
        }
    }

    @Override
    public IncomingInvoiceApprovalTransitionEvent newStateTransitionEvent(
            final IncomingInvoice domainObject,
            final IncomingInvoiceApprovalStateTransition pendingTransitionIfAny) {
        return new IncomingInvoiceApprovalTransitionEvent(domainObject, pendingTransitionIfAny, this);
    }

    @Override
    public boolean canApply(
            final IncomingInvoice domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        // can never apply the initial pseudo approval
        return getFromStates() != null;
    }

    @Override
    public void applyTo(
            final IncomingInvoice domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        // nothing to do....
    }

    /**
     * No {@link Task} will be created unless this method is overridden.
     *
     * @param serviceRegistry2 -to lookup domain services etc
     */
    @Programmatic
    @Override
    public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
        return null;
    }

    @Override
    public IncomingInvoiceApprovalStateTransition createTransition(
            final IncomingInvoice domainObject,
            final IncomingInvoiceApprovalState fromState,
            final ServiceRegistry2 serviceRegistry2) {

        final IncomingInvoiceApprovalStateTransition.Repository repository =
                serviceRegistry2.lookupService(IncomingInvoiceApprovalStateTransition.Repository.class);

        final EstatioRole assignToIfAny = this.assignTaskTo(serviceRegistry2);

        final String taskDescription = Enums.getFriendlyNameOf(this);
        return repository.create(domainObject, this, fromState, assignToIfAny, taskDescription);
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends StateTransitionServiceSupportAbstract<
            IncomingInvoice, IncomingInvoiceApprovalStateTransition, IncomingInvoiceApprovalStateTransitionType, IncomingInvoiceApprovalState> {

        public SupportService() {
            super(IncomingInvoiceApprovalStateTransitionType.class, IncomingInvoiceApprovalStateTransition.class,
                    IncomingInvoiceApprovalState.NEW);
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


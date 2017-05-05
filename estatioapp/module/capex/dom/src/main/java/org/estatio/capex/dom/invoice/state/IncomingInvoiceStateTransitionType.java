package org.estatio.capex.dom.invoice.state;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum IncomingInvoiceStateTransitionType
        implements StateTransitionType<
                        IncomingInvoice,
                        IncomingInvoiceStateTransition,
                        IncomingInvoiceStateTransitionType,
                        IncomingInvoiceState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (IncomingInvoiceState)null,
            IncomingInvoiceState.NEW
    ),
    APPROVE_AS_PROJECT_MANAGER(
            IncomingInvoiceState.NEW,
            IncomingInvoiceState.APPROVED_BY_PROJECT_MANAGER
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
            IncomingInvoiceState.NEW,
            IncomingInvoiceState.APPROVED_BY_ASSET_MANAGER
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
                    IncomingInvoiceState.APPROVED_BY_PROJECT_MANAGER,
                    IncomingInvoiceState.APPROVED_BY_ASSET_MANAGER),
            IncomingInvoiceState.APPROVED_BY_COUNTRY_DIRECTOR
    ) {
        @Override
        public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
            return EstatioRole.COUNTRY_DIRECTOR;
        }
    },
    APPROVE_AS_TREASURER(
            IncomingInvoiceState.APPROVED_BY_COUNTRY_DIRECTOR,
            IncomingInvoiceState.APPROVED_BY_TREASURER
    ) {
        @Override
        public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
            return EstatioRole.TREASURER;
        }
    },
    PAY(
            IncomingInvoiceState.APPROVED_BY_TREASURER,
            IncomingInvoiceState.PAID
    ) {
        @Override
        public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
            return EstatioRole.TREASURER;
        }
    },
    CANCEL(
            Arrays.asList(
                    IncomingInvoiceState.APPROVED_BY_PROJECT_MANAGER,
                    IncomingInvoiceState.APPROVED_BY_ASSET_MANAGER,
                    IncomingInvoiceState.APPROVED_BY_COUNTRY_DIRECTOR,
                    IncomingInvoiceState.APPROVED_BY_TREASURER),
            IncomingInvoiceState.CANCELLED
    );

    private final List<IncomingInvoiceState> fromStates;
    private final IncomingInvoiceState toState;

    IncomingInvoiceStateTransitionType(
            final List<IncomingInvoiceState> fromState,
            final IncomingInvoiceState toState) {
        this.fromStates = fromState;
        this.toState = toState;
    }

    IncomingInvoiceStateTransitionType(
            final IncomingInvoiceState fromState,
            final IncomingInvoiceState toState
    ) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState);
    }

    @Override
    public boolean canApply(
            final IncomingInvoice domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        // can never apply the initial pseudo state
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
    public IncomingInvoiceStateTransition createTransition(
            final IncomingInvoice domainObject,
            final IncomingInvoiceState fromState,
            final ServiceRegistry2 serviceRegistry2) {

        final IncomingInvoiceStateTransitionRepository repository =
                serviceRegistry2.lookupService(IncomingInvoiceStateTransitionRepository.class);

        final EstatioRole assignToIfAny = this.assignTaskTo(serviceRegistry2);

        final String taskDescription = Enums.getFriendlyNameOf(this);
        return repository.create(domainObject, this, fromState, assignToIfAny, taskDescription);
    }

}

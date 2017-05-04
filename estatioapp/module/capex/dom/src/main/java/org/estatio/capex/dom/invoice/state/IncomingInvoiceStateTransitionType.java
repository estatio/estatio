package org.estatio.capex.dom.invoice.state;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.metamodel.MetaModelService3;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.task.IncomingInvoiceStateTransition;
import org.estatio.capex.dom.invoice.task.IncomingInvoiceStateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupport;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum IncomingInvoiceStateTransitionType
        implements StateTransitionType<
                IncomingInvoice, IncomingInvoiceStateTransition, IncomingInvoiceStateTransitionType, IncomingInvoiceState> {

    INSTANTIATING(
            (IncomingInvoiceState)null,
            IncomingInvoiceState.NEW
    ),
    APPROVE_AS_PROJECT_MANAGER(
            IncomingInvoiceState.NEW,
            IncomingInvoiceState.APPROVED_BY_PROJECT_MANAGER
    ) {

        @Override
        public EstatioRole assignTaskTo() {
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
        public EstatioRole assignTaskTo() {
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
        public EstatioRole assignTaskTo() {
            return EstatioRole.COUNTRY_DIRECTOR;
        }
    },
    APPROVE_AS_TREASURER(
            IncomingInvoiceState.APPROVED_BY_COUNTRY_DIRECTOR,
            IncomingInvoiceState.APPROVED_BY_TREASURER
    ) {
        @Override
        public EstatioRole assignTaskTo() {
            return EstatioRole.TREASURER;
        }
    },
    PAY(
            IncomingInvoiceState.APPROVED_BY_TREASURER,
            IncomingInvoiceState.PAID
    ) {
        @Override
        public EstatioRole assignTaskTo() {
            return EstatioRole.TREASURER;
        }
    },
    CANCEL(
            (IncomingInvoiceState)null,
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
        this(Collections.singletonList(fromState), toState);
    }

    /**
     * No {@link Task} will be created unless this method is overridden.
     */
    @Programmatic
    @Override
    public EstatioRole assignTaskTo() {
        return null;
    }

    @Override
    public boolean canApply(
            final IncomingInvoice domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        return true;
    }

    @Override
    public IncomingInvoiceState currentStateOf(final IncomingInvoice domainObject) {
        return domainObject.getIncomingInvoiceState();
    }

    @Override
    public void applyTo(final IncomingInvoice domainObject) {
        domainObject.setIncomingInvoiceState(getToState());
    }

    @Override
    public IncomingInvoiceStateTransition createTransition(
            final IncomingInvoice incomingInvoice,
            final ServiceRegistry2 serviceRegistry2) {

        final IncomingInvoiceStateTransitionRepository repository =
                serviceRegistry2.lookupService(IncomingInvoiceStateTransitionRepository.class);

        final EstatioRole assignTo = this.assignTaskTo();
        if(assignTo == null) {
            return null;
        }

        return repository.create(incomingInvoice, this, assignTo, Enums.getFriendlyNameOf(this));
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService implements StateTransitionServiceSupport<
            IncomingInvoice, IncomingInvoiceStateTransition, IncomingInvoiceStateTransitionType, IncomingInvoiceState> {

        @Override
        public boolean supports(final StateTransitionType<?, ?, ?, ?> transitionType) {
            return transitionType instanceof IncomingInvoiceStateTransitionType;
        }

        @Override
        public boolean supports(final String transitionType) {
            String objectType = metaModelServicwe3.toObjectType(IncomingInvoiceStateTransition.class);
            return Objects.equals(objectType, transitionType);
        }

        @Override
        public IncomingInvoiceStateTransitionType[] allTransitionTypes() {
            return IncomingInvoiceStateTransitionType.values();
        }

        @Override
        public IncomingInvoiceStateTransition findIncomplete(
                final IncomingInvoice incomingInvoice,
                final IncomingInvoiceStateTransitionType transitionType) {
            return repository.findByInvoiceAndTransitionTypeAndTaskCompleted(incomingInvoice, transitionType, false);
        }

        @Override public IncomingInvoiceStateTransition findFor(final Task task) {
            return null;
        }

        @Inject
        private IncomingInvoiceStateTransitionRepository repository;

        @Inject
        private MetaModelService3 metaModelServicwe3;

    }

}

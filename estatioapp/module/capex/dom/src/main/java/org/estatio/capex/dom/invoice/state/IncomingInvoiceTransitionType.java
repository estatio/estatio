package org.estatio.capex.dom.invoice.state;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.task.IncomingInvoice_newTask;
import org.estatio.capex.dom.invoice.task.StateTransitionForIncomingInvoice;
import org.estatio.capex.dom.invoice.task.TaskForIncomingInvoiceRepository;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum IncomingInvoiceTransitionType
        implements StateTransitionType<IncomingInvoice, IncomingInvoiceTransitionType, IncomingInvoiceState> {

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
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
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
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
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

    IncomingInvoiceTransitionType(
            final List<IncomingInvoiceState> fromState,
            final IncomingInvoiceState toState) {
        this.fromStates = fromState;
        this.toState = toState;
    }

    IncomingInvoiceTransitionType(
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
    public StateTransitionForIncomingInvoice createTask(
            final IncomingInvoice domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        final WrapperFactory wrapperFactory = serviceRegistry2.lookupService(WrapperFactory.class);
        final FactoryService factoryService = serviceRegistry2.lookupService(FactoryService.class);
        final EstatioRole taskRoleRequiredIfAny = this.assignTaskTo();
        return taskRoleRequiredIfAny != null
                    ? wrapperFactory.wrap(factoryService.mixin(IncomingInvoice_newTask.class, domainObject))
                                    .newTask(taskRoleRequiredIfAny, this, "")
                    : null;
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TaskForIncomingInvoiceCompletionSubscriber
            extends TaskCompletionSubscriberAbstract<IncomingInvoice, IncomingInvoiceTransitionType, IncomingInvoiceState> {

        @Override
        protected List<Task<?, IncomingInvoice, IncomingInvoiceTransitionType, IncomingInvoiceState>> findTasksByDomainObjectAndTransition(
                final IncomingInvoice domainObject, final IncomingInvoiceTransitionType taskTransition) {
            return (List)repository.findByInvoiceAndTransition(domainObject, taskTransition);
        }

        @Inject
        TaskForIncomingInvoiceRepository repository;
    }

    @Override
    public List<IncomingInvoiceTransitionType> allValues() {
        return Arrays.asList(values());
    }

}

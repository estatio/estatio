package org.estatio.capex.dom.invoice.state;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.task.IncomingInvoice_newTask;
import org.estatio.capex.dom.invoice.task.TaskForIncomingInvoice;
import org.estatio.capex.dom.invoice.task.TaskForIncomingInvoiceRepository;
import org.estatio.capex.dom.task.Task;
import org.estatio.capex.dom.task.TaskTransition;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum IncomingInvoiceTransition
        implements TaskTransition<IncomingInvoice, IncomingInvoiceTransition, IncomingInvoiceState> {

    INSTANTIATING(
            (IncomingInvoiceState)null,
            IncomingInvoiceState.NEW,
            null),
    APPROVE_AS_PROJECT_MANAGER(
            IncomingInvoiceState.NEW,
            IncomingInvoiceState.APPROVED_BY_PROJECT_MANAGER,
            EstatioRole.PROJECT_MANAGER) {

        @Override
        public boolean canApply(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return domainObject.hasProject();
        }

        @Override
        public TaskForIncomingInvoice createTask(
                final IncomingInvoice domainObject,
                final ServiceRegistry2 serviceRegistry2) {
            // TODO: customise the generated task, eg adding in the fixed project somehow
            return super.createTask(domainObject, serviceRegistry2);
        }
    },
    APPROVE_AS_ASSET_MANAGER(
            IncomingInvoiceState.NEW,
            IncomingInvoiceState.APPROVED_BY_ASSET_MANAGER,
            EstatioRole.ASSET_MANAGER) {

        @Override
        public boolean canApply(
                final IncomingInvoice domainObject, final ServiceRegistry2 serviceRegistry2) {
            return !domainObject.hasProject() && domainObject.hasFixedAsset();
        }

        @Override
        public TaskForIncomingInvoice createTask(
                final IncomingInvoice domainObject,
                final ServiceRegistry2 serviceRegistry2) {
            // TODO: customise the generated task, eg adding in the fixed asset somehow
            return super.createTask(domainObject, serviceRegistry2);
        }
    },
    APPROVE_AS_COUNTRY_DIRECTOR(
            Arrays.asList(
                    IncomingInvoiceState.APPROVED_BY_PROJECT_MANAGER,
                    IncomingInvoiceState.APPROVED_BY_ASSET_MANAGER),
            IncomingInvoiceState.APPROVED_BY_COUNTRY_DIRECTOR,
            EstatioRole.COUNTRY_DIRECTOR),
    APPROVE_AS_TREASURER(
            IncomingInvoiceState.APPROVED_BY_COUNTRY_DIRECTOR,
            IncomingInvoiceState.APPROVED_BY_TREASURER,
            EstatioRole.TREASURER),
    PAY(
            IncomingInvoiceState.APPROVED_BY_TREASURER,
            IncomingInvoiceState.PAID,
            EstatioRole.TREASURER),
    CANCEL(
            (IncomingInvoiceState)null,
            IncomingInvoiceState.CANCELLED,
            null);

    private final List<IncomingInvoiceState> fromStates;
    private final IncomingInvoiceState toState;
    private final EstatioRole taskRoleRequiredIfAny;

    IncomingInvoiceTransition(
            final List<IncomingInvoiceState> fromState,
            final IncomingInvoiceState toState,
            final EstatioRole taskRoleRequiredIfAny) {
        this.fromStates = fromState;
        this.toState = toState;
        this.taskRoleRequiredIfAny = taskRoleRequiredIfAny;
    }

    IncomingInvoiceTransition(
            final IncomingInvoiceState fromState,
            final IncomingInvoiceState toState,
            final EstatioRole taskRoleRequiredIfAny
    ) {
        this(Collections.singletonList(fromState), toState, taskRoleRequiredIfAny);
    }

    @Override
    public boolean canApply(
            final IncomingInvoice domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        return true;
    }

    @Override
    public TaskForIncomingInvoice createTask(
            final IncomingInvoice domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        final WrapperFactory wrapperFactory = serviceRegistry2.lookupService(WrapperFactory.class);
        final FactoryService factoryService = serviceRegistry2.lookupService(FactoryService.class);
        final EstatioRole taskRoleRequiredIfAny = this.getTaskRoleRequiredIfAny();
        return taskRoleRequiredIfAny != null
                    ? wrapperFactory.wrap(factoryService.mixin(IncomingInvoice_newTask.class, domainObject))
                                    .newTask(taskRoleRequiredIfAny, this, "")
                    : null;
    }


    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TaskForIncomingInvoiceCompletionSubscriber
            extends TaskCompletionSubscriberAbstract<IncomingInvoice, IncomingInvoiceTransition, IncomingInvoiceState> {

        @Override
        protected List<Task<?, IncomingInvoice, IncomingInvoiceTransition, IncomingInvoiceState>> findTasksByDomainObjectAndTransition(
                final IncomingInvoice domainObject, final IncomingInvoiceTransition taskTransition) {
            return (List)repository.findByInvoiceAndTransition(domainObject, taskTransition);
        }

        @Inject
        TaskForIncomingInvoiceRepository repository;
    }

    @Override
    public List<IncomingInvoiceTransition> allValues() {
        return Arrays.asList(values());
    }

}

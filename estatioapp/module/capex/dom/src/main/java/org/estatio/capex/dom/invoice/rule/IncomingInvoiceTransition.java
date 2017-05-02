package org.estatio.capex.dom.invoice.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.task.IncomingInvoice_newTask;
import org.estatio.capex.dom.invoice.task.NewTaskMixin;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum IncomingInvoiceTransition
        implements TaskTransition<IncomingInvoice, IncomingInvoiceState, IncomingInvoiceTransition> {

    INSTANTIATING(
            (IncomingInvoiceState)null,
            IncomingInvoiceState.NEW,
            null),
    APPROVE_AS_PROJECT_MANAGER(
            IncomingInvoiceState.NEW,
            IncomingInvoiceState.APPROVED_BY_PROJECT_MANAGER,
            EstatioRole.PROJECT_MANAGER) {

        @Override
        public Task<?> newTaskIfApplicable(
                final IncomingInvoice domainObject,
                final WrapperFactory wrapperFactory,
                final FactoryService factoryService) {
            return domainObject.hasProject()
                    ? super.newTaskIfApplicable(domainObject, wrapperFactory, factoryService)
                    : null;
        }
    },
    APPROVE_AS_ASSET_MANAGER(
            IncomingInvoiceState.NEW,
            IncomingInvoiceState.APPROVED_BY_ASSET_MANAGER,
            EstatioRole.ASSET_MANAGER) {
        @Override
        public Task<?> newTaskIfApplicable(
                final IncomingInvoice domainObject,
                final WrapperFactory wrapperFactory,
                final FactoryService factoryService) {
            return !domainObject.hasProject() && domainObject.hasFixedAsset()
                    ? super.newTaskIfApplicable(domainObject, wrapperFactory, factoryService)
                    : null;
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
    public Task<?> newTaskIfApplicable(
            final IncomingInvoice domainObject,
            final WrapperFactory wrapperFactory,
            final FactoryService factoryService) {
        final EstatioRole taskRoleRequiredIfAny = this.getTaskRoleRequiredIfAny();
        return taskRoleRequiredIfAny != null
                    ? wrapperFactory.wrap(factoryService.mixin(IncomingInvoice_newTask.class, domainObject))
                                    .newTask(taskRoleRequiredIfAny, "", this)
                    : null;
    }

    @Override
    public void preApply(
            final IncomingInvoice domainObject,
            final TaskTransition<IncomingInvoice, IncomingInvoiceState, IncomingInvoiceTransition> transition) {
    }

    @Override
    public void postApply(
            final IncomingInvoice domainObject,
            final TaskTransition<IncomingInvoice, IncomingInvoiceState, IncomingInvoiceTransition> transition) {
    }

    @Override
    public List<IncomingInvoiceTransition> allValues() {
        return Arrays.asList(values());
    }

}

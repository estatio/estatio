package org.estatio.capex.dom.invoice.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.task.IncomingInvoice_newTask;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum IncomingInvoiceTransition {

    INSTANTIATING(
            (IncomingInvoiceState)null,
            IncomingInvoiceState.NEW,
            null),
    APPROVE_AS_PROJECT_MANAGER(
            IncomingInvoiceState.NEW,
            IncomingInvoiceState.APPROVED_BY_PROJECT_MANAGER,
            EstatioRole.PROJECT_MANAGER),
    APPROVE_AS_ASSET_MANAGER(
            IncomingInvoiceState.NEW,
            IncomingInvoiceState.APPROVED_BY_ASSET_MANAGER,
            EstatioRole.ASSET_MANAGER),
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

    public void apply(
            final IncomingInvoice invoice,
            final WrapperFactory wrapperFactory,
            final FactoryService factoryService) {

        // transition the domain object to its next state
        invoice.setIncomingInvoiceState(toState);

        // for wherever we might go next, create a task if it needs one.
        // TODO: we will add some further preconditions so that only a single transition returns
        final List<IncomingInvoiceTransition> transitions =
                taskTransitionsFrom(toState);
        for (IncomingInvoiceTransition transition : transitions) {
            wrapperFactory.wrap(
                    factoryService.mixin(IncomingInvoice_newTask.class, invoice))
                    .newTask(transition.taskRoleRequiredIfAny, "");
        }
    }

    public boolean isFromState(final IncomingInvoiceState incomingInvoiceState) {
        return fromStates == null || fromStates.contains(incomingInvoiceState);
    }

    @Programmatic
    public List<IncomingInvoiceTransition> transitionsFrom(IncomingInvoiceState fromState) {
        List<IncomingInvoiceTransition> transitions = Lists.newArrayList();
        final IncomingInvoiceTransition[] values = values();
        for (IncomingInvoiceTransition value : values) {
            if(value.fromStates == null || value.fromStates.contains(fromState)) {
                transitions.add(value);
            }
        }
        return transitions;
    }

    @Programmatic
    public List<IncomingInvoiceTransition> taskTransitionsFrom(IncomingInvoiceState fromState) {
        List<IncomingInvoiceTransition> transitions = Lists.newArrayList();
        final IncomingInvoiceTransition[] values = values();
        for (IncomingInvoiceTransition value : values) {
            if(value.fromStates == null || value.fromStates.contains(fromState)) {
                // TODO: perhaps remove some duplication using a Predicate ?
                if(value.taskRoleRequiredIfAny != null) {
                    transitions.add(value);
                }
            }
        }
        return transitions;
    }

}

package org.estatio.capex.dom.invoice.payment.approval;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.util.Enums;

import org.estatio.capex.dom.invoice.payment.Payment;
import org.estatio.capex.dom.state.StateTransitionEvent;
import org.estatio.capex.dom.state.StateTransitionRepository;
import org.estatio.capex.dom.state.StateTransitionServiceSupportAbstract;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.task.Task;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;

@Getter
public enum PaymentApprovalStateTransitionType
        implements StateTransitionType<
                Payment,
                PaymentApprovalStateTransition,
                PaymentApprovalStateTransitionType,
                PaymentApprovalState> {

    // a "pseudo" transition type; won't ever see this persisted as a state transition
    INSTANTIATE(
            (PaymentApprovalState)null,
            PaymentApprovalState.NEW
    ),
    APPROVE_AS_TREASURER(
            PaymentApprovalState.NEW,
            PaymentApprovalState.APPROVED_BY_TREASURER
    ) {
        @Override
        public EstatioRole assignTaskTo(final ServiceRegistry2 serviceRegistry2) {
            return EstatioRole.TREASURER;
        }
    },
    CANCEL(
            PaymentApprovalState.NEW,
            PaymentApprovalState.CANCELLED
    );

    private final List<PaymentApprovalState> fromStates;
    private final PaymentApprovalState toState;

    PaymentApprovalStateTransitionType(
            final List<PaymentApprovalState> fromState,
            final PaymentApprovalState toState) {
        this.fromStates = fromState;
        this.toState = toState;
    }

    PaymentApprovalStateTransitionType(
            final PaymentApprovalState fromState,
            final PaymentApprovalState toState) {
        this(fromState != null ? Collections.singletonList(fromState): null, toState);
    }

    public static class PaymentApprovalTransitionEvent
            extends StateTransitionEvent<
                        Payment,
                        PaymentApprovalStateTransition,
                        PaymentApprovalStateTransitionType,
                        PaymentApprovalState> {
        public PaymentApprovalTransitionEvent(
                final Payment domainObject,
                final PaymentApprovalStateTransition stateTransitionIfAny,
                final PaymentApprovalStateTransitionType transitionType) {
            super(domainObject, stateTransitionIfAny, transitionType);
        }
    }

    @Override
    public PaymentApprovalTransitionEvent newStateTransitionEvent(
            final Payment domainObject,
            final PaymentApprovalStateTransition pendingTransitionIfAny) {
        return new PaymentApprovalTransitionEvent(domainObject, pendingTransitionIfAny, this);
    }

    @Override
    public boolean canApply(
            final Payment domainObject,
            final ServiceRegistry2 serviceRegistry2) {
        // can never apply the initial pseudo approval
        return getFromStates() != null;
    }

    @Override
    public void applyTo(
            final Payment domainObject,
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
    public PaymentApprovalStateTransition createTransition(
            final Payment domainObject,
            final PaymentApprovalState fromState,
            final ServiceRegistry2 serviceRegistry2) {

        final PaymentApprovalStateTransition.Repository repository =
                serviceRegistry2.lookupService(PaymentApprovalStateTransition.Repository.class);

        final EstatioRole assignToIfAny = this.assignTaskTo(serviceRegistry2);

        final String taskDescription = Enums.getFriendlyNameOf(this);
        return repository.create(domainObject, this, fromState, assignToIfAny, taskDescription);
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService extends StateTransitionServiceSupportAbstract<
            Payment, PaymentApprovalStateTransition, PaymentApprovalStateTransitionType, PaymentApprovalState> {

        public SupportService() {
            super(PaymentApprovalStateTransitionType.class, PaymentApprovalStateTransition.class,
                    PaymentApprovalState.NEW);
        }

        @Override
        protected StateTransitionRepository<
                Payment,
                PaymentApprovalStateTransition,
                PaymentApprovalStateTransitionType,
                PaymentApprovalState
                > getRepository() {
            return repository;
        }

        @Inject
        PaymentApprovalStateTransition.Repository repository;

    }

}

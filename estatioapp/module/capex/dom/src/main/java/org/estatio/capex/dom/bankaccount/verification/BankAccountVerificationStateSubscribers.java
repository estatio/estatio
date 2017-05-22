package org.estatio.capex.dom.bankaccount.verification;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;

import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.financial.bankaccount.BankAccount;

@DomainService(nature = NatureOfService.DOMAIN)
public class BankAccountVerificationStateSubscribers extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    public void toInstantiateWhen(BankAccount.PersistedLifecycleEvent ev) {
        stateTransitionService.apply(ev.getSource(), BankAccountVerificationStateTransitionType.INSTANTIATE, null);
    }

    @com.google.common.eventbus.Subscribe
    public void toResetWhen(BankAccount.ChangeEvent ev) {
        if(ev.getEventPhase() == AbstractDomainEvent.Phase.EXECUTED) {
            stateTransitionService.apply(ev.getSource(), BankAccountVerificationStateTransitionType.RESET, null);
        }
    }

    @Inject
    StateTransitionService stateTransitionService;

}

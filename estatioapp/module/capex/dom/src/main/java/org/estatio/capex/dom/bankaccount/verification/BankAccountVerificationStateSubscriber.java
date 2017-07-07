package org.estatio.capex.dom.bankaccount.verification;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;

import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.financial.bankaccount.BankAccount;

@DomainService(nature = NatureOfService.DOMAIN)
public class BankAccountVerificationStateSubscriber extends AbstractSubscriber {

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toInstantiateWhen(BankAccount.PersistedLifecycleEvent ev) {
        stateTransitionService.trigger(ev.getSource(), BankAccountVerificationStateTransitionType.INSTANTIATE, null, null);
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toDeleteWhen(BankAccount.RemovingLifecycleEvent ev) {
        repository.deleteFor(ev.getSource());
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void toResetWhen(BankAccount.ChangeEvent ev) {
        if(ev.getEventPhase() == AbstractDomainEvent.Phase.EXECUTED) {
            stateTransitionService.trigger(ev.getSource(), BankAccountVerificationStateTransitionType.RESET, null, null);
        }
    }

    @Inject
    StateTransitionService stateTransitionService;
    @Inject
    BankAccountVerificationStateTransition.Repository repository;

}

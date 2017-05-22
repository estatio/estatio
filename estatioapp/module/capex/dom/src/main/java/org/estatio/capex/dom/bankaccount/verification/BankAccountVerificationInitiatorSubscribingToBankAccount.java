package org.estatio.capex.dom.bankaccount.verification;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.eventbus.AbstractDomainEvent;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;

import org.estatio.capex.dom.state.StateTransitionService;
import org.estatio.dom.financial.bankaccount.BankAccount;

@DomainService(nature = NatureOfService.DOMAIN)
public class BankAccountVerificationInitiatorSubscribingToBankAccount extends AbstractSubscriber {

    @com.google.common.eventbus.Subscribe
    public void on(BankAccount.CreateEvent ev) {
        doOn(ev);
    }

    @com.google.common.eventbus.Subscribe
    public void on(BankAccount.ChangeEvent ev) {
        doOn(ev);
    }

    private void doOn(ActionDomainEvent<BankAccount> ev){
        final AbstractDomainEvent.Phase phase = ev.getEventPhase();
        if (phase == AbstractDomainEvent.Phase.EXECUTED){
            BankAccount bankAccount = ev.getSource();
            stateTransitionService.apply(bankAccount, BankAccountVerificationStateTransitionType.INSTANTIATE, null);
        }
    }

    @Inject
    StateTransitionService stateTransitionService;

}

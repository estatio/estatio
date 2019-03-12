package org.estatio.module.capex.dom.invoice.approval;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.state.StateTransitionEvent;
import org.estatio.module.capex.dom.state.StateTransitionService;
import org.estatio.module.financial.dom.BankAccount;

import static org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType.CONFIRM_BANK_ACCOUNT_VERIFIED;
import static org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalStateTransitionType.REJECT;

@DomainService(nature = NatureOfService.DOMAIN)
public class IncomingInvoiceApprovalStateSubscriber extends AbstractSubscriber {

    /**
     * We can't mutate the state of the incoming invoice in an ObjectPersistedEVent callback, we must do it here.
     * <p>
     * Note that the incoming invoice at this stage has no items attached to it, so there is a limit as to what
     * we can safely do.  However, it *is* ok to just create the state chart for the invoice.
     */
    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(IncomingInvoice.ObjectPersistingEvent ev) {
        final IncomingInvoice incomingInvoice = ev.getSource();

        // for the OrderInvoiceLine import from existing spreadsheets, will be set to PAID, so do nothing
        if (incomingInvoice.getApprovalState() == null) {
            incomingInvoice.setApprovalState(IncomingInvoiceApprovalStateTransitionType.INSTANTIATE.getToState());
        }
    }

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(BankAccountVerificationStateTransitionType.TransitionEvent ev) {
        final StateTransitionEvent.Phase phase = ev.getPhase();
        if (phase == StateTransitionEvent.Phase.TRANSITIONED) {
            final BankAccountVerificationStateTransitionType transitionType = ev.getTransitionType();
            final BankAccount bankAccount = ev.getDomainObject();
            final List<IncomingInvoice> incomingInvoices = findIncomingInvoicesUsing(bankAccount);

            switch (transitionType) {

                case INSTANTIATE:
                    break;

                case VERIFY_BANK_ACCOUNT:
                    incomingInvoices.forEach(incomingInvoice -> stateTransitionService.trigger(incomingInvoice, CONFIRM_BANK_ACCOUNT_VERIFIED, null, null));
                    break;
                case REJECT_PROOF:
                    break;
                case PROOF_UPDATED:
                    break;
                case RESET:
                    break;
                case DISCARD:
                    incomingInvoices.forEach(incomingInvoice -> stateTransitionService.trigger(incomingInvoice, REJECT, "Bank account discarded, please update", "Bank account discarded, please update"));
                    break;
            }
        }
    }

    private List<IncomingInvoice> findIncomingInvoicesUsing(final BankAccount bankAccount) {
        return incomingInvoiceRepository.findByBankAccount(bankAccount);
    }

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    StateTransitionService stateTransitionService;

}

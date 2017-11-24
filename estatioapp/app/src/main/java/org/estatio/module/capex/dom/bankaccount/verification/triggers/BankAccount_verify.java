package org.estatio.module.capex.dom.bankaccount.verification.triggers;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.module.capex.contributions.BankAccount_attachPdfAsIbanProof;
import org.estatio.module.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.module.financial.dom.BankAccount;

/**
 * This cannot be inlined (needs to be a mixin) because BankAccount does not know abouts its verification state machine
 */
@Mixin(method="act")
public class BankAccount_verify extends BankAccount_triggerAbstract {

    private final BankAccount bankAccount;

    public BankAccount_verify(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.VERIFY_BANK_ACCOUNT);
        this.bankAccount = bankAccount;
    }

    public static class ActionDomainEvent extends BankAccount_triggerAbstract.ActionDomainEvent<BankAccount_verify> {}



    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @MemberOrder(sequence = "9")
    public BankAccount act(
            @Nullable final String comment) {
        trigger(comment, null);
        return getDomainObject();
    }

    public boolean hideAct() {
        return cannotTransition();
    }

    public String disableAct() {
        if(bankAccount.getBic() == null) {
            return "BIC is required";
        }

        final List<Paperclip> paperclips =
                paperclipRepository.findByAttachedToAndRoleName(
                        bankAccount, BankAccount_attachPdfAsIbanProof.ROLE_NAME_FOR_IBAN_PROOF);

        if(paperclips.isEmpty()) {
            return "IBAN proof must first be attached";
        }
        return reasonGuardNotSatisified();
    }


    @Inject
    PaperclipRepository paperclipRepository;
}

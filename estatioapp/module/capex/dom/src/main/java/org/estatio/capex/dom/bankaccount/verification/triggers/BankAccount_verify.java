package org.estatio.capex.dom.bankaccount.verification.triggers;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import org.estatio.capex.dom.EstatioCapexDomModule;
import org.estatio.capex.dom.bankaccount.documents.BankAccount_attachPdfAsVerificationProof;
import org.estatio.capex.dom.bankaccount.verification.BankAccountVerificationStateTransitionType;
import org.estatio.dom.financial.bankaccount.BankAccount;

@Mixin(method="act")
public class BankAccount_verify extends BankAccount_triggerAbstract {

    private final BankAccount bankAccount;

    public BankAccount_verify(BankAccount bankAccount) {
        super(bankAccount, BankAccountVerificationStateTransitionType.VERIFY_BANK_ACCOUNT);
        this.bankAccount = bankAccount;
    }

    public static class DomainEvent extends EstatioCapexDomModule.ActionDomainEvent<BankAccount_verify> {}

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = DomainEvent.class
    )
    @MemberOrder(sequence = "9")
    public BankAccount act(@Nullable final String comment) {
        return super.act(comment);
    }

    public boolean hideAct() {
        return super.hideAct();
    }

    @Override
    public String disableAct() {
        if(bankAccount.getBic() == null) {
            return "BIC is required";
        }

        final List<Paperclip> paperclips =
                paperclipRepository.findByAttachedToAndRoleName(
                        bankAccount, BankAccount_attachPdfAsVerificationProof.ROLE_NAME_FOR_IBAN_PROOF);

        if(paperclips.isEmpty()) {
            return "IBAN proof must first be attached";
        }
        return super.disableAct();
    }


    @Inject
    PaperclipRepository paperclipRepository;
}
